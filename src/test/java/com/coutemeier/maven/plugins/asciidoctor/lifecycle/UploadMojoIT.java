package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import static io.takari.maven.testing.TestResources.assertFilesPresent;

import java.io.File;

import org.junit.Test;

import io.takari.maven.testing.executor.MavenRuntime.MavenRuntimeBuilder;

public class UploadMojoIT
extends AbstractMojoIT {
    public UploadMojoIT( final MavenRuntimeBuilder builder )
    throws Exception {
        super( builder );
    }

    @Test
    public void inputDirectoryDoesntExistsTest()
    throws Exception {
        final File basedirFile = resources.getBasedir( "upload/inputDirectory-doesnt-exists" );
        forProject(basedirFile) //
            .execute( "upload" ) //
            .assertLogText( "The Asciidoctor generated files directory does not exists" );
    }

    @Test
    public void publishToDirectory()
    throws Exception {
        final File basedirFile = resources.getBasedir( "upload/publish-to-folder" );
        final String expectedFile = "target/publish-repository/publish-to-folder/0.0.1-SNAPSHOT/index.html";
        forProject(basedirFile) //
            .execute( "upload" )
            .assertErrorFreeLog();
        assertFilesPresent( basedirFile, expectedFile );
    }

    @Test
    public void wagonDoesntSupportDirectCopy()
    throws Exception {
        final File basedirFile = resources.getBasedir( "upload/wagon-doesnt-support-directcopy" );
        forProject(basedirFile) //
            .execute( "upload" ) //
            .assertLogText( "Wagon protocol 'https' does not supports directory copy" );
    }
}
