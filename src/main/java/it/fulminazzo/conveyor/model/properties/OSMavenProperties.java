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
    public OSMavenProperties(final @NotNull Properties systemProperties) {
        this.internal = new HashMap<>();

        this.internal.put("os.detected.name", normalizeOs(systemProperties.get("os.name")));
        String arch = normalizeArch(systemProperties.get("os.arch"));
        this.internal.put("os.detected.arch", arch);
        this.internal.put("os.detected.bitness", String.valueOf(determineBitness(systemProperties, arch)));
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

    private static String normalizeArch(@Nullable String value) {
        value = normalize(value);
        if (value.matches("^(x8664|amd64|ia32e|em64t|x64)$")) return "x86_64";
        if (value.matches("^(x8632|x86|i[3-6]86|ia32|x32)$")) return "x86_32";
        if (value.matches("^(ia64w?|itanium64)$")) return "itanium_64";
        if (value.matches("^(sparc|sparc32)$")) return "sparc_32";
        if (value.matches("^(sparcv9|sparc64)$")) return "sparc_64";
        if (value.matches("^(arm|arm32)$")) return "arm_32";
        if (value.matches("^(mips|mips32)$")) return "mips_32";
        if (value.matches("^(mipsel|mips32el)$")) return "mipsel_32";
        if (value.matches("^(ppc|ppc32)$")) return "ppc_32";
        if (value.matches("^(ppcle|ppc32le)$")) return "ppcle_32";
        if (value.matches("^(riscv|riscv32)$")) return "riscv";
        return switch (value) {
            case "ia64n" -> "itanium_32";
            case "aarch64" -> "aarch_64";
            case "mips64" -> "mips_64";
            case "mips64el" -> "mipsel_64";
            case "ppc64" -> "ppc_64";
            case "ppc64le" -> "ppcle_64";
            case "s390" -> "s390_32";
            case "s390x" -> "s390_64";
            case "riscv64" -> "riscv64";
            case "e2k" -> "e2k";
            case "loongarch64" -> "loongarch_64";
            default -> "unknown";
        };
    }

    private static @NotNull String normalize(final @Nullable String value) {
        if (value == null) return "";
        return value.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");
    }

    private int determineBitness(final @NotNull Properties systemProperties,
                                 final @NotNull String architecture) {
        String bitness = systemProperties.get("sun.arch.data.model");
        if (bitness != null && bitness.matches("[0-9]+"))
            return Integer.parseInt(bitness, 10);

        bitness = systemProperties.get("com.ibm.vm.bitmode");
        if (bitness != null && bitness.matches("[0-9]+"))
            return Integer.parseInt(bitness, 10);

        return architecture.contains("64") ? 64 : 32;
    }

}
