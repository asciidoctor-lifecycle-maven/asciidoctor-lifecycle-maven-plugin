package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution( ExecutionMode.CONCURRENT)
public class ArtifactUtilOtherTestCase {

    @Test
    @Execution( ExecutionMode.CONCURRENT)
    public void invalidCoordinatesTest() throws ArtifactResolutionException, MojoFailureException {
        assertThrows( MojoFailureException.class, () ->
            ArtifactUtil.downloadByAether("", null, null, null)
        );
    }

}
