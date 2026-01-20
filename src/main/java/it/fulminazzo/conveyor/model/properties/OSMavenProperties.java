package it.fulminazzo.conveyor.model.properties;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
final class OSMavenProperties extends BaseProperties {
    private final @NotNull Map<String, String> delegate;

    @Override
    public @Nullable String get(final @NotNull String key) {
        return this.delegate.get(key);
    }

    /**
     * Instantiates a new builder for {@link OSMavenProperties}.
     *
     * @param systemProperties the system properties (to get OS information from)
     * @return the builder
     */
    public static @NotNull OSMavenPropertiesBuilder builder(final @NotNull Properties systemProperties) {
        return new OSMavenPropertiesBuilder(systemProperties);
    }

    /**
     * A builder for {@link OSMavenProperties}.
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    static class OSMavenPropertiesBuilder {
        private final @NotNull Map<String, String> delegate = new HashMap<>();
        private final @NotNull Properties systemProperties;

        /**
         * Build os maven properties.
         *
         * @return the os maven properties
         */
        public @NotNull OSMavenProperties build() {
            this.delegate.put("os.detected.name", convertOsName(this.systemProperties.get("os.name")));
            String arch = convertOsArchitecture(this.systemProperties.get("os.arch"));
            this.delegate.put("os.detected.arch", arch);
            this.delegate.put("os.detected.bitness", String.valueOf(determineBitness(arch)));

            return new OSMavenProperties(Map.copyOf(this.delegate));
        }

        /**
         * Determines the "bitness" of the current operating system.
         * If the property <code>sun.arch.data.model</code> is defined,
         * it will attempt to convert it to a decimal number and return it.
         * Otherwise, it will do the same with the property <code>com.ibm.vm.bitmode</code>
         * if it is defined.
         * If none of the properties are defined, it will look in the given architecture:
         * if it contains the number <i>64</i>, the number itself will be returned,
         * otherwise <i>32</i>.
         *
         * @param architecture the architecture of the operating system
         * @return the bitness
         */
        int determineBitness(final @NotNull String architecture) {
            String bitness = this.systemProperties.get("sun.arch.data.model");
            if (bitness != null && bitness.matches("[0-9]+"))
                return Integer.parseInt(bitness, 10);

            bitness = this.systemProperties.get("com.ibm.vm.bitmode");
            if (bitness != null && bitness.matches("[0-9]+"))
                return Integer.parseInt(bitness, 10);

            return architecture.contains("64") ? 64 : 32;
        }

        /**
         * Converts the OS name to a set of known names.
         *
         * @param osName the os name
         * @return the converted name
         */
        static @NotNull String convertOsName(@Nullable String osName) {
            osName = normalize(osName);
            if (osName.startsWith("aix")) return "aix";
            else if (osName.startsWith("hpux")) return "hpux";
            else if (osName.startsWith("os400")) {
                if (osName.length() == 5 || !Character.isDigit(osName.charAt(5))) return "os400";
            } else if (osName.startsWith("linux")) return "linux";
            else if (osName.startsWith("mac") || osName.startsWith("osx")) return "osx";
            else if (osName.startsWith("freebsd")) return "freebsd";
            else if (osName.startsWith("openbsd")) return "openbsd";
            else if (osName.startsWith("netbsd")) return "netbsd";
            else if (osName.startsWith("solaris") || osName.startsWith("sunos")) return "sunos";
            else if (osName.startsWith("windows")) return "windows";
            else if (osName.startsWith("zos")) return "zos";
            return "unknown";
        }

        /**
         * Converts the OS architecture to a set of known architectures.
         *
         * @param osArchitecture the os architecture
         * @return the converted architecture
         */
        static String convertOsArchitecture(@Nullable String osArchitecture) {
            osArchitecture = normalize(osArchitecture);
            if (osArchitecture.matches("^(x8664|amd64|ia32e|em64t|x64)$")) return "x86_64";
            if (osArchitecture.matches("^(x8632|x86|i[3-6]86|ia32|x32)$")) return "x86_32";
            if (osArchitecture.matches("^(ia64w?|itanium64)$")) return "itanium_64";
            if (osArchitecture.matches("^(sparc|sparc32)$")) return "sparc_32";
            if (osArchitecture.matches("^(sparcv9|sparc64)$")) return "sparc_64";
            if (osArchitecture.matches("^(arm|arm32)$")) return "arm_32";
            if (osArchitecture.matches("^(mips|mips32)$")) return "mips_32";
            if (osArchitecture.matches("^(mipsel|mips32el)$")) return "mipsel_32";
            if (osArchitecture.matches("^(ppc|ppc32)$")) return "ppc_32";
            if (osArchitecture.matches("^(ppcle|ppc32le)$")) return "ppcle_32";
            if (osArchitecture.matches("^(riscv|riscv32)$")) return "riscv";
            return switch (osArchitecture) {
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

        /**
         * Turns the string to lower case.
         * If it is <code>null</code>, then an empty string is returned.
         *
         * @param string the string
         * @return the normalized string
         */
        static @NotNull String normalize(final @Nullable String string) {
            if (string == null) return "";
            return string.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");
        }

    }

}
