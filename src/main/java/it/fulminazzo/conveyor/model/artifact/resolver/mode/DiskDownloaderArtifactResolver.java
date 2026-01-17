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
 * downloads the requested artifact only if it is not already present
 * on disk (does not check for its validity).
 */
final class DiskDownloaderArtifactResolver extends BaseArtifactResolver {
    private final @NotNull Downloader downloader;

    /**
     * Instantiates a new Disk downloader artifact resolver.
     *
     * @param repositoryManager the repository manager
     * @param workingDir        the working dir
     * @param logger            the logger
     */
    public DiskDownloaderArtifactResolver(final @NotNull RepositoryManager repositoryManager,
                                          final @NotNull File workingDir,
                                          final @NotNull Logger logger) {
        super(repositoryManager);
        this.downloader = Downloader.newDownloader(workingDir, logger);
    }

    @Override
    protected @NotNull File resolve(final @NotNull String artifactPath,
                                    final @NotNull Collection<DownloadSource> downloadSources) throws DownloadException {
        File file = this.downloader.getResourceFile(artifactPath);
        if (file.exists()) return file;
        else return this.downloader.resolveToFile(artifactPath, downloadSources);
    }

}
