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
 * checks if the pom is already present, if it is it attempts
 * to compute and verify its checksum, otherwise proceeds to
 * download the resource.
 */
final class ChecksumDownloaderPomResolverEngine extends DownloaderPomResolverEngine {

    /**
     * Instantiates a new Checksum downloader pom resolver engine.
     *
     * @param workingDir the working dir
     * @param logger     the logger
     */
    public ChecksumDownloaderPomResolverEngine(final @NotNull File workingDir,
                                               final @NotNull Logger logger) {
        super(Downloader.newChecksumDownloader(workingDir, logger));
    }

    @Override
    protected @NotNull InputStream resolve(final @NotNull String artifactPath) throws DownloadException {
        try {
            File file = this.downloader.resolveToFile(artifactPath);
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            // should be impossible
            throw new DownloadException(e);
        }
    }

}
