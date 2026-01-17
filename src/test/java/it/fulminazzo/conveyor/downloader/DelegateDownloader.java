package it.fulminazzo.conveyor.downloader;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

@RequiredArgsConstructor
public final class DelegateDownloader implements Downloader {
    private final @NotNull Downloader delegate;

    @Override
    public @NotNull InputStream resolve(@NotNull String resourcePath) throws DownloadException {
        return this.delegate.resolve(resourcePath);
    }

    @Override
    public @NotNull Collection<DownloadSource> getDownloadSources() {
        return this.delegate.getDownloadSources();
    }

    @Override
    public @NotNull Downloader addDownloadSources(@NotNull Collection<DownloadSource> sources) {
        this.delegate.addDownloadSources(sources);
        return this;
    }

    @Override
    public @NotNull File getWorkingDir() {
        return this.delegate.getWorkingDir();
    }

}
