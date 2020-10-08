package com.coutemeier.maven.plugins.asciidoctor.lifecycle.vo;

import java.io.File;

public class MyFile extends File {
    public MyFile( final File file ) {
        super( file.getAbsolutePath() );
    }

    public MyFile( final String path ) {
        super( path );
    }

    public MyFile( final MyFile file, final String path ) {
        super( file, path );
    }

    public boolean contains( final String path ) {
        return this.getFile( path ).exists();
    }

    public boolean containsDependency() {
        return this.containsFile( "junit-4.11.jar" );
    }

    public boolean containsFile( final String filename ) {
        final MyFile file = this.getFile( filename );
        return file.exists() && file.isFile();
    }

    public boolean containsFolder( final String foldername ) {
        final MyFile folder = this.getFile( foldername );
        return folder.exists() && folder.isDirectory();
    }

    public boolean containsIndex() {
        return this.containsFile( "index.adoc" );
    }

    public boolean containsIndexHtml() {
        return this.containsFile( "index.html" );
    }

    public boolean doesNotContainsFile( final String filename ) {
        return ! this.containsFile( filename );
    }

    public boolean doesNotContainsDependency() {
        return ! this.containsDependency();
    }

    public boolean doesNotContainsIndex() {
        return ! this.containsIndex();
    }

    public boolean doesNotContainsIndexHtml() {
        return ! this.containsIndexHtml();
    }

    public boolean doesNotContainsFolder( final String foldername ) {
        return ! this.containsFolder( foldername );
    }

    public boolean doesNotExists() {
        return ! this.exists();
    }

    public MyFile getFile( final String filename ) {
        return new MyFile( this, filename );
    }
}
