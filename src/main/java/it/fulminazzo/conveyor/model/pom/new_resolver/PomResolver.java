package it.fulminazzo.conveyor.model.pom.new_resolver;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.pom.Pom;
import org.jetbrains.annotations.NotNull;

/**
 * An object to resolve a {@link Pom} object given its {@link Artifact} information.
 */
public interface PomResolver {

    /**
     * Resolves the pom.
     *
     * @param artifact the artifact
     * @return the pom
     */
    @NotNull Pom resolve(final @NotNull Artifact artifact) throws PomResolverException;

}
