package it.fulminazzo.conveyor.downloader;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a source to download resources from.
 */
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public final class DownloadSource {
    @Getter
    private final @NotNull String url;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final @NotNull Map<Class<?>, Object> capabilities = new HashMap<>();

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
