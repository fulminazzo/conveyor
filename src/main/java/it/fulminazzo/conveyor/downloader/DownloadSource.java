package it.fulminazzo.conveyor.downloader;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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
     * <b>WARNING</b>: only <i>base</i> urls should be passed.
     * <br>
     * If the goal is to download a resource in "https://www.example.com/resource/path",
     * then here only "https://www.example.com/" should be provided.
     *
     * @param url the url
     * @throws MalformedURLException if the URL is invalid
     */
    public DownloadSource(final @NotNull String url) throws MalformedURLException {
        String modifiedUrl = url;
        String https = "https://";
        if (!modifiedUrl.startsWith(https) && !modifiedUrl.startsWith("http://")) modifiedUrl = https + modifiedUrl;
        if (!modifiedUrl.endsWith("/")) modifiedUrl += "/";
        try {
            new URI(modifiedUrl);
        } catch (URISyntaxException e) {
            throw new MalformedURLException(String.format("Invalid URL '%s'", url));
        }
        this.url = modifiedUrl;
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
        return Optional.ofNullable(type.cast(rawCapability));
    }

}
