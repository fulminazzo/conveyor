package it.fulminazzo.conveyor;

import it.fulminazzo.conveyor.manager.RepositoryManager;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.dependency.Scope;
import it.fulminazzo.conveyor.model.pom.resolver.ConveyorPomResolver;
import it.fulminazzo.conveyor.model.pom.resolver.PomResolverException;
import it.fulminazzo.conveyor.model.pom.resolver.mode.PomResolverMode;
import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext;
import it.fulminazzo.conveyor.model.repository.Repository;
import it.fulminazzo.conveyor.model.tree.DependencyNode;
import it.fulminazzo.conveyor.model.tree.DependencyTreeBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

/**
 * Main access point of the library.
 */
public final class Conveyor {
    private static final @NotNull String mavenCentralUrl = "https://repo.maven.apache.org/maven2/";

    private final @NotNull RepositoryManager repositoryManager;

    private final @NotNull ConveyorPomResolver resolver;
    private final @NotNull DependencyTreeBuilder dependencyTreeBuilder;

    private Conveyor(final @NotNull ActivationContext context,
                     final @NotNull File workingDir,
                     final @NotNull Logger logger) {
        this.repositoryManager = RepositoryManager.newManager(logger);

        this.resolver = ConveyorPomResolver.newResolver(this.repositoryManager, workingDir, logger);
        this.dependencyTreeBuilder = new DependencyTreeBuilder(this.resolver, context);
    }

    /**
     * Builds the dependency tree of the given artifact.
     *
     * @param artifact the artifact
     * @return the dependency tree
     * @throws PomResolverException in case of any errors
     */
    @NotNull Collection<DependencyNode> buildDependencyTree(final @NotNull Artifact artifact) throws PomResolverException {
        return this.dependencyTreeBuilder.setProject(artifact).build();
    }

    /**
     * Adds the given repositories to the lookup list.
     *
     * @param urls the URLs of the repositories
     * @return this conveyor
     */
    public @NotNull Conveyor addRawRepositories(final String @NotNull ... urls) {
        return addRepositories(Arrays.stream(urls)
                .map(u -> Repository.builder().id(u).url(u).build())
                .toArray(Repository[]::new)
        );
    }

    /**
     * Adds the given repositories to the lookup list.
     *
     * @param repositories the repositories
     * @return this conveyor
     */
    public @NotNull Conveyor addRepositories(final Repository @NotNull ... repositories) {
        this.repositoryManager.addRepositories(Arrays.asList(repositories));
        return this;
    }

    /**
     * Updates the mode of resolving the pom data for each artifact.
     *
     * @param mode the mode
     * @return this conveyor
     */
    public @NotNull Conveyor setPomResolveMode(final @NotNull PomResolverMode mode) {
        this.resolver.setMode(mode);
        return this;
    }

    /**
     * Updates the scopes that should be downloaded from the dependencies tree.
     *
     * @param scopes the scopes
     * @return this conveyor
     */
    public @NotNull Conveyor setScopesOfInterest(final Scope @NotNull ... scopes) {
        this.dependencyTreeBuilder.setRequiredScopes(scopes);
        return this;
    }

    /**
     * Instantiates a new Conveyor.
     *
     * @param context    the activation context
     * @param workingDir the directory where the repositories should be stored
     * @param logger     the logger
     * @return the conveyor
     */
    public static @NotNull Conveyor newConveyor(final @NotNull ActivationContext context,
                                                final @NotNull File workingDir,
                                                final @NotNull Logger logger) {
        return new Conveyor(context, workingDir, logger)
                .setPomResolveMode(PomResolverMode.CHECKSUM)
                .addRawRepositories(mavenCentralUrl)
                .setScopesOfInterest(Scope.COMPILE, Scope.PROVIDED, Scope.RUNTIME);
    }

}
