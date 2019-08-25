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
public class UploadMojoIT {
    private static final String THEME_FILE_PATH = "/wagon-provider-api/META-INF/DEPENDENCIES";
    private static final String THEMESBASEDIR_PATH = "target/asciidoctor-themes";
    private static final String EXPECTED_THEME_FILE_PATH = THEMESBASEDIR_PATH + THEME_FILE_PATH;

    @Rule
    public final TestResources resources = new TestResources();

    public final MavenRuntime maven;

    private final String[] cliOptions;

    public UploadMojoIT( final MavenRuntimeBuilder builder )
    throws Exception {
        this.maven = builder.build();
        this.cliOptions = new String[] { "-B", "-e", "-DmavenVersion=" + this.maven.getMavenVersion() };
    }

    @Test
    public void inputDirectoryDoesntExistsTest()
    throws Exception {
        final File basedir = resources.getBasedir( "upload/inputDirectory-doesnt-exists" );
        maven.forProject(basedir) //
            .withCliOptions( cliOptions ) //
            .execute( "upload" ) //
            .assertLogText( "The Asciidoctor generated files directory does not exists" );
    }

    @Test
    public void publishToDirectory()
    throws Exception {
        final File basedir = resources.getBasedir( "upload/publish-to-folder" );
        final String expectedFile = "target/publish-repository/publish-to-folder/0.0.1-SNAPSHOT/index.html";
        maven.forProject(basedir) //
            .withCliOptions( cliOptions ) //
            .execute( "upload" )
            .assertErrorFreeLog();
        assertFilesPresent( basedir, expectedFile );
    }

    @Test
    public void wagonDoesntSupportDirectCopy()
    throws Exception {
        final File basedir = resources.getBasedir( "upload/wagon-doesnt-support-directcopy" );
        maven.forProject(basedir) //
            .withCliOptions( cliOptions ) //
            .execute( "upload" ) //
            .assertLogText( "Wagon protocol 'https' does not supports directory copy" );
    }
}
