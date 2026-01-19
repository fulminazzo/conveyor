package it.fulminazzo.conveyor.model.tree;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.dependency.Dependency;
import it.fulminazzo.conveyor.model.dependency.ExclusionsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a node of a {@link Dependency} tree.
 *
 * @param requester         the artifact that requested this dependency
 * @param dependency        the dependency
 * @param depth             the depth of the node
 * @param exclusionsManager the exclusions of the dependency
 */
public record DependencyNode(@Nullable Artifact requester,
                             @NotNull Dependency dependency,
                             int depth,
                             @NotNull ExclusionsManager exclusionsManager) {

    /**
     * Instantiates a new Dependency node.
     *
     * @param requester  the artifact that requested this dependency
     * @param dependency the dependency
     * @param depth      the depth
     */
    public DependencyNode(final @Nullable Artifact requester,
                          final @NotNull Dependency dependency,
                          final int depth) {
        this(requester, dependency, depth, new ExclusionsManager());
    }

}
