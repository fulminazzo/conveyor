package it.fulminazzo.conveyor.model.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A special type of {@link MutableProperties} that supports properties aggregation.
 * It will first attempt to fetch the requested property from the stored properties.
 * If it fails, it will use all the aggregated other properties to lookup.
 */
final class AggregateProperties extends BaseProperties implements MutableProperties {
    private final @NotNull MutableProperties delegate = new BaseMutableProperties();
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
        String result = this.delegate.get(key);
        if (result != null) return result;
        for (Properties properties : this.others) {
            result = properties.get(key);
            if (result != null) return result;
        }
        return null;
    }

    @Override
    public @NotNull MutableProperties add(@NotNull String key, @NotNull String value) {
        this.delegate.add(key, value);
        return this;
    }

}
