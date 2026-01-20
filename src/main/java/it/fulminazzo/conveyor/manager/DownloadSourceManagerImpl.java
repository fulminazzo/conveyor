package it.fulminazzo.conveyor.manager;

import it.fulminazzo.conveyor.downloader.DownloadSource;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A basic implementation for {@link DownloadSourceManager}.
 */
final class DownloadSourceManagerImpl implements DownloadSourceManager {
    private final @NotNull Set<DownloadSource> downloadSources = new LinkedHashSet<>();

    @Override
    public @NotNull DownloadSourceManager addDownloadSources(final @NotNull Collection<DownloadSource> sources) {
        this.downloadSources.addAll(sources);
        return this;
    }

    @Override
    public @NotNull Collection<DownloadSource> getDownloadSources() {
        return this.downloadSources;
    }

}
