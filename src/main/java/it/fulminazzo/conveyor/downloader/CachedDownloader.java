package it.fulminazzo.conveyor.downloader;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;

/**
 * A special {@link Downloader} that will check if the requested
 * resource is already present on disk before downloading.
 * If found, it will attempt to verify its validity through checksum.
 */
final class CachedDownloader extends DownloaderImpl {

    /**
     * Instantiates a new Cached downloader.
     *
     * @param workingDir the working dir
     */
    public CachedDownloader(final @NotNull File workingDir) {
        super(workingDir);
    }

    @Override
    public @NotNull File resolveToFile(final @NotNull String resourcePath) throws DownloadException {
        throw new UnsupportedOperationException("Should check cache");
    }

    @Override
    public @NotNull InputStream resolve(final @NotNull String resourcePath) throws DownloadException {
        throw new UnsupportedOperationException("Should check cache");
    }

}
