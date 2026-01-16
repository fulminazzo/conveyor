package it.fulminazzo.conveyor.model.pom.resolver;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.pom.Pom;
import it.fulminazzo.conveyor.model.repository.Repository;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A function to resolve a {@link Pom} object given its {@link Artifact} information.
 */
public interface PomResolver {

    /**
     * Resolves the pom.
     *
     * @param artifact the artifact
     * @return the pom
     */
    @NotNull Pom resolve(final @NotNull Artifact artifact);

    /**
     * Adds repositories to the current resolver.
     *
     * @param repositories the repositories
     * @return this resolver
     */
    @NotNull PomResolver addRepositories(final @NotNull Collection<Repository> repositories);

}
