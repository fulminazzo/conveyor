package it.fulminazzo.conveyor.model;

import lombok.ToString;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A special {@link Map} to represent properties.
 * Provides an {@link #apply(String)} method.
 */
@ToString(includeFieldNames = false)
public final class Properties implements Map<String, String> {
    private static final @NotNull Pattern PROPERTY_REGEX = Pattern.compile("\\$\\{([^}]+)}");

    @Delegate
    private final @NotNull Map<String, String> delegate = new HashMap<>();

    /**
     * Instantiates a new Properties.
     */
    public Properties() {
    }

    /**
     * Instantiates a new Properties.
     *
     * @param map the map
     */
    public Properties(final @NotNull Map<String, String> map) {
        putAll(map);
    }

    /**
     * Applies the currently stored properties to the given string.
     *
     * @param string the string
     * @return the applied string
     */
    public @NotNull String apply(@NotNull String string) {
        Matcher matcher = PROPERTY_REGEX.matcher(string);
        Set<String> set = new HashSet<>();
        while (matcher.find()) {
            String key = matcher.group(1);
            if (set.contains(key)) continue;
            set.add(key);
            String value = get(key);
            if (value != null) {
                string = string.replace(formatToProperty(key), value);
                matcher = PROPERTY_REGEX.matcher(string);
            }
        }
        return string;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Properties properties)
            return this.delegate.equals(properties.delegate);
        if (obj instanceof Map<?,?>)
            return this.delegate.equals(obj);
        else return false;
    }

    /**
     * Formats the given string to the expected property format.
     *
     * @param string the string
     * @return the formatted string
     */
    public static @NotNull String formatToProperty(final @NotNull String string) {
        return String.format("${%s}", string);
    }

}
