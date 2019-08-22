package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.junit.Test;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.ArtifactUtil;

public class ArtifactUtilOtherTestCase {

    @Test(expected = MojoFailureException.class)
    public void invalidCoordinatesTest() throws ArtifactResolutionException, MojoFailureException {
        ArtifactUtil.downloadByAether("", null, null, null);
    }

}
