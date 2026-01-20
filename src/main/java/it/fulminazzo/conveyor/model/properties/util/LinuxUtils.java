package it.fulminazzo.conveyor.model.properties.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A collection of utilities to work with Linux based operating systems.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LinuxUtils {

    /**
     * Represents Linux release data.
     *
     * @param id      the id
     * @param version the version
     * @param like    the like
     */
    public record Release(@NotNull String id,
                          @NotNull String version,
                          @NotNull Collection<String> like) {

    }

}
