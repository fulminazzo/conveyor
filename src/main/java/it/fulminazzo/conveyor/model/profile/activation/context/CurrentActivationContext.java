package it.fulminazzo.conveyor.model.profile.activation.context;

import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An implementation of {@link ActivationContext} that represents the current environment.
 */
@Value
class CurrentActivationContext implements ActivationContext {
    private static final Pattern propertiesRegex = Pattern.compile("\\$\\{([^}]+)}");
    private static final @NotNull String envPropertyPrefix = "env.";
    private static final @NotNull List<String> projectDirectoryPropertyNames = Arrays.asList(
            "basedir", "project.basedir", "maven.multiModuleProjectDirectory"
    );

    @NotNull File currentDir;
    @NotNull String packaging;

    /**
     * Gets the context JDK version.
     *
     * @return the jdk version
     */
    @Override
    public @NotNull String getJdkVersion() {
        return Objects.requireNonNull(getProperty("java.version"), "Could not find JDK version");
    }

    /**
     * Gets the context Operating System name.
     *
     * @return the os name
     */
    @Override
    public @NotNull String getOsName() {
        return Objects.requireNonNull(getProperty("os.name"), "Could not find OS name");
    }

    /**
     * Gets the context Operating System arch.
     *
     * @return the os arch
     */
    @Override
    public @NotNull String getOsArch() {
        return Objects.requireNonNull(getProperty("os.arch"), "Could not find OS arch");
    }

    /**
     * Gets the context Operating System version.
     *
     * @return the os version
     */
    @Override
    public @NotNull String getOsVersion() {
        return Objects.requireNonNull(getProperty("os.version"), "Could not find OS version");
    }

    @Override
    public @NotNull String applyProperties(@NotNull String string) {
        Matcher matcher = propertiesRegex.matcher(string);
        while (matcher.find()) {
            String propertyName = matcher.group(1);
            String propertyValue = getProperty(propertyName);
            if (propertyValue != null) {
                string = string.replace(String.format("${%s}", propertyName), propertyValue);
                matcher = propertiesRegex.matcher(string);
            }
        }
        return string;
    }

    @Override
    public @Nullable String getProperty(@NotNull String name) {
        if (projectDirectoryPropertyNames.contains(name)) return getCurrentDir().getAbsolutePath();
        if (name.startsWith(envPropertyPrefix)) {
            name = name.substring(envPropertyPrefix.length());
            return System.getenv(name);
        }
        return System.getProperty(name);
    }

}
