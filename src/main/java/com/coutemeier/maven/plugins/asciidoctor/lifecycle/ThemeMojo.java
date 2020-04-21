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
@Mojo(name = "theme", requiresProject = true, threadSafe = true)
public class ThemeMojo extends AbstractAsciidoctorLifecycleMojo {
        /**
     * Prefix to apply to properties created automatically to reflect where a theme was unzipped
     */
    private final String THEME_AUTOPROPERTY_PREFIX = "asciidoctor.theme.";

    /**
     * The list of themes to download and unzip
     */
    @Parameter(property = "themes", required = false)
    private List<String> themes;

    /**
     * The current repository/network configuration of Maven.
     */
    @Parameter(readonly = true, defaultValue="${repositorySystemSession}")
    private RepositorySystemSession repoSession;

    /**
     * Remote repositories which will be searched for themes.
     */
    @Parameter(readonly=true, defaultValue="${project.remoteProjectRepositories}")
    protected List<RemoteRepository> remoteRepositories;

    /**
     * The directory where themes should be unzipping
     */
    private File getThemesDirectory() {
        return new File( this.buildDirectory, "themes" );
    }

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        try {
            for (final String theme : themes) {
                // Download the theme artifact
                final Artifact themeArtifact = downloadAction( theme );
                // Unpack it to directory
                final File outputDirectory = unpackAction( themeArtifact );
                copyCommonThemeResources( themeArtifact, outputDirectory );
                // Create the property
                createPropertyAction( themeArtifact, outputDirectory );
            }

        } catch ( final IOException cause ) {
            throw new MojoExecutionException( Messages.THEME_ERROR_UNPACKING, cause );
        } catch ( final ArtifactResolutionException cause ) {
            throw new MojoExecutionException( Messages.THEME_ERROR_DOWNLOADING, cause );
        }
    }

    /**
     * Download theme artifact from remote repository if needed.
     */
    private Artifact downloadAction( final String theme )
    throws IOException, ArtifactResolutionException, MojoFailureException {
        this.debugMessage( Messages.THEME_DOWNLOADING, theme );
        return ArtifactUtil.downloadByAether(theme, repositorySystem, repoSession, remoteRepositories);
    }

    /**
     * Unpack the downloaded artifact theme to a folder.
     * <p>
     * Steps are:
     * <ul>
     *      <li>Extract the contents to the output folder.
     *      <li>Copy the _asciidoctor/templates folder if exists to the ${asciidoctor.sources}
     * </ul>
     */
    private File unpackAction( final Artifact theme )
    throws IOException {
        final File outputDirectory = new File(this.getThemesDirectory(), theme.getArtifactId());
        this.debugMessage( Messages.THEME_UNPACKING, theme.getArtifactId(), outputDirectory.toString() );
        ZipUtil.unzip(theme.getFile(), outputDirectory);
        return outputDirectory;
    }

    /**
     * Copy all common resources to ${project.output.directory}/asciidoctor-build/asciidoc
     * <p>
     * Common resources are all resources under ${theme}/asciidoc directory.
     */
    private void copyCommonThemeResources(final Artifact theme, final File themeOutputDirectory)
    throws IOException {
        final File commonResources = new File( themeOutputDirectory, Constants.THEME_COMMON_RESOURCES_DEFAULT_DIRECTORY );
        if ( commonResources.isDirectory() ) {
            this.debugFormatted( Messages.THEME_COPY_RESOURCES, theme.getArtifactId() );
            FileUtil.copyDir( commonResources.toPath(), this.getBuildSourceDirectory().toPath() );
        }
    }

    private String createPropertyAction( final Artifact theme, final File outputDirectory )
    throws IOException {
        final String propertyName = createPropertyName(theme);
        final String propertyValue = outputDirectory.getCanonicalPath();
        this.setProperty(propertyName, propertyValue);
        return propertyName;
    }

    /**
     * Creates a property name for the artifact (theme), normalizing its artifacId, prepending it with a prefix and
     * ending it with ".path"
     *
     * @param artifact
     *            the artifact for which property will be created
     * @return a text with the name of the property
     * @see #THEME_AUTOPROPERTY_PREFIX
     */
    private String createPropertyName(final Artifact artifact) {
        return THEME_AUTOPROPERTY_PREFIX + ArtifactUtil.normalizeArtifactId(artifact) + ".path";
    }
}
