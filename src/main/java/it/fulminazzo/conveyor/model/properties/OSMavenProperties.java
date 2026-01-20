package it.fulminazzo.conveyor.model.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link Properties} that supports all
 * the properties of the <a href="https://github.com/trustin/os-maven-plugin/">OS Maven Plugin</a>.
 */
final class OSMavenProperties extends BaseProperties {
    private final @NotNull Map<String, String> internal;
    private final @NotNull SystemProperties systemProperties = new SystemProperties();

    /**
     * Instantiates a new Os maven properties.
     */
    public OSMavenProperties() {
        this.internal = new HashMap<>();
    }

    @Override
    public @Nullable String get(final @NotNull String key) {
        return this.internal.get(key);
    }

}
