package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import io.takari.maven.testing.executor.MavenRuntime.MavenRuntimeBuilder;

import org.junit.Assert;
import org.junit.Test;

public class SkipParameterIT
extends AbstractMojoIT{
    public SkipParameterIT( final MavenRuntimeBuilder builder )
    throws Exception {
        super( builder );
    }

    @Test
    public void skipSystemPropertyTest()
    throws Exception {
        multimoduleForProject()
            .withCliOptions( SKIP )
            .execute( "build" )
            .assertErrorFreeLog();
        Assert.assertTrue( subValidator.themeNotExists() && subValidator.dependencyNotExists() );
    }
}
