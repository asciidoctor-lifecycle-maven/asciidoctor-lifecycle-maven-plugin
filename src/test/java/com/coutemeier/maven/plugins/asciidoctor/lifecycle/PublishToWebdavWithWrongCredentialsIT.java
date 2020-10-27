package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.vo.ProjectValidator;
import com.soebes.itf.jupiter.extension.MavenCLIOptions;
import com.soebes.itf.jupiter.extension.MavenDebug;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenOption;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.assertj.core.api.SoftAssertions;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenJupiterExtension
public class PublishToWebdavWithWrongCredentialsIT
extends AbstractWebdavPublishMojoIT {
    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-publish" )
    @MavenOption( value = MavenCLIOptions.SETTINGS, parameter = "src/test/resources/settings.xml" )
    @MavenDebug
    @SystemProperty( value = "webdav.server.port", content = "" + WebDavConstants.PUBLISH_WITH_WRONG_CREDENTIALS_PORT )
    public void publishWithWrongCredentials( final MavenExecutionResult result ) {
        final ProjectValidator validator = new ProjectValidator( result, this.getRootFolder() );
        final SoftAssertions assertions = new SoftAssertions();

        assertThat( result )
            .out()
                .error()
                    .contains(
                        "Failed to execute goal com.coutemeier.maven.plugins:asciidoctor-lifecycle-maven-plugin:1.0-SNAPSHOT:asciidoctor-publish (default-asciidoctor-publish) on project publish-to: Publish: Error publishing generated files to server: Failed to transfer file: http://localhost:40003/publish-to/0.0.1-SNAPSHOT/index.html. Return code is: 401, ReasonPhrase: Unauthorized. -> [Help 1]"
                    );
        assertions.assertThat( result.isFailure() ).isTrue();
        assertions.assertThat( validator.getPublished().containsIndexHtml() ).isFalse();
        assertions.assertAll();
    }

    @Override
    protected int getPort() {
        return WebDavConstants.PUBLISH_WITH_WRONG_CREDENTIALS_PORT;
    }

    @Override
    public boolean areCredentialsEnabled() {
        return true;
    }
}
