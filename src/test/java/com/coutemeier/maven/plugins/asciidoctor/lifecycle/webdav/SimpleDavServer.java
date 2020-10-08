package com.coutemeier.maven.plugins.asciidoctor.lifecycle.webdav;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import de.bitinsomnia.webdav.server.MiltonWebDAVFileServer;

public class SimpleDavServer
implements Closeable {
    private static final Logger LOGGER = Logger.getLogger( SimpleDavServer.class.getName() );
    private final MiltonWebDAVFileServer server;
    private final File rootFolder;

    public SimpleDavServer( final File rootFolder, final int port )
    throws IOException {
        this.rootFolder = rootFolder;
        this.server = new MiltonWebDAVFileServer( rootFolder );
        this.server.setPort( port );
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

    public File getRootFolder() {
        return this.rootFolder;
    }

    public void start()
    throws Exception {
        this.server.start();
    }

    public void stop()
    throws Exception {
        this.server.stop();
    }

    public void stopSilently() {
        try {
            this.stop();
        } catch ( final Exception cause ) {
            LOGGER.warning( "Error stopping server: " + cause.getMessage() );
        }
    }

    @Override
    public void close()
    throws IOException {
        try {
            this.server.stop();
        } catch ( final Exception cause ) {
            throw new IOException( cause );
        }
    }
}
