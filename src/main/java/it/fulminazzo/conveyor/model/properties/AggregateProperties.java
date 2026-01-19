package it.fulminazzo.conveyor.model.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A special type of {@link Properties} that supports properties aggregation.
 * It will use all the aggregated other properties to lookup.
 */
final class AggregateProperties extends BaseProperties implements Properties {
    private final @NotNull List<Properties> others = new ArrayList<>();

    /**
     * Aggregates a new properties object to this aggregate.
     *
     * @param properties the properties
     * @return this properties object
     */
    public @NotNull AggregateProperties addProperties(final @NotNull Properties properties) {
        this.others.add(properties);
        return this;
    }

    @Override
    public @Nullable String get(final @NotNull String key) {
        for (Properties properties : this.others) {
            String result = properties.get(key);
            if (result != null) return result;
        }
        return null;
    }

}
