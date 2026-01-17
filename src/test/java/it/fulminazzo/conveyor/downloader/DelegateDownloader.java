package it.fulminazzo.conveyor.downloader;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class DelegateDownloader implements Downloader {
    @Delegate
    private final @NotNull Downloader delegate;

}
