package it.fulminazzo.conveyor.model.pom.resolver;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.pom.Pom;
import it.fulminazzo.conveyor.model.pom.resolver.engine.PomResolveEngineType;
import it.fulminazzo.conveyor.model.repository.Repository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.util.Collection;

/**
 * Conveyor official {@link PomResolver}, with support for releases and snapshots artifacts.
 */
public final class ConveyorPomResolver implements RepositoryBasedPomResolver {
    private final @NotNull RepositoryBasedPomResolver releasesResolver;
    private final @NotNull RepositoryBasedPomResolver snapshotsResolver;

    private ConveyorPomResolver(final @NotNull File workingDir, final @NotNull Logger logger) {
        this.releasesResolver = new ReleasesRepositoryBasedPomResolver(workingDir, logger);
        this.snapshotsResolver = new SnapshotsRepositoryBasedPomResolver(workingDir, logger);
    }

    @Override
    public @NotNull Pom resolve(final @NotNull Artifact artifact) throws PomResolverException {
        String version = artifact.getVersion();
        if (version.endsWith("-SNAPSHOT")) return this.snapshotsResolver.resolve(artifact);
        else return this.releasesResolver.resolve(artifact);
    }

    @Override
    public @NotNull RepositoryBasedPomResolver addRepositories(final @NotNull Collection<Repository> repositories) {
        this.releasesResolver.addRepositories(repositories);
        this.snapshotsResolver.addRepositories(repositories);
        return this;
    }

    @Override
    public @NotNull RepositoryBasedPomResolver setMode(final @NotNull PomResolveEngineType mode) {
        this.releasesResolver.setMode(mode);
        this.snapshotsResolver.setMode(mode);
        return this;
    }

    /**
     * Instantiates a new Repository based Pom resolver.
     *
     * @param workingDir the working dir
     * @param logger     the logger
     * @return the repository based pom resolver
     */
    public static @NotNull RepositoryBasedPomResolver newResolver(final @NotNull File workingDir,
                                                                  final @NotNull Logger logger) {
        return new ConveyorPomResolver(workingDir, logger);
    }

}
