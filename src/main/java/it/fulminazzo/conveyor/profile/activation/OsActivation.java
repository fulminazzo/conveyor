package it.fulminazzo.conveyor.profile.activation;

import it.fulminazzo.conveyor.profile.activation.context.ActivationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * An {@link Activation} based on the current operating system.
 *
 * @param name    the name
 * @param family  the family
 * @param arch    the architecture
 * @param version the version (can be specified as "regex: &lt;regex&gt;")
 */
record OsActivation(
        @Nullable String name,
        @Nullable String family,
        @Nullable String arch,
        @Nullable String version
) implements Activation {
    private static final @NotNull String macFamily = "mac";
    private static final @NotNull String linuxFamily = "linux";
    private static final @NotNull String unixFamily = "unix";
    private static final @NotNull Map<String, String> familiesMap = new HashMap<>() {{
        put("windows", "windows");
        put("mac", macFamily);
        put("linux", linuxFamily);
        put("dos", "dos");
        put("ms-dos", "dos");
        put("os/2", "os/2");
        put("netware", "netware");
        put("os/400", "os/400");
        put("z/os", "z/os");
        put("os/390", "z/os");
        put("nonstop-kernel", "tandem");
        put("openvms", "openvms");
    }};

    private static final @NotNull String regexPrefix = "regex:";

    @Override
    public boolean isEnabled(final @NotNull ActivationContext context) {
        if (this.name != null && checkValue(this.name, context.getOsName())) return false;
        if (this.family != null) {
            String expected = this.family;
            boolean negated = expected.startsWith(NEGATION);
            if (negated) expected = expected.substring(1);

            final String actual = getFamily(context.getOsName());
            boolean isMatch;

            if (expected.equalsIgnoreCase(unixFamily)) {
                isMatch = actual.equals(unixFamily) || actual.equals(macFamily) || actual.equals(linuxFamily);
            } else {
                isMatch = expected.toLowerCase().contains(actual.toLowerCase());
            }

            if (negated == isMatch) return false;
        }
        if (this.arch != null && checkValue(this.arch, context.getOsArch())) return false;
        if (this.version != null) {
            final String actualVersion = context.getOsVersion();
            if (this.version.startsWith(regexPrefix)) {
                String regex = this.version.substring(regexPrefix.length());
                return actualVersion.matches(regex);
            } else if (checkValue(this.version, actualVersion)) return false;
        }
        return true;
    }

    private @NotNull String getFamily(final @NotNull String osName) {
        for (String key : familiesMap.keySet())
            if (osName.contains(key)) return familiesMap.get(key);
        return unixFamily;
    }

    /**
     * Checks the given value against the expected one.
     *
     * @param expected the expected value
     * @param actual   the actual value
     * @return if the expected starts with {@link #NEGATION},
     * checks that the two values are not equal.
     * Otherwise, checks that they are.
     */
    private boolean checkValue(@NotNull String expected,
                               final @NotNull String actual) {
        boolean negated = expected.startsWith(NEGATION);
        if (negated) expected = expected.substring(1);
        if (expected.toLowerCase().contains(actual.toLowerCase())) return negated;
        return !negated;
    }

}
