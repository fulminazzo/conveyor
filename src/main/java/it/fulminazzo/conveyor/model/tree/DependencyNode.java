package it.fulminazzo.conveyor.model.tree;

import it.fulminazzo.conveyor.model.dependency.Dependency;
import it.fulminazzo.conveyor.model.dependency.Exclusions;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a node of a {@link Dependency} tree.
 */
@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DependencyNode {
    @NotNull Dependency dependency;
    int depth;
    @NotNull Exclusions exclusions = new Exclusions();

}
