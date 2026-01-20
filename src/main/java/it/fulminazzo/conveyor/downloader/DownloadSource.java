package it.fulminazzo.conveyor.downloader;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a source to download resources from.
 */
@EqualsAndHashCode
@ToString
public final class DownloadSource {
    /**
     * The timeout for connection and reading operations.
     */
    static final int CONNECT_READ_TIMEOUT = 10000;

    @Getter
    private final @NotNull String url;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final @NotNull Map<Class<?>, Object> capabilities = new HashMap<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final @NotNull Logger logger;

    /**
     * Checks if the given URL is valid, then instantiates a new DownloadSource.
     * <br>
     * <b>WARNING</b>: only <i>base</i> URLs should be passed.
     * <br>
     * If the goal is to download a resource in "https://www.example.com/resource/path",
     * then here only "https://www.example.com/" should be provided.
     *
     * @param url the url
     * @throws MalformedURLException if the URL is invalid
     */
    public DownloadSource(final @NotNull String url) throws MalformedURLException {
        String modifiedUrl = url;
        if (!modifiedUrl.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*$")) modifiedUrl = "https://" + modifiedUrl;
        if (!modifiedUrl.endsWith("/")) modifiedUrl += "/";
        try {
            new URI(modifiedUrl);
        } catch (URISyntaxException e) {
            throw new MalformedURLException(String.format("Invalid URL '%s'", url));
        }
        this.url = modifiedUrl;
        this.logger = LoggerFactory.getLogger(String.format("%s(%s)", getClass().getSimpleName(), modifiedUrl));
    }

    /**
     * Attempts to fetch the given resource path at the current url.
     *
     * @param resourcePath the resource path
     * @return the data
     * @throws IOException in case of any errors (usually connection or not found)
     */
    public @NotNull InputStream resolveResource(@NotNull String resourcePath) throws IOException {
        try {
            if (resourcePath.startsWith("/")) resourcePath = resourcePath.substring(1);
            HttpURLConnection connection = (HttpURLConnection) new URL(this.url + resourcePath).openConnection();
            connection.setConnectTimeout(CONNECT_READ_TIMEOUT);
            connection.setReadTimeout(CONNECT_READ_TIMEOUT);
            int status = connection.getResponseCode();
            this.logger.debug("{} /{} HTTP/1.1 - {}", connection.getRequestMethod(), resourcePath, status);
            if (status == HttpURLConnection.HTTP_MOVED_PERM ||
                    status == HttpURLConnection.HTTP_MOVED_TEMP ||
                    status == 307 || status == 308) {
                String newUrl = connection.getHeaderField("Location");
                this.logger.debug("Redirected to {}", newUrl);
                //TODO: handle redirect
            }
            return connection.getInputStream();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Unreachable code");
        }
    }

    /**
     * Adds the given capability to the current source.
     *
     * @param <T>        the type of the capability
     * @param capability the capability
     * @return this source
     */
    public <T> @NotNull DownloadSource withCapability(final @NotNull T capability) {
        this.capabilities.put(capability.getClass(), capability);
        return this;
    }

    /**
     * Gets the capability corresponding to the given class.
     *
     * @param <T>  the type of the capability
     * @param type the class of the capability
     * @return the capability (if present)
     */
    public <T> @NotNull Optional<T> getCapability(final @NotNull Class<T> type) {
        Object rawCapability = this.capabilities.get(type);
        if (rawCapability == null)
            for (Class<?> clazz : this.capabilities.keySet())
                if (type.isAssignableFrom(clazz)) {
                    rawCapability = this.capabilities.get(clazz);
                    break;
                }
        return Optional.ofNullable(type.cast(rawCapability));
    }

}
