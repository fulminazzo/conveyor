package it.fulminazzo.conveyor.model.properties;

import it.fulminazzo.conveyor.model.pom.Pom;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * An implementation of {@link Properties} that provides
 * all the properties of the given {@link Pom}.
 */
@RequiredArgsConstructor
final class PomProperties extends BaseProperties {
    private static final @NotNull List<String> prefixes = Arrays.asList("project.", "pom.");

    private final @NotNull Pom pom;

    @Override
    public @Nullable String get(@NotNull String key) {
        for (String prefix : prefixes)
            if (key.startsWith(prefix)) {
                key = key.substring(prefix.length());
                break; // avoid invalid chaining of prefixes
            }
        return this.pom.getProperty(key);
    }

}
