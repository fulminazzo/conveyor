package it.fulminazzo.conveyor.model.tree;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A helper class to create a tree ({@link Map}) of {@link DependencyNode}.
 */
public final class DependencyTreeBuilder {
    private final @NotNull Map<String, DependencyNode> dependencyTree = new LinkedHashMap<>();

    /**
     * Builds the dependency tree.
     *
     * @return the tree
     */
    public @NotNull Collection<DependencyNode> build() {
        return this.dependencyTree.values();
    }

}
