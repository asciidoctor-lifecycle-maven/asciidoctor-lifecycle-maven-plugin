package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.vo.ProjectValidator;
import com.soebes.itf.jupiter.extension.MavenDebug;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenJupiterExtension
@MavenGoal("clean")
@MavenGoal("asciidoctor-convert")
@MavenDebug
public class SkipParameterIT {
    @MavenTest
    @SystemProperty( value="asciidoctor.skip", content="true" )
    @DisplayName( "asciidoctor.skip=true" )
    public void skipTrue( MavenExecutionResult result )
    throws Exception {
        assertThat( result )
            .isSuccessful()
            .out()
                .info()
                .containsSequence( Messages.SKIPPING_PLUGIN_EXECUTION );

        final ProjectValidator validator = new ProjectValidator( result );

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat( validator.getTarget().doesNotExists() );
        assertions.assertAll();
    }

    @MavenTest
    @SystemProperty( value="asciidoctor.skip", content="false" )
    @DisplayName( "asciidoctor.skip=false" )
    public void skipFalse( MavenExecutionResult result )
    throws Exception {
        assertThat( result )
            .isSuccessful()
            .out()
                .info()
                .doesNotContainSequence( Messages.SKIPPING_PLUGIN_EXECUTION );

        final ProjectValidator validator = new ProjectValidator( result );
        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat( validator.getTarget().exists() );
        assertions.assertThat( validator.getBuildSources().containsIndex() );
        assertions.assertAll();
    }

    @MavenTest
    @DisplayName( "asciidoctor.skip absent" )
    public void skipMissing( MavenExecutionResult result )
    throws Exception {
        assertThat( result )
            .isSuccessful()
            .out()
                .info()
                .doesNotContainSequence( Messages.SKIPPING_PLUGIN_EXECUTION );

        final ProjectValidator validator = new ProjectValidator( result );
        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat( validator.getTarget().exists() );
        assertions.assertThat( validator.getBuildSources().containsIndex() );
        assertions.assertAll();
    }
}
