package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.vo.ProjectValidator;
import com.soebes.itf.jupiter.extension.MavenDebug;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.assertj.core.api.SoftAssertions;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenJupiterExtension
@MavenGoal("clean")
@MavenGoal("asciidoctor-theme")
@MavenDebug
public class ThemeMojoIT {
    @MavenTest
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
    public void buildDirectoryDoesNotExists( final MavenExecutionResult result ) {
        final ProjectValidator validator = new ProjectValidator( result );
        final SoftAssertions assertions = new SoftAssertions();

        assertThat( result )
            .out()
                .plain()
                    .containsSequence(
                        "[ERROR] Failed to execute goal com.coutemeier.maven.plugins:asciidoctor-lifecycle-maven-plugin:1.0-SNAPSHOT:asciidoctor-theme (default-asciidoctor-theme) on project theme-builddirectorydoesnotexists: Theme: Error unpacking theme: /dir-doesnt-exists/themes/theme-example/asciidoc/snippets/inprogress.adoc -> [Help 1]",
                        "org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal com.coutemeier.maven.plugins:asciidoctor-lifecycle-maven-plugin:1.0-SNAPSHOT:asciidoctor-theme (default-asciidoctor-theme) on project theme-builddirectorydoesnotexists: Theme: Error unpacking theme"
                    );

        assertions.assertThat( result.isSuccesful() ).isFalse();
        assertions.assertThat( validator.getBuild().exists() ).isFalse();
        assertions.assertAll();
    }

    @MavenTest
    public void themeDoesNotExist( final MavenExecutionResult result )
    throws Exception {
        final ProjectValidator validator = new ProjectValidator( result );
        final SoftAssertions assertions = new SoftAssertions();
        assertThat( result )
            .isFailure()
            .out()
                .plain()
                    .containsSequence(
                        "org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal com.coutemeier.maven.plugins:asciidoctor-lifecycle-maven-plugin:1.0-SNAPSHOT:asciidoctor-theme (default-asciidoctor-theme) on project theme-doesnotexists: Theme: Error downloading theme"
                    );

        assertions.assertThat( result.isSuccesful() ).isFalse();
        assertions.assertThat( validator.getThemeExample().containsIndex() ).isFalse();
        assertions.assertAll();
    }
}
