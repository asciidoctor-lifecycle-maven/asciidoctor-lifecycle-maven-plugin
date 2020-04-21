package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.FileUtil;

/**
 * Manages the themes by downloading them, unziping them and creating a property to reflect where has been unzipped.
 * <p>
 * This mojo follows the following steps for each theme configured:
 * <ol>
 * <li>Download the theme (an artifact with type zip).
 * <li>Unzip the theme.
 * <li>Create a property to reflect where this theme has been unzipped.
 * </ol>
 *
 * <b>Pattern for property names created in step 3</b>
 * <p>
 * <code>asciidoctor.theme.${normalizedArtifactId}.path</code>
 * <p>
 * The <code>normalizedArtifactId</code> is the name of the <code>artifactId</code> theme, but removing chars not in
 * [A-Za-z0-9-].
 *
 * @author rrialq
 * @since 1.0
 */
@Mojo(name = "prepare-sources", requiresProject = true, threadSafe = true)
public class PrepareSourcesMojo extends AbstractAsciidoctorLifecycleMojo {
    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        try {
            this.getLog().info( String.format( "Build directory=%s", this.getBuildSourceDirectory().toString() ) );
            this.debugFormatted( Messages.PREPARE_SOURCES_SOURCE_DIRECTORY, this.sourceDirectory );
            
            if ( this.sourceDirectory.isDirectory() ) {
                FileUtil.copyDir( this.sourceDirectory.toPath(), this.getBuildSourceDirectory().toPath() );
                // We update the new Asciidoctor.sourceDirectory value
            }
            this.setProperty( "asciidoctor.sourceDirectory", this.getBuildSourceDirectory().toString() );

        } catch (final IOException cause) {
            throw new MojoExecutionException( Messages.PREPARE_SOURCES_ERROR_PREPARING, cause );
        }
    }
}
