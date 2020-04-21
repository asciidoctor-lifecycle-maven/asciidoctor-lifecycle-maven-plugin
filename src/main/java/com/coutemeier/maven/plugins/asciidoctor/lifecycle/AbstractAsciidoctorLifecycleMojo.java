package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import java.io.File;

import static com.coutemeier.maven.plugins.asciidoctor.lifecycle.Constants.BUILDDIRECTORY;

import org.apache.maven.execution.MavenSession;
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
public abstract class AbstractAsciidoctorLifecycleMojo extends AbstractMojo {
    protected static final String ASCIIDOCTOR_GOAL_PREFIX = "asciidoctor.";
    protected static final String GOAL_PREFIX = "asciidoctor.lifecycle.";

    /**
     * Skip plugin execution completely
     */
    @Parameter(property = ASCIIDOCTOR_GOAL_PREFIX + "skip", defaultValue = "false", required = false)
    private boolean skip;

    /**
     * The maven project
     */
    @Parameter(readonly = true, defaultValue="${project}")
    protected MavenProject project;

    @Parameter( defaultValue = "${session}", readonly = true )
    protected MavenSession mavenSession;

    /**
     * The entry point to Aether, i.e. the component doing all the work.
     */
    @Component
    protected RepositorySystem repositorySystem;

    /**
     * Directory where are source files
     */
    @Parameter(property = ASCIIDOCTOR_GOAL_PREFIX + "sourceDirectory", defaultValue="${project.basedir}/src/main/asciidoc", required=false)
    protected File sourceDirectory;

    /**
     * Directory used to store all source files allowing merging with theme resources,
     * without affecting original versioned files.
     */
    @Parameter(property = GOAL_PREFIX + "buildDirectory", defaultValue="${project.build.directory}/" + BUILDDIRECTORY, required=true)
    protected File buildDirectory;

    /*
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        if (isSkip()) {
            getLog().info( Messages.SKIPPING_PLUGIN_EXECUTION );
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
        this.debugFormatted( Messages.SET_PROPERTY, name, value);
        this.getProject().getProperties().put(name, value);
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

    /**
     * Show a debug message text.
     *
     * @parameter values an array of strings that will be joined with space separator
     */
    protected void debugMessage( final String ... values ) {
        if ( this.getLog().isDebugEnabled() ) {
            this.getLog().debug( String.join( " ", values ) );
        }
    }

    /**
     * Show a debug message text, formatting with object array
     *
     * @parameter formatter formatter for the message
     * @parameter objects an array of objects used for formatting the message
     */
    protected void debugFormatted( final String formatter, final Object ... objects ) {
        if ( this.getLog().isDebugEnabled() ) {
            final String message = String.format( formatter, objects );
            this.getLog().debug( message );
        }
    }
}
