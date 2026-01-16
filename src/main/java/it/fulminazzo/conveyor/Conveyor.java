package it.fulminazzo.conveyor;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.pom.resolver.ConveyorPomResolver;
import it.fulminazzo.conveyor.model.pom.resolver.PomResolverException;
import it.fulminazzo.conveyor.model.pom.resolver.RepositoryBasedPomResolver;
import it.fulminazzo.conveyor.model.pom.resolver.engine.PomResolveEngineType;
import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext;
import it.fulminazzo.conveyor.model.tree.DependencyNode;
import it.fulminazzo.conveyor.model.tree.DependencyTreeBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.util.Collection;

/**
 * Main access point of the library.
 */
public final class Conveyor {
    private final @NotNull RepositoryBasedPomResolver resolver;
    private final @NotNull ActivationContext context;

    /**
     * Instantiates a new Conveyor.
     *
     * @param context    the activation context
     * @param workingDir the directory where the repositories should be stored
     * @param logger     the logger
     */
    public Conveyor(final @NotNull ActivationContext context,
                    final @NotNull File workingDir,
                    final @NotNull Logger logger) {
        this.resolver = ConveyorPomResolver.newResolver(workingDir, logger).setMode(PomResolveEngineType.CHECKSUM);
        this.context = context;
    }

    /**
     * Builds the dependency tree of the given artifact.
     *
     * @param artifact the artifact
     * @return the dependency tree
     * @throws PomResolverException in case of any errors
     */
    @NotNull Collection<DependencyNode> buildDependencyTree(final @NotNull Artifact artifact) throws PomResolverException {
        return new DependencyTreeBuilder(artifact, this.resolver, this.context).build();
    }

    /**
     * Updates the mode of resolving the pom data for each artifact.
     *
     * @param mode the mode
     * @return this conveyor
     */
    public @NotNull Conveyor setPomResolveMode(final @NotNull PomResolveEngineType mode) {
        this.resolver.setMode(mode);
        return this;
    }

}
