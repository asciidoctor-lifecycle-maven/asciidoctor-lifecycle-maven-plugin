package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import org.junit.Assert;
import org.junit.Test;

import io.takari.maven.testing.executor.MavenRuntime.MavenRuntimeBuilder;

public class PrepareSourcesIT
extends AbstractMojoIT {
    public PrepareSourcesIT( final MavenRuntimeBuilder builder )
    throws Exception {
        super( builder );
    }

    @Test
    public void prepareSourcesIOExceptionTest()
    throws Exception {
        forProject( "prepareSources-ioexception" ) //
            .execute( "asciidoctor-prepare-convert" ) //
            .assertLogText( Messages.PREPARE_SOURCES_ERROR_PREPARING );
        Assert.assertTrue( this.validator.buildDirectoryNotExists() );
    }

    @Test
    public void prepareSourcesSourceDirectoryNotExistsTest()
    throws Exception {
        forProject( "prepareSources-sourceDirectoryNotExists" ) //
            .execute( "asciidoctor-prepare-convert" ) //
            .assertErrorFreeLog();
        Assert.assertTrue( this.validator.indexNotExists() );
    }
}
