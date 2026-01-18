package it.fulminazzo.conveyor.model.properties;

import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

final class MockProperties extends BaseProperties implements Map<String, String> {
    @Delegate
    private final @NotNull Map<String, String> delegate = new HashMap<>();

    @Override
    public @Nullable String get(final @NotNull String key) {
        return this.delegate.get(key);
    }

}
