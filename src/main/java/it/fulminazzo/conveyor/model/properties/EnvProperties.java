package it.fulminazzo.conveyor.model.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of {@link Properties} that provides
 * all the currently stored environment properties.
 */
final class EnvProperties extends BaseProperties {
    private static final @NotNull String prefix = "env.";

    @Override
    public @Nullable String get(final @NotNull String key) {
        if (key.startsWith(prefix))
            return System.getenv(key.substring(prefix.length()));
        else return null;
    }

}
