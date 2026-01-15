package it.fulminazzo.conveyor.model.tree;

import it.fulminazzo.conveyor.model.dependency.Dependency;
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
        EffectivePom effectivePom = EffectivePom.builder(pom, this.resolver, this.context).build();

        for (Dependency transitiveDep : effectivePom.getDependencies()) {
            DependencyNode transitiveNode = new DependencyNode(transitiveDep, depth + 1);
            this.dependenciesToCheck.offer(transitiveNode);
        }
    }

}
