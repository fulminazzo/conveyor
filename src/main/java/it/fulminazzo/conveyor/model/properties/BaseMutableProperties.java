package it.fulminazzo.conveyor.model.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A base implementation of {@link MutableProperties}.
 */
final class BaseMutableProperties extends BaseProperties implements MutableProperties {
    private final @NotNull Map<String, String> map = new HashMap<>();

    @Override
    public @NotNull MutableProperties add(final @NotNull String key, final @NotNull String value) {
        this.map.put(key, value);
        return this;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public @Nullable String get(final @NotNull String key) {
        return this.map.get(key);
    }

    @Override
    public @NotNull MutableProperties clear() {
        this.map.clear();
        return this;
    }

    @Override
    public @NotNull Map<String, String> toMap() {
        return this.map;
    }

}
