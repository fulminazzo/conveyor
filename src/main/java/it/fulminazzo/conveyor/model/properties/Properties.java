package it.fulminazzo.conveyor.model.properties;

import it.fulminazzo.conveyor.model.pom.Pom;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * A basic object to replace keys in a certain format with values.
 */
public interface Properties {

    /**
     * Applies all the properties to the given string.
     *
     * @param string the string
     * @return the applied string
     */
    @NotNull String apply(final @NotNull String string);

    /**
     * Gets the value of the property.
     *
     * @param key the key of the property
     * @return the value
     */
    @Nullable String get(final @NotNull String key);

    /**
     * Instantiates a new Properties with support for Maven models properties.
     *
     * @param pom        the pom of the project to get data from
     * @param workingDir the current working directory (for reference of the pom file)
     * @return the properties
     */
    static @NotNull MutableProperties newProjectProperties(final @NotNull Pom pom,
                                                           final @NotNull File workingDir) {
        return new AggregateMutableProperties()
                .addProperties(new PomProperties(pom, workingDir))
                .addProperties(new EnvProperties())
                .addProperties(new SystemProperties());
    }

}
