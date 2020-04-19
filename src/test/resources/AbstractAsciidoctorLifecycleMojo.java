package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;

/**
 * Abstract base class for Asciidoctor Lifecycle mojos.
 *
 * @author rrialq
 * @since 1.0
 */
public abstract class AbstractAsciidoctorLifecycleMojo
extends AbstractMojo {
    protected static final String ASCIIDOCTOR_GOAL_PREFIX = "asciidoctor.";

    protected static final String GOAL_PREFIX = "asciidoctor.lifecycle.";

    protected static final String BUILDDIRECTORY_DEFAULT_VALUE = "asciidoctor-build";

    /**
     * Skip plugin execution completely
     */
    @Parameter(property = GOAL_PREFIX + "skip", defaultValue = "false", required = false)
    private boolean skip;

    /**
     * The maven project
     */
    @Parameter(readonly = true, defaultValue="${project}")
    private MavenProject project;


    /**
     * The entry point to Aether, i.e. the component doing all the work.
     */
    @Component
    protected RepositorySystem repositorySystem;

    /**
     * Directory where are source files
     */
    @Parameter(property = ASCIIDOCTOR_GOAL_PREFIX + "sourceDirectory", defaultValue="${project.basedir}/src/main/asciidoc", required=false)
    private File sourceDirectory;

    /**
     * Directory used to store all source files allowing merging with theme resources,
     * without affecting original versioned files.
     */
    @Parameter(property = GOAL_PREFIX + "buildDirectory", defaultValue="${project.build.directory}/" + BUILDDIRECTORY_DEFAULT_VALUE, required=false)
    private File buildDirectory;

    /*
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        if (isSkip()) {
            getLog().info("Skipping plugin execution");
        } else {
            doExecute();
        }
    }

    /**
     * @return {@link #skip}
     */
    public boolean isSkip() {
        return skip;
    }

    /**
     * Get the current project instance.
     *
     * @return the project
     */
    public MavenProject getProject() {
        return this.project;
    }

    protected void setProperty(final String name, final String value) {
        if (getLog().isDebugEnabled()) {
            getLog().debug("define property " + name + " = \"" + value + "\"");
        }
        this.getProject().getProperties().put(name, value);
    }

    public File getBuildDirectory() {
        return this.buildDirectory;
    }

    public File getBuildSourceDirectory() {
        return new File( this.buildDirectory, "asciidoc");
    }

    /**
     * Write here the logic of the Mojo
     *
     * @throws MojoExecutionException
     *             {@link MojoExecutionException}
     * @throws MojoFailureException
     *             {@link MojoFailureException}
     */
    protected abstract void doExecute() throws MojoExecutionException, MojoFailureException;
}
