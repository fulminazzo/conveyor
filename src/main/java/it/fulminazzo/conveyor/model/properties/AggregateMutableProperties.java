package it.fulminazzo.conveyor.model.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A special type of {@link MutableProperties} that supports properties aggregation.
 * It will first attempt to fetch the requested property from the stored properties.
 * If it fails, it will use all the aggregated other properties to lookup.
 */
final class AggregateMutableProperties extends BaseProperties implements MutableProperties {
    private final @NotNull MutableProperties mutable = new BaseMutableProperties();
    final @NotNull AggregateProperties delegate = new AggregateProperties();

    /**
     * Aggregates a new properties object to this aggregate.
     *
     * @param properties the properties
     * @return this properties object
     */
    public @NotNull AggregateMutableProperties addProperties(final @NotNull Properties properties) {
        this.delegate.addProperties(properties);
        return this;
    }

    @Override
    public @Nullable String get(final @NotNull String key) {
        String result = this.mutable.get(key);
        if (result != null) return result;
        else return this.delegate.get(key);
    }

    @Override
    public @NotNull MutableProperties add(@NotNull String key, @NotNull String value) {
        this.mutable.add(key, value);
        return this;
    }

    @Override
    public @NotNull MutableProperties clear() {
        this.mutable.clear();
        return this;
    }

}
