package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import java.io.File;
import java.util.NoSuchElementException;

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
import org.codehaus.plexus.util.xml.Xpp3Dom;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.SettingsUtil;
import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.WagonUtil;

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
    @Parameter(property = GOAL_PREFIX
            + "outputDirectory", defaultValue = "${project.build.directory}/generated-docs", required = true)
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
    @Parameter(property = GOAL_PREFIX
            + "upload.directory", defaultValue = "${project.artifactId}/${project.version}", required = true)
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

    private final void uploadTo(final Repository repository) throws MojoExecutionException {
        if (!this.inputDirectory.exists()) {
            throw new MojoExecutionException(
                    "The Asciidoctor generated files directory does not exists. Please, run asciidoctor-lifecycle:build first.");
        }

        if (getLog().isDebugEnabled()) {
            getLog().debug("Uploading to '" + this.uploadToRepository + "' , using credentials from server id '"
                    + this.serverId + "'.");
        }

        upload(repository);
    }

    private final void upload(final Repository repository) throws MojoExecutionException {
        final Wagon wagon = getWagon(repository);

        try {
            configureWagon(wagon);
            // TODO Need investigation for how get proxy info.
            // For the moment it will be null (no proxy info).
            final ProxyInfo proxyInfo = null;

            uploadDirectory(repository, wagon, proxyInfo);

        } catch (TransferFailedException e) {
            throw new MojoExecutionException("Unable to configure Wagon: '" + repository.getProtocol() + "'", e);
        }
    }

    private Wagon getWagon(final Repository repository) throws MojoExecutionException {
        final Wagon wagon;
        try {
            // This seems the new way to get the wagon reference
            wagon = (Wagon) this.container.lookup(Wagon.ROLE, repository.getProtocol());

        } catch (final ComponentLookupException cause) {
            final Throwable originalCause = cause.getCause();

            if (originalCause instanceof NoSuchElementException) {
                final String message = "Unsupported protocol: '" + repository.getProtocol() + "' "
                        + "for Asciidoctor upload to asciidoctor.lifecycle.deployToUrl = '" + repository.getUrl()
                        + "'.";
                final String messageWithAvailableProtocols = message + "\nAvailable protocols are: "
                        + WagonUtil.getSupportedProtocols(this.container, getLog()) + "."
                        + "\nMore protocols may be added through wagon providers, see http://maven.apache.org/plugins/maven-site-plugin/examples/adding-deploy-protocol.html";
                getLog().error(messageWithAvailableProtocols);
                throw new MojoExecutionException(message, cause.getCause());
            }
            throw new MojoExecutionException("Error while configuring wagon: '" + repository.getProtocol() + "'.",
                    cause);
        }

        if (!wagon.supportsDirectoryCopy()) {
            throw new MojoExecutionException(
                    "Wagon protocol '" + repository.getProtocol() + "' does not supports directory copy.");
        }
        return wagon;
    }

    private final void uploadDirectory(final Repository repository, final Wagon wagon, final ProxyInfo proxyInfo)
            throws MojoExecutionException {
        final AuthenticationInfo authenticationInfo = SettingsUtil.getAuthenticationInfo(this.serverId, this.settings,
                this.settingsDecrypter);
        try {
            if (proxyInfo != null) {
                wagon.connect(repository, authenticationInfo, proxyInfo);
            } else if (authenticationInfo != null) {
                wagon.connect(repository, authenticationInfo);
            } else {
                wagon.connect(repository);
            }
            wagon.putDirectory(inputDirectory, this.uploadToDirectory);
        } catch (final AuthorizationException | AuthenticationException | ConnectionException
                | ResourceDoesNotExistException | TransferFailedException cause) {
            throw new MojoExecutionException("Error uploading Asciidoctor documents to server: " + cause.getMessage(),
                    cause);
        } finally {
            try {
                wagon.disconnect();
            } catch (final ConnectionException cause) {
                getLog().error("Error disconnecting wagon - ignored", cause);
            }
        }
    }

    private final void configureWagon(final Wagon wagon) throws TransferFailedException {
        final Server server = settings.getServer(this.serverId);
        if (server != null && server.getConfiguration() != null) {
            final PlexusConfiguration plexusConfiguration = new XmlPlexusConfiguration(
                    (Xpp3Dom) server.getConfiguration());
            ComponentConfigurator componentConfigurator = null;

            try {
                componentConfigurator = (ComponentConfigurator) container.lookup(ComponentConfigurator.ROLE, "basic");
                componentConfigurator.configureComponent(wagon, plexusConfiguration, container.getContainerRealm());

            } catch (final ComponentLookupException cause) {
                throw new TransferFailedException("Unable to lookup wagon configurator for \'" + this.serverId + "\'",
                        cause);
            } catch (ComponentConfigurationException cause) {
                throw new TransferFailedException("Unable to apply wagon configuration for \'" + this.serverId + "\'.",
                        cause);
            } finally {
                if (componentConfigurator != null) {
                    try {
                        container.release(componentConfigurator);
                    } catch (ComponentLifecycleException e) {
                        getLog().error("Problem releasing component configurator - ignoring: " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextualize(Context context) throws ContextException {
        container = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
    }
}
