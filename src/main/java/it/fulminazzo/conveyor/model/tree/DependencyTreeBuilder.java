package it.fulminazzo.conveyor.model.tree;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.dependency.Dependency;
import it.fulminazzo.conveyor.model.dependency.Exclusions;
import it.fulminazzo.conveyor.model.dependency.Scope;
import it.fulminazzo.conveyor.model.pom.EffectivePom;
import it.fulminazzo.conveyor.model.pom.Pom;
import it.fulminazzo.conveyor.model.pom.resolver.RepositoryBasedPomResolver;
import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A helper class to create a tree ({@link Map}) of {@link DependencyNode}.
 */
@RequiredArgsConstructor
public final class DependencyTreeBuilder {
    private final @NotNull Map<String, DependencyNode> dependencyTree = new LinkedHashMap<>();
    private final @NotNull Queue<DependencyNode> dependenciesToCheck = new LinkedList<>();

    private final @NotNull Set<Scope> scopes = new HashSet<>();

    private final @NotNull Artifact project;
    private final @NotNull RepositoryBasedPomResolver resolver;
    private final @NotNull ActivationContext context;

    /**
     * Builds the dependency tree.
     *
     * @return the tree
     */
    public @NotNull Collection<DependencyNode> build() {
        populateTree();
        return this.dependencyTree.values();
    }

    /**
     * Populates the {@link #dependencyTree} with the required dependencies.
     */
    void populateTree() {
        this.dependencyTree.clear();
        this.dependenciesToCheck.clear();

        Pom projectPom = this.resolver.resolve(this.project);
        addPomDependenciesToCheckList(projectPom, 1, new Exclusions());
        Dependency projectDependency = Dependency.builder()
                .groupId(this.project.getGroupId())
                .artifactId(this.project.getArtifactId())
                .classifier(this.project.getClassifier())
                .version(this.project.getVersion())
                .type(projectPom.getPackaging())
                .build();
        this.dependencyTree.put(projectDependency.getCoordinates(), new DependencyNode(projectDependency, 0));

        while (!this.dependenciesToCheck.isEmpty())
            populateTree(this.dependenciesToCheck.poll());

        this.dependencyTree.remove(projectDependency.getCoordinates());
    }

    /**
     * Support method to populate the final {@link #dependencyTree}.
     *
     * @param node the starting dependency node
     */
    void populateTree(final @NotNull DependencyNode node) {
        final Dependency dependency = node.dependency();
        final int depth = node.depth();
        final String coordinates = dependency.getCoordinates();

        final DependencyNode prevNode = this.dependencyTree.get(coordinates);
        if (prevNode != null && prevNode.depth() <= depth) return;
        this.dependencyTree.put(coordinates, node);

        Pom pom = this.resolver.resolve(dependency);
        addPomDependenciesToCheckList(pom, depth + 1, node.exclusions());
    }

    /**
     * Adds all the given {@link Pom} dependencies to the {@link #dependenciesToCheck} list.
     * <br>
     * If their {@link Scope} is not in {@link #scopes}, or the list is not empty,
     * they are ignored.
     *
     * @param pom        the pom
     * @param depth      the depth of the dependencies
     * @param exclusions the exclusions of the dependency that generated the pom
     */
    void addPomDependenciesToCheckList(final @NotNull Pom pom,
                                       final int depth,
                                       final @NotNull Exclusions exclusions) {
        EffectivePom effectivePom = EffectivePom.builder(pom, this.resolver, this.context).build();

        for (Dependency transitiveDep : effectivePom.getDependencies()) {
            if (exclusions.isExcluded(transitiveDep.getGroupId(), transitiveDep.getArtifactId())) continue;
            if (!this.scopes.isEmpty() && !this.scopes.contains(transitiveDep.getScope())) continue;
            DependencyNode transitiveNode = new DependencyNode(transitiveDep, depth);
            transitiveNode.exclusions().addAll(exclusions).addAll(transitiveDep.getExclusions());
            this.dependenciesToCheck.offer(transitiveNode);
        }
    }

    /**
     * Updates the required {@link Scope}s of this builder.
     * When the dependency tree will be built, it will be required
     * to have one of the given scopes.
     * <br>
     * If none are given, then all the scopes are allowed.
     *
     * @param scopes the scopes
     * @return this builder
     */
    public @NotNull DependencyTreeBuilder setRequiredScopes(final Scope @NotNull ... scopes) {
        return setRequiredScopes(Arrays.asList(scopes));
    }

    /**
     * Updates the required {@link Scope}s of this builder.
     * When the dependency tree will be built, it will be required
     * to have one of the given scopes.
     * <br>
     * If none are given, then all the scopes are allowed.
     *
     * @param scopes the scopes
     * @return this builder
     */
    public @NotNull DependencyTreeBuilder setRequiredScopes(final @NotNull Collection<Scope> scopes) {
        this.scopes.clear();
        this.scopes.addAll(scopes);
        return this;
    }

}
