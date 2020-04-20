package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import org.junit.Assert;
import org.junit.Test;

import io.takari.maven.testing.executor.MavenRuntime.MavenRuntimeBuilder;

public class PhasesIT
extends AbstractMojoIT {
    public PhasesIT( final MavenRuntimeBuilder builder )
    throws Exception {
        super( builder );
    }

    @Test
    public void prepareBuildTest()
    throws Exception {
        multimoduleForProject() //
            .execute( "prepare-build" ) //
            .assertErrorFreeLog();
        Assert.assertTrue(
            this.subValidator.themeFilesExists()
            && this.subValidator.generatedFilesNotExists()
            && this.subValidator.dependencyNotExists() );
    }

    @Test
    public void buildTest()
    throws Exception {
        multimoduleForProject()
            .execute( "build" )
            .assertErrorFreeLog();
        Assert.assertTrue(
            this.subValidator.themeFilesExists()
            && this.subValidator.generatedFilesExists()
            && this.subValidator.dependencyNotExists()
        );
    }

    @Test
    public void packageTest()
    throws Exception {
        multimoduleForProject()
            .execute( "package" )
            .assertErrorFreeLog();
        Assert.assertTrue(
            this.subValidator.dependencyExists()
            && this.subValidator.themeFilesNotExists()
            && this.subValidator.generatedFilesNotExists()
        );
    }

}
