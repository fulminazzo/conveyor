package it.fulminazzo.conveyor.model.pom.new_resolver;

import it.fulminazzo.conveyor.downloader.DownloadException;
import it.fulminazzo.conveyor.downloader.Downloader;
import it.fulminazzo.conveyor.manager.RepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;

/**
 * An implementation of {@link DownloaderPomResolver} that
 * downloads the requested pom in every request.
 */
final class MemoryDownloaderPomResolver extends DownloaderPomResolver {

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
        super(repositoryManager, Downloader.newDownloader(workingDir, logger));
    }

    @Override
    protected @NotNull InputStream resolve(@NotNull String artifactPath) throws DownloadException {
        return this.downloader.resolve(artifactPath);
    }

}
