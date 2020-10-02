package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import java.io.File;

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;

@MavenJupiterExtension
public abstract class AbstractPublishMojoIT {
    private final File relativeRootFolder;

    public AbstractPublishMojoIT() {
        this( "filesystem" );
    }

    public AbstractPublishMojoIT( final String outputFolder ) {
        final File outputFolderFile = new File( "target", outputFolder );
        this.relativeRootFolder = new File( outputFolderFile, this.getClass().getSimpleName() );
        this.relativeRootFolder.mkdirs();
    }

    public File getRelativeRoot() {
        return this.relativeRootFolder;
    }

    public File getChildDirectory( final String childFolder ) {
        return new File( this.relativeRootFolder, childFolder );
    }
}
