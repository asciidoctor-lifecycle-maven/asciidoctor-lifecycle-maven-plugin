package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import static io.takari.maven.testing.TestResources.assertFilesPresent;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import io.takari.maven.testing.executor.MavenRuntime.MavenRuntimeBuilder;

public class PublishMojoIT
extends AbstractMojoIT {
    public PublishMojoIT( final MavenRuntimeBuilder builder )
    throws Exception {
        super( builder );
    }

    @Test
    public void inputDirectoryDoesntExistsTest()
    throws Exception {
        this.forProject("publish/inputDirectory-doesnt-exists") //
            .execute( "asciidoctor-publish" ) //
            .assertLogText( "The Asciidoctor generated files directory does not exists" );
        Assert.assertTrue( this.validator.publishedFilesNotExists() );
    }

    @Test
    public void publishToDirectory()
    throws Exception {
        this.forProject("publish/publish-to-folder") //
            .execute( "asciidoctor-publish" )
            .assertErrorFreeLog();

        Assert.assertTrue( this.validator.publishedFilesExists() );
    }

    @Test
    public void wagonDoesntSupportDirectCopy()
    throws Exception {
        this.forProject("publish/wagon-doesnt-support-directcopy") //
            .execute( "asciidoctor-publish" ) //
            .assertLogText( "Wagon protocol 'https' doesn't support directory copying" );
        Assert.assertTrue( this.validator.publishedFilesNotExists() );
    }
}
