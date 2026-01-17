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
public interface RepositoryPomResolver extends PomResolver {

    /**
     * Adds repositories to the repository manager of the resolver.
     *
     * @param repositories the repositories
     * @return this resolver
     */
    @NotNull RepositoryPomResolver addRepositories(final @NotNull Collection<Repository> repositories);


}
