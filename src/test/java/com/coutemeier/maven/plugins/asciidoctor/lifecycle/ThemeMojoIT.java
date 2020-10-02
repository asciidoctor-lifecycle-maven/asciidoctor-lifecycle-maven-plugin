package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.vo.ProjectValidator;
import com.soebes.itf.jupiter.extension.MavenCLIOptions;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenOption;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

@MavenJupiterExtension
@Execution( ExecutionMode.CONCURRENT )
public class ThemeMojoIT {
    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-theme" )
    @MavenOption( MavenCLIOptions.DEBUG )
    @Execution( ExecutionMode.CONCURRENT )
    public void themeExists( final MavenExecutionResult result )
    throws Exception {
        final ProjectValidator validator = new ProjectValidator( result );
        final SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat( result.isSuccesful() ).isTrue().as( "Result" );
        assertions.assertThat( validator.getThemeExample().exists() ).isTrue().as( "themes/theme-example" );
        assertions.assertThat( validator.getDependencies().containsDependency() ).isFalse().as( "dependency" );
        assertions.assertAll();
    }

    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-theme" )
    @MavenOption( MavenCLIOptions.DEBUG )
    @Execution( ExecutionMode.CONCURRENT )
    public void noThemesConfigured( final MavenExecutionResult result ) {
        final ProjectValidator validator = new ProjectValidator( result );
        final SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat( result.isSuccesful() ).isTrue();
        assertions.assertThat( validator.getThemeExample().containsIndex() ).isFalse();
        assertions.assertThat( validator.getThemeExample().containsIndexHtml() ).isFalse();
        assertions.assertThat( validator.getDependencies().containsDependency() ).isFalse();
        assertions.assertAll();
    }

    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-theme" )
    @MavenOption( MavenCLIOptions.DEBUG )
    @Execution( ExecutionMode.CONCURRENT )
    public void buildDirectoryDoesNotExists( final MavenExecutionResult result ) {
        final ProjectValidator validator = new ProjectValidator( result );
        final SoftAssertions assertions = new SoftAssertions();

        assertThat( result )
            .out()
                .plain()
                    .containsSequence(
                        "[ERROR] Failed to execute goal com.coutemeier.maven.plugins:asciidoctor-lifecycle-maven-plugin:1.0-SNAPSHOT:asciidoctor-theme (default-asciidoctor-theme) on project theme-builddirectory-ioexception: Theme: Error unpacking theme: /dir-doesnt-exists/themes/theme-example/asciidoc/snippets/inprogress.adoc -> [Help 1]",
                        "org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal com.coutemeier.maven.plugins:asciidoctor-lifecycle-maven-plugin:1.0-SNAPSHOT:asciidoctor-theme (default-asciidoctor-theme) on project theme-builddirectory-ioexception: Theme: Error unpacking theme"
                    );

        assertions.assertThat( result.isSuccesful() ).isFalse();
        assertions.assertThat( validator.getBuild().exists() ).isFalse();
        assertions.assertAll();
    }

    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-theme" )
    @MavenOption( MavenCLIOptions.DEBUG )
    @Execution( ExecutionMode.CONCURRENT )
    public void themeDoesNotExist( final MavenExecutionResult result )
    throws Exception {
        final ProjectValidator validator = new ProjectValidator( result );
        final SoftAssertions assertions = new SoftAssertions();
        assertThat( result )
            .isFailure()
            .out()
                .plain()
                    .containsSequence(
                        "[ERROR] Failed to execute goal com.coutemeier.maven.plugins:asciidoctor-lifecycle-maven-plugin:1.0-SNAPSHOT:asciidoctor-theme (default-asciidoctor-theme) on project theme-exists: Theme: Error downloading theme: Could not find artifact com.coutemeier.maven.plugins.skip.system.property:theme-example:zip:1.0.0-does-not-exists in central (https://repo.maven.apache.org/maven2) -> [Help 1]",
                            "org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal com.coutemeier.maven.plugins:asciidoctor-lifecycle-maven-plugin:1.0-SNAPSHOT:asciidoctor-theme (default-asciidoctor-theme) on project theme-exists: Theme: Error downloading theme"
                    );

        assertions.assertThat( result.isSuccesful() ).isFalse();
        assertions.assertThat( validator.getThemeExample().containsIndex() ).isFalse();
        assertions.assertAll();
    }
}
