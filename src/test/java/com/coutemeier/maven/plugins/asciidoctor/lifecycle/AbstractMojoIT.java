package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import io.takari.maven.testing.TestResources;
import io.takari.maven.testing.executor.MavenExecution;
import io.takari.maven.testing.executor.MavenRuntime;
import io.takari.maven.testing.executor.MavenRuntime.MavenRuntimeBuilder;
import io.takari.maven.testing.executor.MavenVersions;
import io.takari.maven.testing.executor.junit.MavenJUnitTestRunner;

import java.io.IOException;
import java.io.File;

import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(MavenJUnitTestRunner.class)
@MavenVersions(value= {"3.3.9"})
public abstract class AbstractMojoIT {
    protected static final String SUBMODULE_NAME = "asciidoctor-module";
    protected static final String DEPENDENCY_PATH = "target/dependency/junit-4.11.jar";

    protected static final String THEMESBASEDIR_PATH = "target/" + Constants.BUILDDIRECTORY + "/themes";
    protected static final String README_PATH = "/theme-example/asciidoc/_templates/readme.adoc";

    protected static final String THEME_README_PATH = THEMESBASEDIR_PATH + README_PATH;
    protected static final String SUBMODULE_THEME_README_PATH = SUBMODULE_NAME + THEME_README_PATH;

    @Rule
    public final TestResources resources = new TestResources();

    protected final MavenRuntime maven;

    protected File basedirFile;

    protected FilesValidator validator;
    protected FilesValidator subValidator;

    protected static final String SKIP = "-Dasciidoctor.skip=true";

    public AbstractMojoIT( final MavenRuntimeBuilder builder )
    throws Exception {
        this.maven = builder.build();
    }

    public MavenExecution forProject( final String basedir )
    throws IOException {
        this.basedirFile = resources.getBasedir( basedir );
        this.validator = new FilesValidator( this.basedirFile );
        this.subValidator = new FilesValidator( this.basedirFile, SUBMODULE_NAME );
        return forProject( this.basedirFile );
    }

    public MavenExecution forProject( final File basedirFile )
    throws IOException {
        return maven.forProject( basedirFile )
        .withCliOptions(
            "-B",
            "-e",
            "-X"
        );
    }

    public MavenExecution multimoduleForProject()
    throws IOException {
        return forProject( "multimodule" );
    }

    protected void debug( final String title, final File file ) {
        System.out.println(
            String.format( "[Asciidoctor.lifecycle] %s(%s, %s)=%s", title, file.isDirectory(), file.isFile(), file.toString() )
        );
    }
}
