package it.fulminazzo.conveyor.model.properties;

import it.fulminazzo.conveyor.model.pom.Pom;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * An implementation of {@link MutableProperties} for a Maven project.
 * Supports all types of properties, with addition of custom ones.
 */
public final class MavenProjectProperties implements MutableProperties {
    @Delegate
    private final @NotNull AggregateMutableProperties delegate;

    /**
     * Instantiates a new Maven project properties.
     *
     * @param pom        the pom
     * @param workingDir the working dir
     */
    public MavenProjectProperties(final @NotNull Pom pom,
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

}
