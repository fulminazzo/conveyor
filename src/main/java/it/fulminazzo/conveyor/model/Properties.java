package it.fulminazzo.conveyor.model;

import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A special {@link Map} that does not allow overriding of existing values.
 */
@ToString(includeFieldNames = false)
public final class Properties implements Map<String, String> {
    private static final @NotNull Pattern PROPERTY_REGEX = Pattern.compile("\\$\\{([^}]+)}");

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
    public @Nullable String put(String s, String s2) {
        if (!containsKey(s)) return this.delegate.put(s, s2);
        else return get(s);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends String> map) {
        map.forEach(this::put);
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return this.delegate.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return this.delegate.containsValue(o);
    }

    @Override
    public String get(Object o) {
        return this.delegate.get(o);
    }

    @Override
    public String remove(Object o) {
        return this.delegate.remove(o);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public @NotNull Set<String> keySet() {
        return this.delegate.keySet();
    }

    @Override
    public @NotNull Collection<String> values() {
        return this.delegate.values();
    }

    @Override
    public @NotNull Set<Entry<String, String>> entrySet() {
        return this.delegate.entrySet();
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
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
