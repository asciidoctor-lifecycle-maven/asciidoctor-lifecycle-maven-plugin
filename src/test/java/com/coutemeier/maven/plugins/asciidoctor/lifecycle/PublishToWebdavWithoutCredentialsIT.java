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
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

@MavenJupiterExtension
@Execution( ExecutionMode.CONCURRENT )
public class PublishToWebdavWithoutCredentialsIT
extends AbstractWebdavPublishMojoIT {
    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-publish" )
    @MavenOption( MavenCLIOptions.DEBUG )
    @SystemProperty( value = "webdav.server.port", content = "" + WebDavConstants.PUBLISH_WITHOUT_CREDENTIALS_PORT )
    @Execution( ExecutionMode.CONCURRENT )
    public void publish( final MavenExecutionResult result ) {
        final ProjectValidator validator = new ProjectValidator( result, this.getRootFolder() );
        final SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat( result.isSuccesful() ).isTrue();
        assertions.assertThat( validator.getPublished().containsIndexHtml() ).isTrue();
        assertions.assertAll();
    }

    @Override
    protected int getPort() {
        return WebDavConstants.PUBLISH_WITHOUT_CREDENTIALS_PORT;
    }
}
