package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

public abstract class AbstractAsciidoctorLifecycleMojo
extends AbstractMojo {
	/**
     * Skip plugin execution completely
     */
    @Parameter( property = "asciidoctor.lifecycle.skip", defaultValue = "false", required = false)
    private boolean skip;

    /**
     * The directory where themes should be unzipping
     */
    @Parameter( property = "asciidoctor.lifecycle.themesBaseDir", defaultValue = "${project.build.directory}/asciidoctor-themes" )
    private File themesBaseDir;

    /**
     * The maven project
     */
    @Parameter( readonly = true, defaultValue = "${project}" )
    protected MavenProject project;

	/*
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public final void execute()
        throws MojoExecutionException, MojoFailureException {
        if ( isSkip() ) {
            getLog().info( "Skipping plugin execution" );
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
     * Return the directory base for unzipping themes
     *
     * @return {@link #themesBaseDir}
     */
    public File getThemesBaseDir() {
    	return this.themesBaseDir;
    }

    /**
     * Get the current project instance.
     *
     * @return the project
     */
    public MavenProject getProject()
    {
        return this.project;
    }

    protected void setProperty( final String name, final String value ) {
    	if ( getLog().isDebugEnabled() ) {
    		getLog().debug( "define property " + name + " = \"" + value + "\"" );
        }
    	this.project.getProperties().put( name,  value );
    }


	/**
	 * Write here the logic of the Mojo
	 *
     * @throws MojoExecutionException {@link MojoExecutionException}
     * @throws MojoFailureException {@link MojoFailureException}
     */
    protected abstract void doExecute()
        throws MojoExecutionException, MojoFailureException;
}
