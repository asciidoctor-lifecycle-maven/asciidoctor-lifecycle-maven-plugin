/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

/**
 * Aether Eclipse Artifact utils.
 *
 * @author rrialq
 * @since 1.0
 */
public final class ArtifactUtil {
    private static final Pattern ARTIFACT_NAME_NORMALIZER_RE = Pattern.compile( "[^a-zA-Z0-9-]" );

    private ArtifactUtil() {
    }

    /**
     * Download an artifact from a repository
     * <p>
     * For the moment This method assume that relocations has been resolved
     *
     * @param theme              the theme coordinates to download
     * @param repositorySystem   the entry point to Aether
     * @param session            the current repository/network configuration of Maven
     * @param remoteRepositories the remote repositories using for resolving the theme
     * @return the {@link Artifact} resolved
     * @throws ArtifactResolutionException if the theme can not be resolved
     * @throws MojoFailureException        if the theme coordinates are not valid
     * @author rrialq
     * @since 1.0.0
     */
    public static Artifact downloadByAether(
        final String theme,
        final RepositorySystem repositorySystem,
        final RepositorySystemSession session,
        final List< RemoteRepository > remoteRepositories )
        throws ArtifactResolutionException,
        MojoFailureException {
        final Artifact artifact;

        try {
            artifact = new DefaultArtifact( theme );
        } catch ( final IllegalArgumentException cause ) {
            throw new MojoFailureException( "Invalid theme coordinates: " + theme, cause );
        }
        final ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact( artifact );
        artifactRequest.setRepositories( remoteRepositories );

        final ArtifactResult artifactResult = repositorySystem.resolveArtifact( session, artifactRequest );
        return artifactResult.getArtifact();
    }

    /**
     * Normalize the artifactId of an artifact by removing all characters not in [A-Za-z0-9-].
     *
     * @param artifact the {@link Artifact} to normalize
     * @return a text with the artifactId normalized
     * @author rrialq
     * @since 1.0.0
     */
    public static String normalizeArtifactId( final Artifact artifact ) {
        Objects.requireNonNull( artifact, "The artifact can not be null" );
        return ARTIFACT_NAME_NORMALIZER_RE.matcher( artifact.getArtifactId() ).replaceAll( "" );
    }
}
