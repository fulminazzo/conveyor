package it.fulminazzo.conveyor.downloader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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
    public @NotNull Downloader addDownloadSources(final @NotNull Collection<DownloadSource> sources) {
        this.sources.addAll(sources);
        return this;
    }

}
