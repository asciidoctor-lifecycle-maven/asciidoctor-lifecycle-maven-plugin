package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import java.io.File;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.UnsupportedProtocolException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.observers.Debug;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.WagonUtil;

/**
 * Publish the generate files using <a href="https://maven.apache.org/wagon/">wagon supported protocols</a> to the
 * Asciidoctor repository URL specified by the {@link #publishToRepository} parameter.
 *
 * @author rrialq
 * @since 1.0
 */
@Mojo(name = "asciidoctor-publish", requiresProject = true, threadSafe = true)
public class PublishMojo extends AbstractAsciidoctorLifecycleMojo implements Contextualizable {
    /**
     * The current user system settings for use in Maven.
     *
     * @since 1.0
     */
    @Parameter(defaultValue = "${settings}", readonly = true)
    private Settings settings;

    /**
     * The decrypter used to decrypt passwords in server.xml
     *
     * @since 1.0
     */
    @Component
    private SettingsDecrypter settingsDecrypter;

    @Component
    private WagonManager wagonManager;

    /**
     * The container
     *
     * @since 1.0
     */
    private PlexusContainer container;

    /**
     * The directory where Asciidoctor generates the files. The default value is
     * <code>${porject.build.directory/generated-docs}</code>.
     *
     * @since 1.0
     */
    @Parameter(property = GOAL_PREFIX + "inputDirectory", defaultValue = "${project.build.directory}/generated-docs", required = false)
    private File inputDirectory;

    /**
     * The repository to which you want to publish the files
     * <p>
     * The repository can be any URL supported by wagon, for example:
     * {@code dav:http://localhost:8081/nexus/content/sites/test-site/} or {@code file:///tmp/file-repository}
     *
     * @since 1.0
     */
    @Parameter(property = GOAL_PREFIX + "publish.repository", required = true)
    private String publishToRepository;

    /**
     * The directory in the repository to which publish the files
     *
     * @since 1.0
     */
    @Parameter(property = GOAL_PREFIX + "publish.directory", defaultValue = "${project.artifactId}/${project.version}", required = false)
    private String publishToDirectory;

    /**
     * The id of the server for providing credentials
     *
     * @since 1.0
     */
    @Parameter(property = GOAL_PREFIX + "publish.serverId", defaultValue="", required = false)
    private String serverId;

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        this.publishTo( new Repository(this.serverId, this.publishToRepository));
    }

    private final void publishTo( final Repository repository ) throws MojoExecutionException {
        if (!this.inputDirectory.exists()) {
            throw new MojoExecutionException( Messages.PUBLISH_ERROR_MISSING_GENERATED_FILES );
        }

        this.debugFormatted( Messages.PUBLISH_PUBLISHING_TO, this.publishToRepository, this.publishToDirectory, this.serverId );
        this.publish( this.inputDirectory, repository );
    }

    private final void publish( final File directory, final Repository repository )
    throws MojoExecutionException {
        try {
            final Wagon wagon = this.wagonManager.getWagon( repository );
            if ( !wagon.supportsDirectoryCopy() ) {
                throw new MojoExecutionException(
                    "Wagon protocol '" + repository.getProtocol() + "' doesn't support directory copying" );
            }
            WagonUtil.configureWagon( wagon, repository.getId(), this.settings, this.container, this.getLog() );
            final ProxyInfo proxyInfo = WagonUtil.getProxyInfo( this.mavenSession, this.getLog(), repository, this.settingsDecrypter );
            this.push( directory, repository, wagon, proxyInfo );

        } catch ( final TransferFailedException cause ) {
            final String message = String.format( Messages.PUBLISH_ERROR_UNABLE_TO_CONFIGURE_WAGON, repository.getProtocol() );
            this.getLog().error( message, cause );
            throw new MojoExecutionException( message, cause);
        } catch ( final UnsupportedProtocolException cause ) {
            final String shortMessage = "Unsupported protocol: '" + repository.getProtocol() + "' for documentation deployment to "
                + "url=" + repository.getUrl() + ".";
            final String longMessage =
                "\n" + shortMessage + "\n" + "Currently supported protocols are: "
                    + WagonUtil.getSupportedProtocols(this.container, this.getLog()) + ".\n"
                    + "    Protocols may be added through wagon providers.\n" + "    For more information, see "
                    + "http://maven.apache.org/plugins/maven-site-plugin/examples/adding-deploy-protocol.html";

            this.getLog().error( longMessage, cause );
            throw new MojoExecutionException( shortMessage );
        }
    }

    private final void push(final File directory, final Repository repository, final Wagon wagon, final ProxyInfo proxyInfo)
            throws MojoExecutionException {
        final String repositoryId = repository.getId();
        final AuthenticationInfo authenticationInfo = this.wagonManager.getAuthenticationInfo( repositoryId );

        try {
            if( this.getLog().isDebugEnabled() ) {
                Debug debug = new Debug();
                wagon.addSessionListener( debug );
                wagon.addTransferListener( debug );
            }
            if ( proxyInfo != null ) {
                this.debugMessage( Messages.PUBLISH_CONNECT_WITH_PROXY );
                wagon.connect( repository, authenticationInfo, proxyInfo );
            } else {
                this.debugMessage( Messages.PUBLISH_CONNECT_WITHOUT_PROXY );
                wagon.connect( repository, authenticationInfo );
            }
            this.infoFormatted( Messages.PUBLISH_PUSHING_DIRECTORY, directory, this.publishToDirectory );
            wagon.putDirectory( directory, this.publishToDirectory );

        } catch (
            final ResourceDoesNotExistException
                | TransferFailedException
                | AuthorizationException
                | ConnectionException
                | AuthenticationException cause ) {
            throw new MojoExecutionException( Messages.PUBLISH_ERROR_PUBLISHING_TO_SERVER, cause );
        } finally {
            try {
                wagon.disconnect();
            } catch (final ConnectionException cause) {
                this.getLog().error( Messages.PUBLISH_ERROR_DISCONNECTING_WAGON, cause);
            }
        }
    }

    @Override
    public void contextualize( final Context context ) throws ContextException {
        this.container = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
    }
}
