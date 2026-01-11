package it.fulminazzo.conveyor.downloader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Basic implementation of {@link Downloader}.
 */
@RequiredArgsConstructor
class DownloaderImpl implements Downloader {
    static final int CONNECT_READ_TIMEOUT = 10000;

    @Getter
    private final @NotNull File workingDir;
    private final @NotNull Set<String> baseUrls = new LinkedHashSet<>();

    @Override
    public @NotNull InputStream resolve(@NotNull String resourcePath) throws DownloadException {
        if (this.baseUrls.isEmpty())
            throw new DownloadException("No base URL provided! Please, use addBaseUrls before calling this method");
        if (resourcePath.startsWith("/")) resourcePath = resourcePath.substring(1);
        Throwable latest = null;
        for (String url : this.baseUrls)
            try {
                URLConnection connection = new URL(url + resourcePath).openConnection();
                connection.setConnectTimeout(CONNECT_READ_TIMEOUT);
                connection.setReadTimeout(CONNECT_READ_TIMEOUT);
                return connection.getInputStream();
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Unreachable code");
            } catch (IOException e) {
                latest = e;
            }
        throw new DownloadException(String.format("Could not download resource '%s'", resourcePath), latest);
    }

    @Override
    public @NotNull Downloader addBaseUrls(final @NotNull Collection<String> urls) throws MalformedURLException {
        List<String> parsedUrls = new ArrayList<>();
        for (String url : urls) {
            String https = "https://";
            if (!url.startsWith(https) && !url.startsWith("/")) url = https + url;
            if (!url.endsWith("/")) url += "/";
            new URL(url);
            parsedUrls.add(url);
        }
        this.baseUrls.addAll(parsedUrls);
        return this;
    }

}
