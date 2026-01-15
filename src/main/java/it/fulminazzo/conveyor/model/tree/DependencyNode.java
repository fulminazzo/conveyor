package it.fulminazzo.conveyor.model.tree;

import it.fulminazzo.conveyor.model.dependency.Dependency;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a node of a {@link Dependency} tree.
 *
 * @param dependency the dependency
 * @param depth the depth of the node
 */
public record DependencyNode(@NotNull Dependency dependency, int depth) {
}
