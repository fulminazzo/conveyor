package it.fulminazzo.conveyor.downloader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
final class MockDownloader implements Downloader {
    private final @NotNull File workingDir;

    @Override
    public @NotNull InputStream resolve(final @NotNull String resourcePath,
                                        final @NotNull Collection<DownloadSource> downloadSources) {
        return new ByteArrayInputStream(String.format("Data of '%s'", resourcePath).getBytes());
    }

}
