package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import static io.takari.maven.testing.TestResources.assertFilesNotPresent;

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
public class AbstractAsciidoctorLifecycleMojoIT {
    private static final String THEMESBASEDIR_PATH = "target/asciidoctor-themes";
    private static final String THEME_FILE_PATH = "/wagon-provider-api/META-INF/DEPENDENCIES";
    private static final String EXPECTED_THEME_FILE_PATH = THEMESBASEDIR_PATH + THEME_FILE_PATH;

    @Rule
    public final TestResources resources = new TestResources();

    public final MavenRuntime maven;

    private final String[] cliOptions;

    public AbstractAsciidoctorLifecycleMojoIT( final MavenRuntimeBuilder builder )
    throws Exception {
        this.maven = builder.build();
        this.cliOptions = new String[] { "-B", "-e", "-DmavenVersion=" + this.maven.getMavenVersion() };
    }

    @Test
    public void skipParameterTest()
    throws Exception {
        final File basedir = resources.getBasedir( "abstract/skip-parameter-true" );
        maven.forProject(basedir) //
            .withCliOptions( cliOptions ) //
            .execute( "upload" ) //
            .assertErrorFreeLog();
        // We cannot test EXPECTED_GENERATED_INDEX_PATH
        // because asciidoctor-maven-plugin executes, because its skip parameter is different
        assertFilesNotPresent( basedir, EXPECTED_THEME_FILE_PATH );
    }
}
