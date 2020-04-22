package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import org.junit.Assert;
import org.junit.Ignore;
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
            .assertLogText( Messages.PUBLISH_ERROR_MISSING_GENERATED_FILES );
        Assert.assertTrue( this.validator.publishedFilesNotExists() );
    }

    @Test
    public void toDirectory()
    throws Exception {
        this.forProject("publish/publish-to-folder")
            .withCliOptions( "-Dasciidoctor.lifecycle.publish.serverId=nexus", "-X" )
            .execute( "asciidoctor-publish" )
            .assertErrorFreeLog();

        Assert.assertTrue( this.validator.publishedFilesExists() );
    }

    @Test
    public void connectWithoutProxy()
    throws Exception {
        this.forProject("publish/publish-to-folder")
            .withCliOptions( "-Dasciidoctor.lifecycle.publish.serverId=nexus", "-X" )
            .execute( "asciidoctor-publish" )
            .assertErrorFreeLog()
            .assertLogText( Messages.PUBLISH_CONNECT_WITHOUT_PROXY );

        Assert.assertTrue( this.validator.publishedFilesExists() );
    }

    @Ignore( "Until the day I write a proxy service for testing" )
    @Test
    public void connectWithProxy()
    throws Exception {
        this.forProject("publish/publish-to-folder")
            .withCliOptions( "-Dasciidoctor.lifecycle.publish.serverId=nexus-proxy", "-X" )
            .execute( "asciidoctor-publish" )
            .assertErrorFreeLog()
            .assertLogText( Messages.PUBLISH_CONNECT_WITH_PROXY );

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

    @Test
    public void unsupportedProtocolTest()
    throws Exception {
        final String expected = "Unsupported protocol: 'smb' for documentation deployment to";
        this.forProject("publish/publish-to-folder")
            .withCliOptions( "-Dasciidoctor.lifecycle.publish.serverId=nexus", "-X")
            .withCliOptions( "-Dasciidoctor.lifecycle.publish.repository=smb:///tmp/publish-to" )
            .execute( "asciidoctor-publish" )
            .assertLogText( expected );

        Assert.assertTrue( this.validator.webdavFilesNotExists() );
    }
}
