package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import java.io.File;
import java.util.NoSuchElementException;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Settings;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

@Mojo( name="upload", requiresProject=true, threadSafe = true)
public class UploadMojo
extends AbstractAsciidoctorLifecycleMojo
implements Contextualizable {
	/**
     */
    @Component
    private WagonManager wagonManager;

    /**
     * The current user system settings for use in Maven.
     */
    @Parameter( defaultValue = "${settings}", readonly = true )
    private Settings settings;

    private PlexusContainer container;

	@Parameter( property = "asciidoctor.lifecycle.outputDirectory", defaultValue="${project.build.directory}/generated-docs", required = true )
	private File outputDirectory;

	@Parameter( property="asciidoctor.lifecycle.uploadTo", required = true )
	private String uploadTo;

	@Parameter( property="asciidoctor.lifecycle.upload.serverId", required = false )
	private String serverId;

	@Override
	protected void doExecute() throws MojoExecutionException, MojoFailureException {
		deployTo( new Repository( this.serverId, this.uploadTo ) );
	}


	private final void deploy( final File input, final Repository repository )
	throws MojoExecutionException {
		final Wagon wagon = getWagon( repository, this.container );

		try {
			configureWagon( wagon, repository.getId(), settings, container, getLog() );
		} catch ( TransferFailedException e ) {
            throw new MojoExecutionException( "Unable to configure Wagon: '" + repository.getProtocol() + "'", e );
        }
	}

	private Wagon getWagon( final Repository repository, final PlexusContainer container )
    throws MojoExecutionException {
		final Wagon wagon;
		try {
			// This seems the new way to get the wagon reference
			wagon = ( Wagon ) container.lookup( Wagon.ROLE, repository.getProtocol() );
			//wagon = wagonManager.getWagon( repository );
/*
		} catch ( final UnsupportedProtocolException cause ) {
			final String message = "Unsupported protocol: '" + repository.getProtocol() + "' "
				+ "for Asciidoctor upload to asciidoctor.lifecycle.deployToUrl = '" + repository.getUrl() + "'.";
			final String messageWithAvailableProtocols = "Available protocols are: " + getSupportedProtocols() + ".\n"
				+ " More protocols may be added through wagon providers, see http://maven.apache.org/plugins/maven-site-plugin/examples/adding-deploy-protocol.html";
			getLog().error( messageWithAvailableProtocols );
			throw new MojoExecutionException( message, cause );
		} catch ( final TransferFailedException cause ) {
			throw new MojoExecutionException( "Error while configuring wagon: '" + repository.getProtocol() + "'.", cause );
*/
		} catch ( final ComponentLookupException cause ) {
			final Throwable originalCause = cause.getCause();

			if ( originalCause instanceof NoSuchElementException ) {
				final String message = "Unsupported protocol: '" + repository.getProtocol() + "' "
						+ "for Asciidoctor upload to asciidoctor.lifecycle.deployToUrl = '" + repository.getUrl() + "'.";
				final String messageWithAvailableProtocols = message + "\nAvailable protocols are: " + getSupportedProtocols() + ".\n"
						+ " More protocols may be added through wagon providers, see http://maven.apache.org/plugins/maven-site-plugin/examples/adding-deploy-protocol.html";
				getLog().error( messageWithAvailableProtocols );
				throw new MojoExecutionException( message, cause.getCause() );
			}
			throw new MojoExecutionException( "Error while configuring wagon: '" + repository.getProtocol() + "'.", cause );
		}

		if ( ! wagon.supportsDirectoryCopy() ) {
			throw new MojoExecutionException( "Wagon protocol '" + repository.getProtocol() + "' does not supports directory copy." );
		}
		return wagon;
	}

	private final void deployTo( final Repository repository )
	throws MojoExecutionException {
		if ( ! this.outputDirectory.exists() ) {
			throw new MojoExecutionException( "The Asciidoctor generated files directory does not exists. Please, run asciidoctor-lifecycle:build first." );
		}

		if ( getLog().isDebugEnabled() ) {
			getLog().debug( "Uploading to '" + this.uploadTo + "' , using credentials from server id '" + this.serverId + "'." );
		}

		deploy( this.outputDirectory, repository );
	}

	private final void configureWagon( final Wagon wagon, final String repositoryId, final Settings settings, final PlexusContainer container, final Log logger )
	throws TransferFailedException {

	}

	private String getSupportedProtocols() {
		try {
			return String.join( ",", container.lookupMap( Wagon.class ).keySet() );

		} catch ( final ComponentLookupException cause ) {
			getLog().error( cause );
		}
		return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public void contextualize( Context context )
    throws ContextException {
        container = ( PlexusContainer ) context.get( PlexusConstants.PLEXUS_KEY );
    }
}
