package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.vo.ProjectValidator;
import com.soebes.itf.jupiter.extension.MavenDebug;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenGoal("clean")
@MavenGoal("asciidoctor-publish")
@MavenDebug
public class PublishToDirectoryIT
extends AbstractPublishMojoIT {
    @MavenTest
    @SystemProperty( value = "subdirectory.name", content = "publishToDirectory" )
    @Execution( ExecutionMode.CONCURRENT )
    public void publishToDirectory( final MavenExecutionResult result )
    throws Exception {
        final ProjectValidator validator = new ProjectValidator( result, this.getChildDirectory( "publishToDirectory" ) );
        final SoftAssertions assertions = new SoftAssertions();

        assertions.assertThat( result.isSuccesful() ).isTrue();
        assertions.assertThat( validator.getPublished().containsIndexHtml() ).isTrue();
        assertions.assertAll();
    }

    @MavenTest
    @SystemProperty( value = "subdirectory.name", content = "inputDirectoryDoesNotExists" )
    @Execution( ExecutionMode.CONCURRENT )
    public void inputDirectoryDoesNotExists( final MavenExecutionResult result ) {
        new ProjectValidator( result, this.getChildDirectory( "inputDirectoryDoesNotExists" ) );
        final SoftAssertions assertions = new SoftAssertions();
        assertThat( result )
            .out()
                .error()
                    .containsSequence(
                        "Failed to execute goal com.coutemeier.maven.plugins:asciidoctor-lifecycle-maven-plugin:1.0-SNAPSHOT:asciidoctor-publish (default-asciidoctor-publish) on project inputDirectory-doesnt-exists: Publish: The Asciidoctor generated files does not exists. Please, run build first. -> [Help 1]"
                    );
        assertions.assertThat( result.isFailure() ).isTrue();
        assertions.assertAll();
    }
}
