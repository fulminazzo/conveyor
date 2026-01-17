package it.fulminazzo.conveyor.model.pom.resolver;

import it.fulminazzo.conveyor.manager.RepositoryManager;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.pom.Pom;
import it.fulminazzo.conveyor.model.pom.resolver.mode.PomResolverMode;
import it.fulminazzo.conveyor.model.repository.Repository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.util.Collection;

/**
 * Conveyor official {@link PomResolver}.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConveyorPomResolver implements RepositoryPomResolver {
    private final @NotNull RepositoryManager repositoryManager;
    private final @NotNull File workingDir;
    private final @NotNull Logger logger;

    private @Nullable PomResolver delegate;

    @Override
    public @NotNull Pom resolve(final @NotNull Artifact artifact) throws PomResolverException {
        return getDelegate().resolve(artifact);
    }

    @Override
    public @NotNull ConveyorPomResolver addRepositories(final @NotNull Collection<Repository> repositories) {
        this.repositoryManager.addRepositories(repositories);
        return this;
    }

    /**
     * Sets the operating mode for this resolver.
     *
     * @param mode the mode
     * @return this resolver
     */
    public @NotNull ConveyorPomResolver setMode(final @NotNull PomResolverMode mode) {
        this.delegate = mode.create(this.repositoryManager, this.workingDir, this.logger);
        return this;
    }

    /**
     * Gets the internal delegate.
     *
     * @return the delegate
     * @throws PomResolverException if the delegate has not been initialized yet
     */
    @NotNull PomResolver getDelegate() throws PomResolverException {
        if (this.delegate == null)
            throw new PomResolverException("No resolving mode has been set yet. Please use setMode before calling this method");
        return this.delegate;
    }

    /**
     * Instantiates a new Conveyor PomResolver.
     *
     * @param repositoryManager the repository manager that will keep track of all the repositories
     * @param workingDir        the directory where the resolver should operate (for storing data)
     * @param logger            the logger
     * @return the conveyor pom resolver
     */
    public static @NotNull ConveyorPomResolver newResolver(final @NotNull RepositoryManager repositoryManager,
                                                           final @NotNull File workingDir,
                                                           final @NotNull Logger logger) {
        return new ConveyorPomResolver(repositoryManager, workingDir, logger);
    }

}
