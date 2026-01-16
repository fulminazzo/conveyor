package it.fulminazzo.conveyor.downloader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
final class MockDownloader implements Downloader {
    private final @NotNull Set<DownloadSource> downloadSources = new HashSet<>();
    private final @NotNull File workingDir;

    @Override
    public @NotNull InputStream resolve(final @NotNull String resourcePath) {
        return new ByteArrayInputStream(String.format("Data of '%s'", resourcePath).getBytes());
    }

    @Override
    public @NotNull Downloader addDownloadSources(final @NotNull Collection<DownloadSource> sources) {
        this.downloadSources.addAll(sources);
        return this;
    }

}
