package it.fulminazzo.conveyor.downloader;

import it.fulminazzo.conveyor.util.HttpUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a source to download resources from.
 */
@EqualsAndHashCode
@ToString
public final class DownloadSource {
    @Getter
    private final @NotNull String url;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final @NotNull Map<Class<?>, Object> capabilities = new HashMap<>();

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
        this.url = HttpUtils.formatUrl(url);
    }

    /**
     * Attempts to fetch the given resource path at the current url.
     *
     * @param resourcePath the resource path
     * @return the data
     * @throws IOException in case of any errors (usually connection or not found)
     */
    public @NotNull InputStream resolveResource(final @NotNull String resourcePath) throws IOException {
        return HttpUtils.openHttpConnection(this.url, resourcePath);
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
