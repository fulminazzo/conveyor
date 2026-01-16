package it.fulminazzo.conveyor.downloader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

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
    @Getter
    private final @NotNull Set<DownloadSource> downloadSources = new HashSet<>();
    @Getter
    private final @NotNull File workingDir;
    private final @NotNull Logger logger;

    @Override
    public @NotNull InputStream resolve(final @NotNull String resourcePath) throws DownloadException {
        if (this.downloadSources.isEmpty())
            throw new DownloadException("No download source provided! Please, use addDownloadSources before calling this method");
        Throwable latest = null;
        for (DownloadSource source : this.downloadSources)
            try {
                return source.resolveResource(resourcePath);
            } catch (IOException e) {
                this.logger.debug("Could not resolve resource '{}' from source '{}'", resourcePath, source.getUrl());
                latest = e;
            }
        throw new DownloadException(String.format("Could not resolve resource '%s'", resourcePath), latest);
    }

    @Override
    public @NotNull Downloader addDownloadSources(final @NotNull Collection<DownloadSource> sources) {
        this.downloadSources.addAll(sources);
        return this;
    }

}
