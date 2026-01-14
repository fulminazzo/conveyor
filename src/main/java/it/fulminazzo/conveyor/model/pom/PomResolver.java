package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import org.jetbrains.annotations.NotNull;

/**
 * A function to resolve a {@link Pom} object given its {@link Artifact} information.
 */
@FunctionalInterface
public interface PomResolver {

    /**
     * Resolves the pom.
     *
     * @param artifact the artifact
     * @return the pom
     */
    @NotNull Pom resolve(final @NotNull Artifact artifact);

}
