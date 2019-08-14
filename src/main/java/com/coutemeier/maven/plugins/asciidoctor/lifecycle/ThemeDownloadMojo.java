package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResolutionException;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.ArtifactUtil;
import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.ZipUtil;

/**
 * Download and unpack a list of themes
 * <p>
 * This mojo downloads a list of themes and unpack every one to its own folder.
 */
@Mojo( name="theme-download", requiresProject = true, threadSafe = true )
public class ThemeDownloadMojo
extends AbstractAsciidoctorLifecycleMojo {
	/**
     * The list of themes to download and unzip
     */
    @Parameter( property = "asciidoctor.lifecycle.themes", required = false )
    private List<String> themes;

    /**
     * Disable the unzip of the themes
     */
    @Parameter( property="asciidoctor.lifecycle.disableUnzip", defaultValue="false" )
    private boolean disableUnzip;

   /**
     * The entry point to Aether, i.e. the component doing all the work.
     */
    @Component
    private RepositorySystem repoSystem;

   /**
     * The current repository/network configuration of Maven.
     */
    @Parameter( defaultValue = "${repositorySystemSession}", readonly = true )
    private RepositorySystemSession repoSession;

    /**
     * Remote repositories which will be searched for themes.
     */
    @Parameter( defaultValue = "${project.remoteProjectRepositories}", readonly = true )
    private List<RemoteRepository> remoteRepositories;

    @Override
	protected void doExecute()
    throws MojoExecutionException, MojoFailureException {
        try {
        	for( final String theme: themes ) {
	        	getLog().info( "Downloading asciidoctor theme: " + theme );
	            final Artifact themeArtifact = ArtifactUtil.downloadByAether( theme, repoSystem, repoSession, remoteRepositories );
	            getLog().info( "Asciidoctor theme " + theme + " downloaded" );
	            if ( ! this.disableUnzip ) {
	            	final File themeOutputDir = new File( this.getThemesBaseDir(), themeArtifact.getArtifactId() );
	            	ZipUtil.unzip( themeArtifact.getFile(), themeOutputDir );
	            	getLog().info( "Asciidoctor theme " + theme + " unpacked in " + themeOutputDir );
	            }
        	}

        } catch ( final IOException cause ) {
            throw new MojoExecutionException( "Error unpacking theme", cause );
        } catch ( final ArtifactResolutionException cause ) {
        	throw new MojoExecutionException( "Error downloading theme", cause );
        }
    }
}
