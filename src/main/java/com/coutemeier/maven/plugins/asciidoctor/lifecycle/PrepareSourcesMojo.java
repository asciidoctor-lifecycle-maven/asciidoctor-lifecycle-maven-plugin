package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.FileUtil;

/**
 * Prepare sources for build.
 * <p>
 * In corporate environments it may be necessary to reuse files whose content is common (for example, images, writing conventions, legal notices ...).
 * It may even be necessary to generate new files "on the fly" (images, new content, ...), or even solve other problems in a more simple and controlled way.
 * <p>
 * Don't forget that it is a bad idea to mix the documentation sources with the files generated or added during build preparation,
 * because other build processes may require working only with the original sources.
 * <p>
 * This mojo solves the problem by copying the original files to a new location,
 * and updating the value of the {@code asciidoctor.sourceDirectory} property.
 * <p>
 * This mojo performs the following steps:
 * <ol>
 *      <li>Copy sources to directory specified by the {@code asciidoctor.lifecycle.outputDirectory} property.
 *      <li>Update the value of the {@code asciidoctor.outputDirectory} property.
 * </ol>
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
