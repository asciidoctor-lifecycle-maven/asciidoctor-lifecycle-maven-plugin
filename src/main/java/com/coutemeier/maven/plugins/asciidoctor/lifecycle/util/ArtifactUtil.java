/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coutemeier.maven.plugins.asciidoctor.lifecycle.util;

import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;


public class ArtifactUtil {
	/**
	 * Download an artifact from a repository
	 * <p>
	 * For the moment This method assume that relocations has been resolved
	 * @author rrialq
	 * @since 1.0.0
	 */
	public static Artifact downloadByAether(
			final String theme,
			final RepositorySystem repositorySystem,
			final RepositorySystemSession session,
			final List<RemoteRepository> remoteRepositories)
			throws IOException, ArtifactResolutionException, MojoFailureException {
		final Artifact artifact;

		try {
			artifact = new DefaultArtifact( theme );
		} catch ( final IllegalArgumentException cause ) {
			throw new MojoFailureException( "Invalid theme coordinates: " + theme, cause );
		}
		final ArtifactRequest artifactRequest = new ArtifactRequest();
		artifactRequest.setArtifact(artifact);
		artifactRequest.setRepositories(remoteRepositories);

		final ArtifactResult artifactResult = repositorySystem.resolveArtifact( session, artifactRequest );
		return artifactResult.getArtifact();
	}
}
