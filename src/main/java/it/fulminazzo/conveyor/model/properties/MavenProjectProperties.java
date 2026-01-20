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
     * The properties related to the <a href="https://github.com/trustin/os-maven-plugin/">OS Maven Plugin</a>.
     */
    private @Nullable Properties osMavenProperties;

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

    /**
     * Updates the internal OS Maven properties.
     *
     * @return this object (for method chaining)
     */
    public @NotNull MavenProjectProperties updateOSMavenProperties() {
        this.osMavenProperties = null;
        this.osMavenProperties = OSMavenProperties.builder(this).build();
        return this;
    }

    @Override
    public @NotNull MavenProjectProperties addAll(@NotNull MutableProperties properties) {
        return (MavenProjectProperties) MutableProperties.super.addAll(properties);
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
    public int size() {
        return this.delegate.size();
    }

    @Override
    public @NotNull MutableProperties clear() {
        this.delegate.clear();
        return this;
    }

    @Override
    public @NotNull Map<String, String> toMap() {
        return this.delegate.toMap();
    }

    @Override
    public @NotNull String apply(final @NotNull String string) {
        return this.delegate.apply(string);
    }

    @Override
    public @Nullable String get(final @NotNull String key) {
        if (this.osMavenProperties != null) {
            String value = this.osMavenProperties.get(key);
            if (value != null) return value;
        }
        return this.delegate.get(key);
    }

}
