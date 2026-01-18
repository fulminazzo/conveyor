package it.fulminazzo.conveyor.model.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of {@link Properties} that provides
 * all the currently stored system properties.
 */
final class SystemProperties extends BaseProperties {

    @Override
    public @Nullable String get(final @NotNull String key) {
        return System.getProperty(key);
    }

}
