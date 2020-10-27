package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import com.soebes.itf.jupiter.extension.MavenDebug;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

public class PublishToHttpsIT
extends AbstractPublishMojoIT {
    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-publish" )
    @MavenDebug
    public void wagonDoesNotSupportDirectCopy( final MavenExecutionResult result ) {
        assertThat( result )
            .isFailure()
            .out()
                .error()
                    .contains(
                        "Failed to execute goal com.coutemeier.maven.plugins:asciidoctor-lifecycle-maven-plugin:1.0-SNAPSHOT:asciidoctor-publish (default-asciidoctor-publish) on project publish-doesnt-support-directcopy: Wagon protocol 'https' doesn't support directory copying -> [Help 1]"
                    );
    }

}
