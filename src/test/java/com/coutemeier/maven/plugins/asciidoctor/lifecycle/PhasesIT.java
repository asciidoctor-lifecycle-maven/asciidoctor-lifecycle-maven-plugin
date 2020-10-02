package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

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
public class PhasesIT {

    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-prepare-convert" )
    @MavenOption( MavenCLIOptions.DEBUG )
    @Execution( ExecutionMode.CONCURRENT )
    public void prepareConvertGoal( MavenExecutionResult result )
    throws Exception {
        final ProjectValidator validator = new ProjectValidator( result );
        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat( result.isSuccesful() );
        assertions.assertThat( validator.getTarget().exists() );
        assertions.assertThat( validator.getModule().containsIndex() );
        assertions.assertThat( validator.getHtml5().doesNotContainsIndexHtml() );
        assertions.assertAll();
    }

    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-convert" )
    @MavenOption( MavenCLIOptions.DEBUG )
    @Execution( ExecutionMode.CONCURRENT )
    public void convertGoal( final MavenExecutionResult result )
    throws Exception {
        final ProjectValidator validator = new ProjectValidator( result );
        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat( result.isSuccesful() );
        assertions.assertThat( validator.getTarget().exists() );
        assertions.assertThat( validator.getBuildSources().containsIndex() );
        assertions.assertThat( validator.getHtml5().containsIndexHtml() );
        assertions.assertThat( validator.getDependencies().doesNotContainsDependency() );
        assertions.assertAll();
    }

    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "package" )
    @MavenOption( MavenCLIOptions.DEBUG )
    @Execution( ExecutionMode.CONCURRENT )
    public void packageGoal( final MavenExecutionResult result )
    throws Exception {
        final ProjectValidator validator = new ProjectValidator( result );
        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat( result.isSuccesful() );
        assertions.assertThat( validator.getTarget().exists() );
        assertions.assertThat( validator.getBuildSources().doesNotExists() );
        assertions.assertThat( validator.getHtml5().doesNotExists() );
        assertions.assertThat( validator.getDependencies().containsDependency() );
        assertions.assertAll();
    }
}
