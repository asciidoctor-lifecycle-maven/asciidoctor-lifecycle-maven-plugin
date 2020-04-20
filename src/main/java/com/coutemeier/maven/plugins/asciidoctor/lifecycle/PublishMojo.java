package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.WagonUtil;

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
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.apache.maven.wagon.observers.Debug;
import org.apache.maven.wagon.proxy.ProxyInfo;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

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
    @Parameter(property = GOAL_PREFIX + "inputDirectory", defaultValue = "${project.build.directory}/generated-docs", required = true)
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
    @Parameter(property = GOAL_PREFIX + "publish.directory", defaultValue = "${project.artifactId}/${project.version}", required = true)
    private String publishToDirectory;

    /**
     * The id of the server for providing credentials
     *
     * @since 1.0
     */
    @Parameter(property = GOAL_PREFIX + "publish.serverId", required = false)
    private String serverId;

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        publishTo(new Repository(this.serverId, this.publishToRepository));
    }

    private final void publishTo( final Repository repository ) throws MojoExecutionException {
        if (!this.inputDirectory.exists()) {
            throw new MojoExecutionException("The Asciidoctor generated files directory does not exists. Please, run build first.");
        }

        if (this.debugEnabled) {
            getLog().debug("Publishing to '" + this.publishToRepository + "' , using credentials from server id '"
                    + this.serverId + "'.");
        }

        publish(inputDirectory, repository);
    }

    private final void publish(final File directory, final Repository repository) throws MojoExecutionException {
        final Wagon wagon = WagonUtil.getWagon(container, getLog(), repository, wagonManager);

        try {
            WagonUtil.configureWagon( wagon, repository.getId(), settings, container, getLog() );
//            final SettingsDecrypter settingsDecrypter = (SettingsDecrypter) container.lookup( SettingsDecrypter.class );
            final ProxyInfo proxyInfo = WagonUtil.getProxyInfo( this.mavenSession, this.getLog(), repository, this.settingsDecrypter );
            push( directory, repository, wagon, proxyInfo );

        } catch (TransferFailedException cause) {
            throw new MojoExecutionException("Unable to configure Wagon: '" + repository.getProtocol() + "'", cause);
        } catch (ComponentLookupException cause ) {
            throw new MojoExecutionException( "Unable to lookup SettingsDecrypter: " + cause.getMessage(), cause );
        }
    }

    private final void push(final File directory, final Repository repository, final Wagon wagon, final ProxyInfo proxyInfo)
            throws MojoExecutionException {
        final AuthenticationInfo authenticationInfo = wagonManager.getAuthenticationInfo( repository.getId() );
        try {
            if( this.debugEnabled ) {
                getLog().debug( "authenticationInfo with id '" + repository.getId() + "': "
                                + ( ( authenticationInfo == null ) ? "-" : authenticationInfo.getUserName() ) );
                Debug debug = new Debug();
                wagon.addSessionListener( debug );
                wagon.addTransferListener( debug );
            }
            if ( proxyInfo != null ) {
                getLog().debug( "connect with proxyInfo" );
                wagon.connect( repository, authenticationInfo, proxyInfo );
            } else if ( proxyInfo == null && authenticationInfo != null ) {
                getLog().debug( "connect with authenticationInfo and without proxyInfo" );
                wagon.connect( repository, authenticationInfo );
            } else {
                getLog().debug( "connect without authenticationInfo and without proxyInfo" );
                wagon.connect( repository );
            }
            getLog().info( "Pushing " + directory );

            wagon.putDirectory( directory, this.publishToDirectory );

        } catch (
            final ResourceDoesNotExistException
                | TransferFailedException
                | AuthorizationException
                | ConnectionException
                |  AuthenticationException cause ) {
            throw new MojoExecutionException("Error publishing Asciidoctor documents to server: ", cause );
        } finally {
            try {
                wagon.disconnect();
            } catch (final ConnectionException cause) {
                getLog().error("Error disconnecting wagon - ignored", cause);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextualize( final Context context ) throws ContextException {
        container = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
    }
}
