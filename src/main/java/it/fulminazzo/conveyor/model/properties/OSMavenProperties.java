package it.fulminazzo.conveyor.model.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * An implementation of {@link Properties} that supports all
 * the properties of the <a href="https://github.com/trustin/os-maven-plugin/">OS Maven Plugin</a>.
 * <br>
 * Supports the following properties:
 * <ul>
 *     <li><code>os.detected.name</code>;</li>
 * </ul>
 */
final class OSMavenProperties extends BaseProperties {
    private final @NotNull Map<String, String> internal;

    /**
     * Instantiates a new Os maven properties.
     *
     * @param systemProperties the system properties to lookup OS information
     */
    public OSMavenProperties(final @NotNull SystemProperties systemProperties) {
        this.internal = new HashMap<>();

        this.internal.put("os.detected.name", normalizeOs(systemProperties.get("os.name")));
    }

    @Override
    public @Nullable String get(final @NotNull String key) {
        return this.internal.get(key);
    }

    private static @NotNull String normalizeOs(@Nullable String value) {
        value = normalize(value);
        if (value.startsWith("aix")) return "aix";
        else if (value.startsWith("hpux")) return "hpux";
        else if (value.startsWith("os400")) {
            if (value.length() == 5 || !Character.isDigit(value.charAt(5))) return "os400";
        } else if (value.startsWith("linux")) return "linux";
        else if (value.startsWith("mac") || value.startsWith("osx")) return "osx";
        else if (value.startsWith("freebsd")) return "freebsd";
        else if (value.startsWith("openbsd")) return "openbsd";
        else if (value.startsWith("netbsd")) return "netbsd";
        else if (value.startsWith("solaris") || value.startsWith("sunos")) return "sunos";
        else if (value.startsWith("windows")) return "windows";
        else if (value.startsWith("zos")) return "zos";
        return "unknown";
    }

    private static @NotNull String normalize(final @Nullable String value) {
        if (value == null) return "";
        return value.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");
    }

}
