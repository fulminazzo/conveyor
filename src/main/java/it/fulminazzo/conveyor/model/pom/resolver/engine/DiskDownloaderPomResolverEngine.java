package it.fulminazzo.conveyor.model.pom.resolver.engine;

import it.fulminazzo.conveyor.downloader.DownloadException;
import it.fulminazzo.conveyor.downloader.Downloader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * An implementation of {@link DownloaderPomResolverEngine} that
 * downloads the requested pom only if it is not already present
 * on disk (does not check for its validity).
 */
final class DiskDownloaderPomResolverEngine extends DownloaderPomResolverEngine<Downloader> {

    /**
     * Instantiates a new Disk downloader pom resolver engine.
     *
     * @param workingDir the working dir
     * @param logger     the logger
     */
    public DiskDownloaderPomResolverEngine(final @NotNull File workingDir,
                                           final @NotNull Logger logger) {
        super(Downloader.newDownloader(workingDir, logger));
    }

    @Override
    protected @NotNull InputStream resolve(final @NotNull String artifactPath) throws DownloadException {
        try {
            File file = this.downloader.getResourceFile(artifactPath);
            if (!file.exists())
                file = this.downloader.resolveToFile(artifactPath);
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            // should be impossible
            throw new DownloadException(e);
        }
    }

}
