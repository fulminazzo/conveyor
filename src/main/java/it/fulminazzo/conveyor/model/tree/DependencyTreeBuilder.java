package it.fulminazzo.conveyor.model.tree;

import it.fulminazzo.conveyor.model.dependency.Dependency;
import it.fulminazzo.conveyor.model.dependency.Scope;
import it.fulminazzo.conveyor.model.pom.EffectivePom;
import it.fulminazzo.conveyor.model.pom.Pom;
import it.fulminazzo.conveyor.model.pom.PomResolver;
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

    private final @NotNull PomResolver resolver;
    private final @NotNull ActivationContext context;

    /**
     * Builds the dependency tree.
     *
     * @return the tree
     */
    public @NotNull Collection<DependencyNode> build() {
        return this.dependencyTree.values();
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

        Pom pom = this.resolver.resolve(dependency);
        addPomDependenciesToCheckList(pom, depth + 1);
        this.dependencyTree.put(coordinates, node);
    }

    /**
     * Adds all the given {@link Pom} dependencies to the {@link #dependenciesToCheck} list.
     * <br>
     * If their {@link Scope} is not in {@link #scopes}, or the list is not empty,
     * they are ignored.
     *
     * @param pom   the pom
     * @param depth the depth of the dependencies
     */
    void addPomDependenciesToCheckList(final @NotNull Pom pom, final int depth) {
        EffectivePom effectivePom = EffectivePom.builder(pom, this.resolver, this.context).build();

        for (Dependency transitiveDep : effectivePom.getDependencies()) {
            if (!this.scopes.isEmpty() && !this.scopes.contains(transitiveDep.getScope())) continue;
            DependencyNode transitiveNode = new DependencyNode(transitiveDep, depth);
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
