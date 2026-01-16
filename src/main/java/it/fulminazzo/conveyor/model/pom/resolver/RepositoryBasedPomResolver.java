package it.fulminazzo.conveyor.model.pom.resolver;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.pom.Pom;
import it.fulminazzo.conveyor.model.repository.Repository;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * An object to resolve a {@link Pom} object given its {@link Artifact} information.
 * <br>
 * Uses repositories to query for the requested data.
 */
public interface RepositoryBasedPomResolver extends PomResolver {

    /**
     * Adds repositories to the current resolver.
     *
     * @param repositories the repositories
     * @return this resolver
     */
    @NotNull RepositoryBasedPomResolver addRepositories(final @NotNull Collection<Repository> repositories);

}
