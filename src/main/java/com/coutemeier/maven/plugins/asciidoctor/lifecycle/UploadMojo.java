package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.WagonUtil;

import java.io.File;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Server;
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
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ComponentConfigurator;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * Uploads the generate files using <a href="https://maven.apache.org/wagon/">wagon supported protocols</a> to the
 * Asciidoctor repository URL specified by the {@link #uploadToRepository} parameter.
 *
 * @author rrialq
 * @since 1.0
 */
@Mojo(name = "upload", requiresProject = true, threadSafe = true)
public class UploadMojo extends AbstractAsciidoctorLifecycleMojo implements Contextualizable {
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
     * The repository to which you want to upload the files
     * <p>
     * The repository can be any URL supported by wagon, for example:
     * {@code dav:http://localhost:8081/nexus/content/sites/test-site/} or {@code file:///tmp/file-repository}
     *
     * @since 1.0
     */
    @Parameter(property = GOAL_PREFIX + "upload.repository", required = true)
    private String uploadToRepository;

    /**
     * The directory in the repository to which upload the files
     *
     * @since 1.0
     */
    @Parameter(property = GOAL_PREFIX + "upload.directory", defaultValue = "${project.artifactId}/${project.version}", required = true)
    private String uploadToDirectory;

    /**
     * The id of the server for providing credentials
     *
     * @since 1.0
     */
    @Parameter(property = GOAL_PREFIX + "upload.serverId", required = false)
    private String serverId;

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        uploadTo(new Repository(this.serverId, this.uploadToRepository));
    }

    private final void uploadTo( final Repository repository ) throws MojoExecutionException {
        if (!this.inputDirectory.exists()) {
            throw new MojoExecutionException("The Asciidoctor generated files directory does not exists. Please, run build first.");
        }

        if (this.debugEnabled) {
            getLog().debug("Uploading to '" + this.uploadToRepository + "' , using credentials from server id '"
                    + this.serverId + "'.");
        }

        upload(inputDirectory, repository);
    }

    private final void upload(final File directory, final Repository repository) throws MojoExecutionException {
        final Wagon wagon = WagonUtil.getWagon(container, getLog(), repository, wagonManager);

        try {
            WagonUtil.configureWagon( wagon, repository.getId(), settings, container, getLog() );
            final SettingsDecrypter settingsDecrypter = (SettingsDecrypter) container.lookup( SettingsDecrypter.class );
            final ProxyInfo proxyInfo = WagonUtil.getProxyInfo( this.mavenSession, this.getLog(), repository, settingsDecrypter );
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
            getLog().info( "Pushing " + inputDirectory );

            wagon.putDirectory( inputDirectory, this.uploadToDirectory );

        } catch (
            final ResourceDoesNotExistException
                | TransferFailedException
                | AuthorizationException
                | ConnectionException
                |  AuthenticationException cause ) {
            throw new MojoExecutionException("Error uploading Asciidoctor documents to server: ", cause );
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
