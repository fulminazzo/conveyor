package it.fulminazzo.conveyor.model.tree;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.dependency.Dependency;
import it.fulminazzo.conveyor.model.dependency.ExclusionsManager;
import it.fulminazzo.conveyor.model.dependency.Scope;
import it.fulminazzo.conveyor.model.pom.EffectivePom;
import it.fulminazzo.conveyor.model.pom.Pom;
import it.fulminazzo.conveyor.model.pom.resolver.PomResolverException;
import it.fulminazzo.conveyor.model.pom.resolver.RepositoryPomResolver;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

/**
 * A helper class to create a tree ({@link Map}) of {@link DependencyNode}.
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j(topic = "DependenciesTreeBuilder")
public final class DependenciesTreeBuilder {
    private final @NotNull Map<String, DependencyNode> dependenciesTree = new LinkedHashMap<>();
    private final @NotNull Queue<DependencyNode> dependenciesToCheck = new LinkedList<>();

    private final @NotNull Set<Scope> scopes = new HashSet<>();

    private @Nullable Artifact project;
    private final @NotNull RepositoryPomResolver resolver;
    private final @NotNull File workingDir;

    /**
     * Builds the dependencies tree.
     *
     * @return the tree
     * @throws PomResolverException in case of any errors during pom construction
     */
    public @NotNull Collection<DependencyNode> build() throws PomResolverException {
        populateTree();
        return this.dependenciesTree.values();
    }

    /**
     * Populates the {@link #dependenciesTree} with the required dependencies.
     *
     * @throws PomResolverException in case of any errors during pom construction
     */
    void populateTree() throws PomResolverException {
        if (this.project == null)
            throw new IllegalStateException("project has not been initialized yet");

        this.dependenciesTree.clear();
        this.dependenciesToCheck.clear();

        log.debug("Resolving POM for main project '{}'", this.project.getCoordinates());
        Pom projectPom = this.resolver.resolve(this.project);
        addPomDependenciesToCheckList(projectPom, 1, new ExclusionsManager());
        Dependency projectDependency = Dependency.builder()
                .groupId(this.project.getGroupId())
                .artifactId(this.project.getArtifactId())
                .classifier(this.project.getClassifier())
                .version(this.project.getVersion())
                .type(projectPom.getPackaging())
                .build();
        this.dependenciesTree.put(projectDependency.getCoordinates(), new DependencyNode(null, projectDependency, 0));

        while (!this.dependenciesToCheck.isEmpty())
            populateTree(this.dependenciesToCheck.poll());

        this.project = null;
    }

    /**
     * Support method to populate the final {@link #dependenciesTree}.
     *
     * @param node the starting dependency node
     * @throws PomResolverException in case of any errors during pom construction
     */
    void populateTree(final @NotNull DependencyNode node) throws PomResolverException {
        final Dependency dependency = node.dependency();
        final int depth = node.depth();
        final String coordinates = dependency.getCoordinates();

        final DependencyNode prevNode = this.dependenciesTree.get(coordinates);
        if (prevNode != null && prevNode.depth() <= depth) return;
        this.dependenciesTree.put(coordinates, node);

        Artifact requester = node.requester();
        if (requester != null)
            log.debug("Resolving POM for dependency '{}' (requested from '{}')",
                    dependency.getCoordinates(),
                    requester.getCoordinates()
            );
        else
            log.debug("Resolving POM for dependency '{}'", dependency.getCoordinates());
        Pom pom = this.resolver.resolve(dependency);
        addPomDependenciesToCheckList(pom, depth + 1, node.exclusionsManager());
    }

    /**
     * Adds all the given {@link Pom} dependencies to the {@link #dependenciesToCheck} list.
     * <br>
     * If their {@link Scope} is not in {@link #scopes}, or the list is not empty,
     * they are ignored.
     *
     * @param pom               the pom
     * @param depth             the depth of the dependencies
     * @param exclusionsManager the exclusions of the dependency that generated the pom
     * @throws PomResolverException in case of any errors during pom construction
     */
    void addPomDependenciesToCheckList(final @NotNull Pom pom,
                                       final int depth,
                                       final @NotNull ExclusionsManager exclusionsManager) throws PomResolverException {
        EffectivePom effectivePom = EffectivePom.builder(pom, this.resolver, this.workingDir).build();

        for (Dependency transitiveDep : effectivePom.getDependencies()) {
            if (exclusionsManager.isExcluded(transitiveDep.getGroupId(), transitiveDep.getArtifactId())) continue;
            if (!this.scopes.isEmpty() && !this.scopes.contains(transitiveDep.getScope())) continue;
            DependencyNode transitiveNode = new DependencyNode(pom.getProject(), transitiveDep, depth);
            transitiveNode.exclusionsManager().addAll(exclusionsManager).addAll(transitiveDep.getExclusionsManager());
            this.dependenciesToCheck.offer(transitiveNode);
        }
    }

    /**
     * Updates the required {@link Scope}s of this builder.
     * When the dependencies tree will be built, it will be required
     * to have one of the given scopes.
     * <br>
     * If none are given, then all the scopes are allowed.
     *
     * @param scopes the scopes
     * @return this builder
     */
    public @NotNull DependenciesTreeBuilder setRequiredScopes(final Scope @NotNull ... scopes) {
        return setRequiredScopes(Arrays.asList(scopes));
    }

    /**
     * Sets the project of which to build the dependencies tree.
     *
     * @param project the project
     * @return this builder
     */
    public @NotNull DependenciesTreeBuilder setProject(final @Nullable Artifact project) {
        this.project = project;
        return this;
    }

    /**
     * Updates the required {@link Scope}s of this builder.
     * When the dependencies tree will be built, it will be required
     * to have one of the given scopes.
     * <br>
     * If none are given, then all the scopes are allowed.
     *
     * @param scopes the scopes
     * @return this builder
     */
    public @NotNull DependenciesTreeBuilder setRequiredScopes(final @NotNull Collection<Scope> scopes) {
        this.scopes.clear();
        this.scopes.addAll(scopes);
        return this;
    }

}
