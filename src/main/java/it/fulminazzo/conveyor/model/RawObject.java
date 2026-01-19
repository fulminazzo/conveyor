package it.fulminazzo.conveyor.model;

import it.fulminazzo.conveyor.model.properties.MavenProjectProperties;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a general object that at the time of creating
 * is still incomplete and requires further parsing
 * before completing creation.
 *
 * @param <O> the type of the completed object
 */
public interface RawObject<O> {

    /**
     * Applies properties to the current object and returns
     * a new fully built object.
     *
     * @param properties the properties
     * @return the completed object
     */
    @NotNull O applyProperties(final @NotNull MavenProjectProperties properties);

}
