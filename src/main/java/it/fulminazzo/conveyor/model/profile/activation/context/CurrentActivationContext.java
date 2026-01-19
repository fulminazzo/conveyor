package it.fulminazzo.conveyor.model.profile.activation.context;

import it.fulminazzo.conveyor.model.properties.Properties;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;

/**
 * An implementation of {@link ActivationContext} that represents the current environment.
 */
@RequiredArgsConstructor
final class CurrentActivationContext implements ActivationContext {
    private final @NotNull Properties properties;

    @Override
    public @NotNull File getProjectDir() {
        return new File(Objects.requireNonNull(getProperty("project.basedir"), "Could not find project directory"));
    }

    @Override
    public @NotNull String getJdkVersion() {
        return Objects.requireNonNull(getProperty("java.version"), "Could not find JDK version");
    }

    @Override
    public @NotNull String getOsName() {
        return Objects.requireNonNull(getProperty("os.name"), "Could not find OS name");
    }

    @Override
    public @NotNull String getOsArch() {
        return Objects.requireNonNull(getProperty("os.arch"), "Could not find OS arch");
    }

    @Override
    public @NotNull String getOsVersion() {
        return Objects.requireNonNull(getProperty("os.version"), "Could not find OS version");
    }

    @Override
    public @NotNull String getPackaging() {
        return Objects.requireNonNull(getProperty("project.packaging"), "Could not find project packaging");
    }

    @Override
    public @Nullable String getProperty(final @NotNull String name) {
        String value = this.properties.get(name);
        if (value != null && value.isEmpty()) return Boolean.TRUE.toString();
        else return value;
    }

    @Override
    public @NotNull String applyProperties(final @NotNull String string) {
        return this.properties.apply(string);
    }

}
