package it.fulminazzo.conveyor.model.artifact.resolver.mode;

import it.fulminazzo.conveyor.downloader.DownloadException;
import it.fulminazzo.conveyor.downloader.Downloader;
import it.fulminazzo.conveyor.manager.RepositoryManager;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.artifact.resolver.ArtifactResolver;
import it.fulminazzo.conveyor.model.artifact.resolver.ArtifactResolverException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * An implementation of {@link ArtifactResolver} that
 * uses a {@link Downloader} to resolve the artifact.
 */
@RequiredArgsConstructor
final class DownloaderArtifactResolver implements ArtifactResolver {
    private final @NotNull RepositoryManager repositoryManager;
    private final @NotNull Downloader downloader;

    @Override
    public @NotNull File resolve(final @NotNull Artifact artifact, 
                                 final @NotNull String packaging) throws ArtifactResolverException {
        try {
            final String artifactPath = artifact.getFullPath(packaging);
            boolean snapshots = artifact.getVersion().endsWith("-SNAPSHOT");
            return this.downloader.resolveToFile(artifactPath, snapshots ?
                    this.repositoryManager.getSnapshotsRepositories() :
                    this.repositoryManager.getReleasesRepositories()
            );
        } catch (DownloadException e) {
            throw new ArtifactResolverException(artifact, e);
        }
    }

}
