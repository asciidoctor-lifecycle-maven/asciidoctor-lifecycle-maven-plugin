package com.coutemeier.maven.plugins.asciidoctor.lifecycle.vo;

import java.io.File;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.Constants;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenProjectResult;

public class ProjectValidator {
    private final MyFile root;
    /*
     * Points to the basedir module that contains the asciidoctor sources
     */
    private final MyFile module;
    /*
     * Points to the module's target directory
     */
    private final MyFile target;
    private final MyFile build;
    private final MyFile dependencies;
    private final MyFile html5;
    private final MyFile themes;
    private final MyFile themeExample;
    private final MyFile buildSources;
    private final MyFile webdav;
    private final MyFile published;

    public ProjectValidator( final MavenExecutionResult result ) {
        this( result, null );
    }

    public ProjectValidator(final MavenExecutionResult result, final File webdavFolder ) {
        final String themeAbsolutePath = this.getThemeBaseDir( result.getMavenProjectResult() );
        this.root = new MyFile( result.getMavenProjectResult().getBaseDir().getAbsolutePath() );
        this.module = new MyFile( themeAbsolutePath );
        this.target = new MyFile( this.module, "target" );
        this.build = new MyFile( this.target, Constants.BUILDDIRECTORY );
        this.html5 = new MyFile( this.build, "html5" );
        this.themes = new MyFile( this.build, "themes" );
        this.themeExample = new MyFile( this.themes, "theme-example" );
        this.buildSources = new MyFile( this.build, "asciidoctor" );
        this.dependencies = new MyFile( this.target, "dependencies" );
        if ( webdavFolder != null ) {
            this.webdav = new MyFile( webdavFolder );
            this.published = new MyFile( new File( webdavFolder, "publish-to/0.0.1-SNAPSHOT" ) );
        } else {
            this.webdav = null;
            this.published = null;
        }
    }

    public MyFile getBuild() {
        return this.build;
    }

    public MyFile getDependencies() {
        return this.dependencies;
    }

    public MyFile getHtml5() {
        return this.html5;
    }

    public MyFile getModule() {
        return this.module;
    }

    public MyFile getRoot() {
        return this.root;
    }

    public MyFile getBuildSources() {
        return this.buildSources;
    }

    public MyFile getTarget() {
        return this.target;
    }

    public MyFile getThemes() {
        return this.themes;
    }

    public MyFile getThemeExample() {
        return this.themeExample;
    }

    public MyFile getWebdav() {
        return this.webdav;
    }

    public MyFile getPublished() {
        return this.published;
    }

    static String getThemeBaseDir( final MavenProjectResult result ) {
        if ( result.getModel().getModules().isEmpty() ) {
            return result.getBaseDir().getAbsolutePath();
        }
        return new File( result.getBaseDir(), result.getModel().getModules().get( 0 ) ).getAbsolutePath();
    }
}
