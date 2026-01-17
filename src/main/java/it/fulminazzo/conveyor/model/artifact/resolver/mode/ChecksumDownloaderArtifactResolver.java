package it.fulminazzo.conveyor.model.artifact.resolver.mode;

import it.fulminazzo.conveyor.downloader.DownloadException;
import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.downloader.Downloader;
import it.fulminazzo.conveyor.manager.RepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.util.Collection;

/**
 * An implementation of {@link BaseArtifactResolver} that
 * checks if the file is already present, if it is it attempts
 * to compute and verify its checksum, otherwise proceeds to
 * download the resource.
 */
final class ChecksumDownloaderArtifactResolver extends BaseArtifactResolver {
    private final @NotNull Downloader downloader;

    /**
     * Instantiates a new Checksum downloader artifact resolver.
     *
     * @param repositoryManager the repository manager
     * @param workingDir        the working dir
     * @param logger            the logger
     */
    public ChecksumDownloaderArtifactResolver(final @NotNull RepositoryManager repositoryManager,
                                              final @NotNull File workingDir,
                                              final @NotNull Logger logger) {
        super(repositoryManager);
        this.downloader = Downloader.newChecksumDownloader(workingDir, logger);
    }

    @Override
    protected @NotNull File resolve(final @NotNull String artifactPath,
                                    final @NotNull Collection<DownloadSource> downloadSources) throws DownloadException {
        return this.downloader.resolveToFile(artifactPath, downloadSources);
    }

}
