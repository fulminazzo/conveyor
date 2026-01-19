package it.fulminazzo.conveyor.model.properties;

import it.fulminazzo.conveyor.model.pom.Pom;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;

/**
 * An implementation of {@link MutableProperties} for a Maven project.
 * Supports all types of properties, with addition of custom ones.
 */
public final class MavenProjectProperties implements MutableProperties {
    private final @NotNull AggregateMutableProperties delegate;

    /**
     * Instantiates a new Maven project properties.
     *
     * @param pom        the pom
     * @param workingDir the working dir
     */
    MavenProjectProperties(final @NotNull Pom pom,
                           final @NotNull File workingDir) {
        this.delegate = new AggregateMutableProperties()
                .addProperties(new PomProperties(pom, workingDir))
                .addProperties(new EnvProperties())
                .addProperties(new SystemProperties());
    }

    /**
     * Returns a {@link Properties} object with only the non-custom properties.
     *
     * @return the properties
     */
    public @NotNull Properties toImmutable() {
        return this.delegate.delegate;
    }

    @Override
    public @NotNull MavenProjectProperties addAll(final @NotNull Map<String, String> properties) {
        return (MavenProjectProperties) MutableProperties.super.addAll(properties);
    }

    @Override
    public @NotNull MavenProjectProperties add(final @NotNull String key, final @NotNull String value) {
        this.delegate.add(key, value);
        return this;
    }

    @Override
    public @NotNull String apply(final @NotNull String string) {
        return this.delegate.apply(string);
    }

    @Override
    public @Nullable String get(final @NotNull String key) {
        return this.delegate.get(key);
    }

}
