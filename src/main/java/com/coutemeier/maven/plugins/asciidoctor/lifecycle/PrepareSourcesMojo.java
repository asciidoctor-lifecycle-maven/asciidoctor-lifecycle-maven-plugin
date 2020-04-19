package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResolutionException;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.ArtifactUtil;
import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.FileUtil;
import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.ZipUtil;

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
    /**
     * Prefix for this goal only properties
     */
    private final String THIS_GOAL_PREFIX = GOAL_PREFIX + "build.";

    /**
     * Prefix to apply to properties created automatically to reflect where a theme was unzipped
     */
    private final String THEME_AUTOPROPERTY_PREFIX = "asciidoctor.theme.";

    /**
     * The current repository/network configuration of Maven.
     */
    @Parameter(readonly = true, defaultValue="${repositorySystemSession}")
    private RepositorySystemSession repoSession;


    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        try {
            debug( "sourceDirectory", this.sourceDirectory );
            if ( this.sourceDirectory.isDirectory() ) {
                FileUtil.copyDir( this.sourceDirectory.toPath(), this.getBuildSourceDirectory().toPath() );
                // We update the new Asciidoctor.sourceDirectory value
            }
            debug( "getBuildSourceDirectory", this.getBuildSourceDirectory() );
            this.setProperty( "asciidoctor.sourceDirectory", this.getBuildSourceDirectory().toString() );

        } catch (final IOException cause) {
            throw new MojoExecutionException("Error preparing asciidoctor sources", cause);
        }
    }
}
