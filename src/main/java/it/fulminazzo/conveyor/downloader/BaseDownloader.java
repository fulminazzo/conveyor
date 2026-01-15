package it.fulminazzo.conveyor.downloader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A base implementation of {@link Downloader}.
 */
@RequiredArgsConstructor
final class BaseDownloader implements Downloader {
    private final @NotNull Set<DownloadSource> sources = new HashSet<>();
    @Getter
    private final @NotNull File workingDir;

    @Override
    public @NotNull InputStream resolve(final @NotNull String resourcePath) throws DownloadException {
        if (this.sources.isEmpty())
            throw new DownloadException("No download source provided! Please, use addDownloadSources before calling this method");
        Throwable latest = null;
        for (DownloadSource source : this.sources)
            try {
                return source.resolveResource(resourcePath);
            } catch (IOException e) {
                latest = e;
            }
        throw new DownloadException(String.format("Could not download resource '%s'", resourcePath), latest);
    }

    @Override
    public @NotNull Downloader addDownloadSources(final @NotNull Collection<DownloadSource> sources) {
        this.sources.addAll(sources);
        return this;
    }

}
