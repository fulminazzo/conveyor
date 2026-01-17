package it.fulminazzo.conveyor.model.pom.resolver.engine;

import it.fulminazzo.conveyor.downloader.DownloadException;
import it.fulminazzo.conveyor.downloader.Downloader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;

/**
 * An implementation of {@link DownloaderPomResolverEngine} that
 * downloads the requested pom for each request.
 */
final class MemoryDownloaderPomResolverEngine extends DownloaderPomResolverEngine {

    /**
     * Instantiates a new Memory downloader pom resolver engine.
     *
     * @param workingDir the working dir
     * @param logger     the logger
     */
    public MemoryDownloaderPomResolverEngine(final @NotNull File workingDir,
                                             final @NotNull Logger logger) {
        super(Downloader.newDownloader(workingDir, logger));
    }

    @Override
    protected @NotNull InputStream resolve(final @NotNull String artifactPath) throws DownloadException {
        return this.downloader.resolve(artifactPath);
    }

}
