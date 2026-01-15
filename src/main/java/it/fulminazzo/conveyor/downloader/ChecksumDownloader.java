package it.fulminazzo.conveyor.downloader;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

/**
 * A special type of {@link Downloader} that supports
 * <b>checksum</b> checking, to verify if the resource
 * already present on disk is valid.
 * <br>
 * It expects the corresponding checksum to be at
 * "&lt;url&gt;/&lt;resource_path&gt;.&lt;checksum_algorithm&gt;"
 */
@RequiredArgsConstructor
final class ChecksumDownloader implements Downloader {
    private final @NotNull Downloader delegate;

    @Override
    public @NotNull InputStream resolve(final @NotNull String resourcePath) throws DownloadException {
        return this.delegate.resolve(resourcePath);
    }

    @Override
    public @NotNull Downloader addDownloadSources(final @NotNull Collection<DownloadSource> sources) {
        return this.delegate.addDownloadSources(sources);
    }

    @Override
    public @NotNull File getWorkingDir() {
        return this.delegate.getWorkingDir();
    }

}
