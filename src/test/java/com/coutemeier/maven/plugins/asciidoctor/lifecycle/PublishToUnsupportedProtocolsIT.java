package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.soebes.itf.jupiter.extension.MavenCLIOptions;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenOption;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

public class PublishToUnsupportedProtocolsIT
extends AbstractPublishMojoIT {
    @MavenTest
    @MavenGoal( "clean" )
    @MavenGoal( "asciidoctor-publish" )
    @MavenOption( MavenCLIOptions.DEBUG )
    @Execution( ExecutionMode.CONCURRENT )
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
