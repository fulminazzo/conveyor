package it.fulminazzo.conveyor.model.tree;

import it.fulminazzo.conveyor.model.dependency.Dependency;
import it.fulminazzo.conveyor.model.dependency.ExclusionsManager;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a node of a {@link Dependency} tree.
 *
 * @param dependency the dependency
 * @param depth      the depth of the node
 */
public record DependencyNode(@NotNull Dependency dependency, int depth, @NotNull ExclusionsManager exclusionsManager) {

    /**
     * Instantiates a new Dependency node.
     *
     * @param dependency the dependency
     * @param depth      the depth
     */
    public DependencyNode(final @NotNull Dependency dependency, final int depth) {
        this(dependency, depth, new ExclusionsManager());
    }

}
