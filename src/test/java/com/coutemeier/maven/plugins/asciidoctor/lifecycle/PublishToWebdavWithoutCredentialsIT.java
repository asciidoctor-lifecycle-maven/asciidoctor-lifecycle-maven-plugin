package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.vo.ProjectValidator;
import com.soebes.itf.jupiter.extension.MavenDebug;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.assertj.core.api.SoftAssertions;

@MavenJupiterExtension
public class PublishToWebdavWithoutCredentialsIT
extends AbstractWebdavPublishMojoIT {
    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-publish" )
    @MavenDebug
    @SystemProperty( value = "webdav.server.port", content = "" + WebDavConstants.PUBLISH_WITHOUT_CREDENTIALS_PORT )
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
