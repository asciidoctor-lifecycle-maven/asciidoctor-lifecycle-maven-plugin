package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

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
	 * Write here the logic of the Mojo
	 *
     * @throws MojoExecutionException {@link MojoExecutionException}
     * @throws MojoFailureException {@link MojoFailureException}
     */
    protected abstract void doExecute()
        throws MojoExecutionException, MojoFailureException;
}
