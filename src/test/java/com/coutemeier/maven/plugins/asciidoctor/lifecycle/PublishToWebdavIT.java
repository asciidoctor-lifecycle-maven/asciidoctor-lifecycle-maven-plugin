package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.FileUtil;

import io.takari.maven.testing.executor.MavenExecution;
import io.takari.maven.testing.executor.MavenRuntime.MavenRuntimeBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class PublishToWebdavIT
extends AbstractMojoIT {
    private SimpleDavServer server = null;
    private final Map<String, String> credentials = new HashMap<>();


    public PublishToWebdavIT( final MavenRuntimeBuilder builder )
    throws Exception {
        super( builder );
    }

    protected void beforeTest()
    throws Exception {
        final File folder = this.validator.getWebdavDirectory();
        if ( folder.exists() ) {
            FileUtil.deleteDir( folder.toPath() );
        } else {
            folder.mkdirs();
        }
        this.server = new SimpleDavServer( folder, 41000 );
        this.server.setCredentials( this.credentials );
        this.server.start();
    }

    @After
    public void afterTest() {
        if ( server != null ) {
            try {
                this.server.stop();
            } catch ( final Exception cause ) {
                // Ignore me
            }
        }
    }

    @Test
    public void publishWithoutCredentialsTest()
    throws Exception {
        this.credentials.clear();
        final MavenExecution execution = this.forProject( "publish-to-webdav" );
        this.beforeTest();
        execution
                .withCliOptions( "-X" )
                .execute( "asciidoctor-publish" )
                .assertErrorFreeLog();
        Assert.assertTrue( this.validator.webdavFilesExists() );
    }

    @Test
    public void publishWithCredentialsTest()
    throws Exception {
        // These are the user:password configured in webdav server to publish
        this.credentials.clear();
        this.credentials.put( "deployment", "deployment123" );
        final MavenExecution execution = this.forProject( "publish-to-webdav" );
        this.beforeTest();
        execution
                .withCliOptions( "-X" )
                .execute( "asciidoctor-publish" )
                .assertErrorFreeLog();
        Assert.assertTrue( this.validator.webdavFilesExists() );
    }

    @Test
    public void publishWithWrongCredentialsTest()
    throws Exception {
        // These are the user:password configured in webdav server to publish
        this.credentials.clear();
        this.credentials.put( "admin", "admin123" );
        final MavenExecution execution = this.forProject( "publish-to-webdav" );
        this.beforeTest();
        execution
            .withCliOptions( "-X" )
            .execute( "asciidoctor-publish" )
            .assertLogText( "Return code is: 401, ReasonPhrase: Unauthorized.");
        Assert.assertTrue( this.validator.webdavFilesNotExists() );
    }
}
