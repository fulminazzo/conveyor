package it.fulminazzo.conveyor.model.tree;

import it.fulminazzo.conveyor.model.dependency.Dependency;
import it.fulminazzo.conveyor.model.dependency.Exclusions;
import lombok.*;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a node of a {@link Dependency} tree.
 */
@Value
@AllArgsConstructor
public class DependencyNode {

    @NotNull Dependency dependency;

    int depth;

    @NotNull Exclusions exclusions;

    /**
     * Instantiates a new Dependency node.
     *
     * @param dependency the dependency
     * @param depth      the depth
     */
    public DependencyNode(final @NotNull Dependency dependency, final int depth) {
        this(dependency, depth, new Exclusions());
    }

}
