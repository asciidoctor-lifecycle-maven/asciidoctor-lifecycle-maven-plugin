package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Execution( ExecutionMode.CONCURRENT)
public class ArtifactUtilTestCase {
    @ParameterizedTest( name = "{index} => Artifact={0}, artifactId={1}" )
    @MethodSource( "artifactToArtifactIdProvider" )
    @Execution( ExecutionMode.CONCURRENT)
    public void normalizeArtifactIdTest( final Artifact artifact, final String expectedPropertyName ) {
        final String propertyName = ArtifactUtil.normalizeArtifactId( artifact );

        assertEquals( expectedPropertyName, propertyName );
    }

    private static Stream< Arguments > artifactToArtifactIdProvider() {
        return Stream.of(
            Arguments.of(   new DefaultArtifact( "com.coutemeier.maven.plugins:asciidoctor-create-theme-example:zip:1.0.0-SNAPSHOT"),
                            "asciidoctor-create-theme-example" ),
            Arguments.of(   new DefaultArtifact("com.coutemeier.maven.plugins:asciidoctor-create-theme-example:1.0.0-SNAPSHOT"),
                            "asciidoctor-create-theme-example" ),
            Arguments.of(   new DefaultArtifact("com.coutemeier.maven.plugins:theme-ñchar:1.0.0"),
                            "theme-char" ),
            Arguments.of( new DefaultArtifact("com.coutemeier.maven.plugins:theme-char-0123456789:3.0.0"),
                        "theme-char-0123456789" )
        );
    }
}
