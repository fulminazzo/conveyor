package it.fulminazzo.conveyor.model.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A basic object to replace keys in a certain format with values.
 */
public interface Properties {

    /**
     * Applies all the properties to the given string.
     *
     * @param string the string
     * @return the applied string
     */
    @NotNull String apply(final @NotNull String string);

    /**
     * Gets the value of the property.
     *
     * @param key the key of the property
     * @return the value
     */
    @Nullable String get(final @NotNull String key);

}
