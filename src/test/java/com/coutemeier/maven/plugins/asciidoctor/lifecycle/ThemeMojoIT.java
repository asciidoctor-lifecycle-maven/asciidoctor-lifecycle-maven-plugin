package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import static io.takari.maven.testing.TestResources.assertFilesNotPresent;
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
public class ThemeMojoIT {
    private static final String THEME_FILE_PATH = "/wagon-provider-api/META-INF/DEPENDENCIES";
    private static final String THEMESBASEDIR_PATH = "target/asciidoctor-themes";
    private static final String EXPECTED_THEME_FILE_PATH = THEMESBASEDIR_PATH + THEME_FILE_PATH;

    @Rule
    public final TestResources resources = new TestResources();

    public final MavenRuntime maven;

    private final String[] cliOptions;

    public ThemeMojoIT( final MavenRuntimeBuilder builder )
    throws Exception {
        this.maven = builder.build();
        this.cliOptions = new String[] { "-B", "-e", "-DmavenVersion=" + this.maven.getMavenVersion() };
    }

    @Test
    public void themeExistsTest()
    throws Exception {
        final File basedir = resources.getBasedir( "theme/theme-exists" );
        maven.forProject(basedir) //
            .withCliOptions( cliOptions ) //
            .execute("theme") //
            .assertErrorFreeLog();
        assertFilesPresent( basedir, EXPECTED_THEME_FILE_PATH );
    }

    @Test
    public void noThemesConfigured()
    throws Exception {
        final File basedir = resources.getBasedir( "theme/no-themes-configured" );
        maven.forProject(basedir) //
            .withCliOptions( cliOptions ) //
            .execute("theme") //
            .assertErrorFreeLog();
        assertFilesNotPresent( basedir, EXPECTED_THEME_FILE_PATH );
    }

    @Test
    public void ioexceptionThemesBaseDirTest()
    throws Exception {
        final File basedir = resources.getBasedir( "theme/ioexception-themesBaseDir" );

        maven.forProject(basedir) //
            .withCliOptions( cliOptions ) //
            .execute("theme")
            .assertLogText( "Error unpacking theme: /this$$/path/does/not/exists/in/Linux/junit" );
    }

    @Test
    public void themeDoesntExistsTest()
    throws Exception {
        final File basedir = resources.getBasedir( "theme/theme-doesnt-exists" );
        maven.forProject(basedir) //
            .withCliOptions( cliOptions ) //
            .execute("theme")
            .assertLogText( "Error downloading theme: " );
    }

    @Test
    public void disableUnzipTrueTest()
    throws Exception {
        final File basedir = resources.getBasedir( "theme/theme-disableUnzip-true" );
        maven.forProject(basedir) //
            .withCliOptions( cliOptions ) //
            .execute("theme")
            .assertErrorFreeLog();
        assertFilesNotPresent( basedir, EXPECTED_THEME_FILE_PATH );
    }

    @Test
    public void themesBaseDirTest()
    throws Exception {
        final String expectedThemeFilePath = "target/themes-asciidoctor" + THEME_FILE_PATH;
        File basedir = resources.getBasedir( "theme/themesBaseDir" );
        maven.forProject(basedir) //
            .withCliOptions( cliOptions ) //
            .execute("theme") //
            .assertErrorFreeLog();
        assertFilesPresent( basedir, expectedThemeFilePath );
    }
}
