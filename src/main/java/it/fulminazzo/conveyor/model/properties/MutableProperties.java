package it.fulminazzo.conveyor.model.properties;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A special type of {@link Properties} that supports adding of properties.
 */
public interface MutableProperties extends Properties {

    /**
     * Adds all the given properties to this object.
     *
     * @param properties the raw properties
     * @return this object (for method chaining)
     */
    default @NotNull MutableProperties addAll(final @NotNull Map<String, String> properties) {
        properties.forEach(this::add);
        return this;
    }

    /**
     * Adds the given property to this object.
     *
     * @param key   the key
     * @param value the value
     * @return this object (for method chaining)
     */
    @NotNull MutableProperties add(final @NotNull String key, final @NotNull String value);

    /**
     * Clears all the previously stored properties.
     *
     * @return this object (for method chaining)
     */
    @NotNull MutableProperties clear();

}
