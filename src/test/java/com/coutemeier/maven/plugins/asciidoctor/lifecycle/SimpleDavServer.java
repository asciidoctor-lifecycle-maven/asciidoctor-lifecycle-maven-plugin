package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import de.bitinsomnia.webdav.server.MiltonWebDAVFileServer;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDavServer {
    private final MiltonWebDAVFileServer server;

    public SimpleDavServer( final File rootFolder, final int port ) {
        this.server = new MiltonWebDAVFileServer( rootFolder );
        server.setPort( port );
    }

    /**
     * Set server credentials for publish files.
     *
     * @parameter credentials a map of user:password entries to set users with publish permission.
     */
    public void setCredentials( final Map<String, String> credentials ) {
        this.server.getUserCredentials().clear();
        this.server.getUserCredentials().putAll( credentials );
    }

    public int getPort() {
        return this.server.getPort();
    }

    public void start()
    throws Exception {
        this.server.start();
    }

    public void stop()
    throws Exception {
        this.server.stop();
    }
}
