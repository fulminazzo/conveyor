package it.fulminazzo.conveyor.model.artifact.resolver.mode;

import it.fulminazzo.conveyor.downloader.DownloadException;
import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.downloader.Downloader;
import it.fulminazzo.conveyor.manager.RepositoryManager;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.artifact.resolver.ArtifactResolver;
import it.fulminazzo.conveyor.model.artifact.resolver.ArtifactResolverException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;

/**
 * An implementation of {@link ArtifactResolver} that
 * uses a {@link Downloader} to resolve the artifact.
 */
@RequiredArgsConstructor
abstract class BaseArtifactResolver implements ArtifactResolver {
    private final @NotNull RepositoryManager repositoryManager;

    @Override
    public @NotNull File resolve(final @NotNull Artifact artifact,
                                 final @NotNull String packaging) throws ArtifactResolverException {
        try {
            final String artifactPath = artifact.getFullPath(packaging);
            boolean snapshots = artifact.getVersion().endsWith("-SNAPSHOT");
            return resolve(artifactPath, snapshots ?
                    this.repositoryManager.getSnapshotsRepositories() :
                    this.repositoryManager.getReleasesRepositories()
            );
        } catch (DownloadException e) {
            throw new ArtifactResolverException(artifact, e);
        }
    }

    /**
     * Resolves the artifact.
     *
     * @param artifactPath    the artifact path
     * @param downloadSources the download sources
     * @return the artifact file
     * @throws DownloadException in case of download errors
     */
    protected abstract @NotNull File resolve(final @NotNull String artifactPath,
                                             final @NotNull Collection<DownloadSource> downloadSources
    ) throws DownloadException;

}
