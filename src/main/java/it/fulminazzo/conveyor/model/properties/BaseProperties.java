package it.fulminazzo.conveyor.model.properties;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An abstract implementation of {@link Properties}.
 * Implements the {@link #apply(String)} method.
 */
abstract class BaseProperties implements Properties {
    private static final @NotNull Pattern PROPERTY_REGEX = Pattern.compile("\\$\\{([^}]+)}");

    @Override
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

    /**
     * Formats the given string to the expected property format.
     *
     * @param string the string
     * @return the formatted string
     */
    static @NotNull String formatToProperty(final @NotNull String string) {
        return String.format("${%s}", string);
    }

}
