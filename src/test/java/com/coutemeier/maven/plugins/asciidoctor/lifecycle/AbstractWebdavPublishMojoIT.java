package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.FileUtil;
import com.coutemeier.maven.plugins.asciidoctor.lifecycle.webdav.SimpleDavServer;
import com.soebes.itf.jupiter.extension.BeforeEachMaven;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

public abstract class AbstractWebdavPublishMojoIT {
    private SimpleDavServer server = null;
    private Logger logger = Logger.getLogger( AbstractWebdavPublishMojoIT.class.getName() );
    private String serverId = "";

    @BeforeEachMaven
    public void startServer()
    throws Exception {
        final File rootFolder = new File ( "target", "webdav/" + this.getClass().getSimpleName() );

        if ( rootFolder.exists() ) {
            FileUtil.deleteDir( rootFolder.toPath() );
        } else {
            rootFolder.mkdirs();
        }

        this.serverId = "http://localhost:" + this.getPort() + " at " + rootFolder.getAbsolutePath();
        this.server = new SimpleDavServer( rootFolder, this.getPort() );
        this.server.setCredentials( this.getCredentials() );
        this.logger.info( "Starting webdav server at: " + this.server.getPort() );
        this.server.start();
        this.logger.info( "Started webdav server at: " + this.server.getPort() );
    }

    @AfterEach
    public void stopServer( final MavenExecutionResult result) {
        if ( this.server != null ) {
            this.logger.info(  "Stopping webdav server: " + this.serverId );
            this.server.stopSilently();
            this.logger.info(  "Stopped webdav server: " + this.serverId );
        }
    }

    protected boolean areCredentialsEnabled() {
        return false;
    }

    protected Map<String, String> getCredentials() {
        final Map<String, String> credentials = new HashMap<>();
        if ( this.areCredentialsEnabled() ) {
            credentials.put( "deployment", "deployment123" );
        }
        return credentials;
    }

    protected abstract int getPort();

    protected String getPublishToPath() {
        return "webdav/" + this.getClass().getSimpleName();
    }

    public File getRootFolder() {
        return this.server.getRootFolder();
    }
}
