package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.vo.ProjectValidator;
import com.soebes.itf.jupiter.extension.MavenCLIOptions;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenOption;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

@MavenJupiterExtension
@Execution( ExecutionMode.CONCURRENT )
public class SkipParameterIT {
    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-convert" )
    @MavenOption( MavenCLIOptions.DEBUG )
    @SystemProperty( value="asciidoctor.skip", content="true" )
    @DisplayName( "asciidoctor.skip=true" )
    @Execution( ExecutionMode.CONCURRENT )
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
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-convert" )
    @MavenOption( MavenCLIOptions.DEBUG )
    @SystemProperty( value="asciidoctor.skip", content="false" )
    @DisplayName( "asciidoctor.skip=false" )
    @Execution( ExecutionMode.CONCURRENT )
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
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-convert" )
    @MavenOption( MavenCLIOptions.DEBUG )
    @DisplayName( "asciidoctor.skip absent" )
    @Execution( ExecutionMode.CONCURRENT )
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
