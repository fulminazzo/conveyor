package it.fulminazzo.conveyor.downloader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

/**
 * A base implementation of {@link Downloader}.
 */
@RequiredArgsConstructor
final class BaseDownloader implements Downloader {
    @Getter
    private final @NotNull File workingDir;
    private final @NotNull Logger logger;

    @Override
    public @NotNull InputStream resolve(final @NotNull String resourcePath,
                                        final @NotNull Collection<DownloadSource> downloadSources) throws DownloadException {
        if (downloadSources.isEmpty())
            throw new DownloadException("No download source provided! Resolving will be interrupted");
        Throwable latest = null;
        for (DownloadSource source : downloadSources)
            try {
                return source.resolveResource(resourcePath);
            } catch (IOException e) {
                this.logger.debug("Could not resolve resource '{}' from source '{}'", resourcePath, source.getUrl());
                latest = e;
            }
        throw new DownloadException(String.format("Could not resolve resource '%s'", resourcePath), latest);
    }

    @Override
    public @NotNull File resolveToFile(final @NotNull String resourcePath,
                                       final @NotNull Collection<DownloadSource> downloadSources) throws DownloadException {
        try (InputStream stream = resolve(resourcePath, downloadSources)) {
            File destination = getResourceFile(resourcePath);
            Files.createDirectories(destination.getParentFile().toPath());
            Files.copy(stream, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destination;
        } catch (IOException e) {
            throw new DownloadException(String.format("Error while downloading resource '%s'", resourcePath), e);
        }
    }

    @Override
    public @NotNull File getResourceFile(final @NotNull String resourcePath) {
        return new File(getWorkingDir(), resourcePath);
    }

}
