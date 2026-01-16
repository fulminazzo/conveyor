package it.fulminazzo.conveyor.model.pom.resolver;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.pom.Pom;
import it.fulminazzo.conveyor.model.pom.resolver.engine.PomResolveEngineType;
import it.fulminazzo.conveyor.model.pom.resolver.engine.PomResolverEngine;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;

/**
 * A base implementation of {@link RepositoryBasedPomResolver}
 * with an internal engine to handle the resolving operations.
 */
@RequiredArgsConstructor
abstract class BaseRepositoryBasedPomResolver implements RepositoryBasedPomResolver {
    private final @NotNull File workingDir;
    private final @NotNull Logger logger;

    private @Nullable PomResolverEngine engine;

    @Override
    public @NotNull Pom resolve(final @NotNull Artifact artifact) throws PomResolverException {
        return getEngine().resolve(artifact);
    }

    /**
     * Gets the current engine.
     * Throws {@link IllegalStateException} if not initialized.
     *
     * @return the engine
     */
    public @NotNull PomResolverEngine getEngine() {
        if (this.engine == null)
            throw new IllegalStateException("Internal engine has not been initialized yet");
        return this.engine;
    }

    @Override
    public @NotNull RepositoryBasedPomResolver setMode(final @NotNull PomResolveEngineType mode) {
        PomResolverEngine newEngine = mode.create(this.workingDir, this.logger);
        if (this.engine != null) this.engine.transferSources(newEngine);
        this.engine = newEngine;
        return this;
    }

}
