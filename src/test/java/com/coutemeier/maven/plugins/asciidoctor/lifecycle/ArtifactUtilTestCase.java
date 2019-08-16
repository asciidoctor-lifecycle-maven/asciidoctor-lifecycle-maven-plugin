package com.coutemeier.maven.plugins.asciidoctor.lifecycle;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.coutemeier.maven.plugins.asciidoctor.lifecycle.util.ArtifactUtil;

@RunWith(Parameterized.class)
public class ArtifactUtilTestCase {

	private Artifact artifact;
	private String expectedPropertyName;

	public ArtifactUtilTestCase( final Artifact artifact, final String expectedPropertyName ) {
		this.artifact = artifact;
		this.expectedPropertyName = expectedPropertyName;
	}


	@Test
	public void normalizeArtifactIdTest() {
		final String propertyName = ArtifactUtil.normalizeArtifactId( this.artifact );
		Assert.assertEquals( this.expectedPropertyName, propertyName );
	}

	@Parameterized.Parameters
	public static Collection<Object[]> artifactIds() {
		return Arrays.asList(new Object[][] {
	         { new DefaultArtifact( "com.coutemeier.maven.plugins:asciidoctor-create-theme-example:zip:1.0.0-SNAPSHOT" ), "asciidoctor-create-theme-example" },
	         { new DefaultArtifact( "com.coutemeier.maven.plugins:asciidoctor-create-theme-example:1.0.0-SNAPSHOT" ), "asciidoctor-create-theme-example"},
	         { new DefaultArtifact( "com.coutemeier.maven.plugins:theme-ñchar:1.0.0" ), "theme-char" },
	         { new DefaultArtifact( "com.coutemeier.maven.plugins:theme-char-0123456789:3.0.0" ), "theme-char-0123456789" }
	    });
	}
}
