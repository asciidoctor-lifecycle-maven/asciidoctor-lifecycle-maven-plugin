package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import static io.takari.maven.testing.TestResources.assertFilesPresent;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.takari.maven.testing.TestResources;
import io.takari.maven.testing.executor.MavenRuntime;
import io.takari.maven.testing.executor.MavenRuntime.MavenRuntimeBuilder;
import io.takari.maven.testing.executor.MavenVersions;
import io.takari.maven.testing.executor.junit.MavenJUnitTestRunner;

@RunWith(MavenJUnitTestRunner.class)
@MavenVersions(value= {"3.3.9"})
public class PhasesIT {
    private static final String THEME_FILE_PATH = "/wagon-provider-api/META-INF/DEPENDENCIES";
    private static final String THEMESBASEDIR_PATH = "target/asciidoctor-themes";
    private static final String EXPECTED_THEME_FILE_PATH = THEMESBASEDIR_PATH + THEME_FILE_PATH;

    @Rule
    public final TestResources resources = new TestResources();

    public final MavenRuntime maven;

    private final String[] cliOptions;

    public PhasesIT( final MavenRuntimeBuilder builder )
    throws Exception {
        this.maven = builder.build();
        this.cliOptions = new String[] { "-B", "-e", "-DmavenVersion=" + this.maven.getMavenVersion() };
    }

    @Test
    public void prepareUploadTest()
    throws Exception {
        final File basedir = resources.getBasedir( "phases/prepare-build" );
        final String expectedJUnitPath = "target/dependency/wagon-provider-api-3.3.3.jar";
        maven.forProject(basedir) //
            .withCliOptions( cliOptions ) //
            .execute( "prepare-build" ) //
            .assertErrorFreeLog();
        assertFilesPresent( basedir, EXPECTED_THEME_FILE_PATH, expectedJUnitPath );
    }

    @Test
    public void buildTest()
    throws Exception {
        final File basedir = resources.getBasedir( "phases/build" );
        final String expectedIndexPath = "target/generated-docs/index.html";
        maven.forProject(basedir) //
            .withCliOptions( cliOptions ) //
            .execute( "build" ) //
            .assertErrorFreeLog();
        assertFilesPresent( basedir, EXPECTED_THEME_FILE_PATH, expectedIndexPath );
    }
}
