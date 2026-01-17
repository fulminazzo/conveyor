package it.fulminazzo.conveyor.model.pom.resolver.mode;

import it.fulminazzo.conveyor.downloader.DownloadException;
import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.downloader.Downloader;
import it.fulminazzo.conveyor.manager.RepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

/**
 * An implementation of {@link BasePomResolver} that
 * downloads the requested pom in every request.
 */
final class MemoryDownloaderPomResolver extends BasePomResolver {
    private final @NotNull Downloader downloader;

    /**
     * Instantiates a new Memory downloader pom resolver.
     *
     * @param repositoryManager the repository manager
     * @param workingDir        the working dir
     * @param logger            the logger
     */
    public MemoryDownloaderPomResolver(final @NotNull RepositoryManager repositoryManager,
                                       final @NotNull File workingDir,
                                       final @NotNull Logger logger) {
        super(repositoryManager);
        this.downloader = Downloader.newDownloader(workingDir, logger);
    }

    @Override
    protected @NotNull InputStream resolve(final @NotNull String artifactPath,
                                           final @NotNull Collection<DownloadSource> downloadSources) throws DownloadException {
        return this.downloader.resolve(artifactPath, downloadSources);
    }

}
