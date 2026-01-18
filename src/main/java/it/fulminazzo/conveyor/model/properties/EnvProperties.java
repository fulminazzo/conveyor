package it.fulminazzo.conveyor.model.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of {@link Properties} that provides
 * all the currently stored environment properties.
 */
final class EnvProperties extends BaseProperties {

    @Override
    public @Nullable String get(final @NotNull String key) {
        final String prefix = "env.";
        if (key.startsWith(prefix))
            return System.getenv(key.substring(prefix.length()));
        else return null;
    }

}
