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
public class CopySourcesIT {
    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-prepare-resources" )
    @MavenOption( MavenCLIOptions.DEBUG )
    @Execution( ExecutionMode.CONCURRENT )
    public void missingBuildDirectory( MavenExecutionResult result )
    throws Exception {
        assertThat( result )
            .isFailure()
            .out()
            .plain()
            .containsSequence( "org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal com.coutemeier.maven.plugins:asciidoctor-lifecycle-maven-plugin:1.0-SNAPSHOT:asciidoctor-copy-sources (default-asciidoctor-copy-sources) on project prepare-sources-ioexception: Error preparing asciidoctor sources" )
        ;
    }

    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-prepare-resources" )
    @MavenOption( MavenCLIOptions.DEBUG )
    @Execution( ExecutionMode.CONCURRENT )
    public void missingSourceDirectory( MavenExecutionResult result )
    throws Exception {
        final ProjectValidator validator = new ProjectValidator( result );
        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat( result.isSuccesful() ).as( "Build successful" );
        assertions.assertThat( validator.getBuildSources().doesNotContainsIndex() ).as( "'index.adoc' file doesn't exists" );
        assertions.assertAll();
    }
}
