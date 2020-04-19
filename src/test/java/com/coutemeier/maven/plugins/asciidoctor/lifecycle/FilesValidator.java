package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import java.io.File;

public final class FilesValidator {
    private final File basedir;
    private final File mavenBuildDirectory;
    private final File buildDirectory;
    private final File themesDirectory;
    private final File htmlBuildDirectory;
    private final File readmeFile;
    private final File dependencyFile;
    private final File indexFile;
    private final File themeDirectory;

    public FilesValidator( final File basedir ) {
        super();
        this.basedir = basedir;
        this.mavenBuildDirectory = new File( this.basedir, "target" );
        this.buildDirectory = new File( this.mavenBuildDirectory, Constants.BUILDDIRECTORY );
        this.themesDirectory = new File( this.buildDirectory, "themes" );
        this.htmlBuildDirectory = new File( this.buildDirectory, "html5" );
        this.themeDirectory = new File( this.themesDirectory, "theme-example" );
        this.readmeFile = new File( this.themeDirectory, "asciidoc/_templates/readme.adoc" );
        this.dependencyFile = new File( this.mavenBuildDirectory, "dependency/junit-4.11.jar" );
        this.indexFile = new File( this.htmlBuildDirectory, "index.html" );
    }

    public FilesValidator( final File basedir, String moduleName ) {
        this ( new File( basedir, moduleName ) );
    }

    public boolean indexExists() {
        return this.indexFile.exists();
    }

    public boolean indexNotExists() {
        return !this.indexFile.exists();
    }

    public boolean dependencyExists() {
        return this.dependencyFile.exists();
    }

    public boolean dependencyNotExists() {
        return !this.dependencyFile.exists();
    }

    public boolean readmeExists() {
        return this.readmeFile.exists();
    }

    public boolean readmeNotExists() {
        return !this.readmeFile.exists();
    }

    public boolean themeExists() {
        return this.themeDirectory.exists();
    }

    public boolean themeNotExists() {
        return !this.themeDirectory.exists();
    }

    public boolean themeFilesExists() {
        return this.themeExists() && this.readmeExists();
    }

    public boolean themeFilesNotExists() {
        return this.themeNotExists() && this.readmeNotExists();
    }

    public boolean generatedFilesExists() {
        return this.indexExists();
    }

    public boolean generatedFilesNotExists() {
        return this.indexNotExists();
    }
}
