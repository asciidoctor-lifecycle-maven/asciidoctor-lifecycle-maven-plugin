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
     * Prefix for this goal only properties
     */
    private final String THIS_GOAL_PREFIX = GOAL_PREFIX + "theme.";

    /**
     * Prefix to apply to properties created automatically to reflect where a theme was unzipped
     */
    private final String THEME_AUTOPROPERTY_PREFIX = "asciidoctor.theme.";

    /**
     * The directory where themes should be unzipping
     */
    @Parameter(property = THIS_GOAL_PREFIX
            + "themesBaseDir", defaultValue = "${project.build.directory}/asciidoctor-themes")
    private File themesBaseDir;

    /**
     * The list of themes to download and unzip
     */
    @Parameter(property = "themes", required = false)
    private List<String> themes;

    /**
     * Disable the unzip of the themes
     */
    @Parameter(property = THIS_GOAL_PREFIX + "disableUnzip", defaultValue = "false")
    private boolean disableUnzip;

    /**
     * The entry point to Aether, i.e. the component doing all the work.
     */
    @Component
    private RepositorySystem repoSystem;

    /**
     * The current repository/network configuration of Maven.
     */
    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repoSession;

    /**
     * Remote repositories which will be searched for themes.
     */
    @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
    private List<RemoteRepository> remoteRepositories;

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        try {
            for (final String theme : themes) {
                if (getLog().isInfoEnabled()) {
                    getLog().info("Asciidoctor theme " + theme + ": downloading...");
                }
                final Artifact themeArtifact = ArtifactUtil.downloadByAether(theme, repoSystem, repoSession,
                        remoteRepositories);
                if (getLog().isInfoEnabled()) {
                    getLog().info("Asciidoctor theme " + theme + ": downloaded");
                }
                if (!this.disableUnzip) {
                    final File themeOutputDir = new File(this.themesBaseDir, themeArtifact.getArtifactId());
                    ZipUtil.unzip(themeArtifact.getFile(), themeOutputDir);
                    final String propertyName = createPropertyName(themeArtifact);
                    final String propertyValue = themeOutputDir.getCanonicalPath();
                    this.setProperty(propertyName, propertyValue);

                    if (getLog().isDebugEnabled()) {
                        getLog().debug("Asciidoctor theme " + theme + " - property: " + propertyName + " = \""
                                + propertyValue + "\"");
                    }
                }
            }

        } catch (final IOException cause) {
            throw new MojoExecutionException("Error unpacking theme", cause);
        } catch (final ArtifactResolutionException cause) {
            throw new MojoExecutionException("Error downloading theme", cause);
        }
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
