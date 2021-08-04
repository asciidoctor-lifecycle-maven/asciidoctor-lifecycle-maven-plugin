package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import com.soebes.itf.jupiter.extension.MavenDebug;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

public class PublishToUnsupportedProtocolsIT
extends AbstractPublishMojoIT {
    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-publish" )
    @MavenDebug
    public void publishToSamba( final MavenExecutionResult result ) {
        assertThat( result )
            .isFailure()
            .out()
                .plain()
                    .contains(
                        "Unsupported protocol: 'smb' for documentation deployment to url=smb:///home/user/Documents/html."
                    );
    }

}
