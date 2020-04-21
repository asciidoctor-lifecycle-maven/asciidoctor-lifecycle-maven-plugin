package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

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
            .assertLogText( Messages.PUBLISH_ERROR_MISSING_GENERATED_FILES );
        Assert.assertTrue( this.validator.publishedFilesNotExists() );
    }

    @Test
    public void toDirectory()
    throws Exception {
        this.forProject("publish/publish-to-folder")
            .withCliOptions( "-Dasciidoctor.lifecycle.publish.serverId=nexus" )
            .execute( "asciidoctor-publish" )
            .assertErrorFreeLog();

        Assert.assertTrue( this.validator.publishedFilesExists() );
    }

    public void toDirectoryNoProxyNoAuthentication()
    throws Exception {
        this.forProject("publish/publish-to-folder")
            .execute( "asciidoctor-publish" )
            .assertErrorFreeLog()
            .assertLogText( Messages.PUBLISH_CONNECT_NOAUTHENTICATION_AND_NOPROXY );

        Assert.assertTrue( this.validator.publishedFilesExists() );
    }

    @Test
    public void toDirectoryProxy()
    throws Exception {
        this.forProject("publish/publish-to-folder")
            .withCliOptions( "-Dasciidoctor.lifecycle.publish.serverId=nexus" )
            .execute( "asciidoctor-publish" )
            .assertErrorFreeLog()
            .assertLogText( Messages.PUBLISH_CONNECT_WITH_AUTHENTICATION );

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
