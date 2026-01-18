package it.fulminazzo.conveyor.model;

import it.fulminazzo.conveyor.model.dependency.RawDependency;
import it.fulminazzo.conveyor.model.repository.RawRepository;
import it.fulminazzo.conveyor.property.PropertyAccessible;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a general Maven model object with
 * support for properties, repositories and dependencies.
 */
@Getter
@EqualsAndHashCode
@ToString
@SuperBuilder
public abstract class MavenModel implements PropertyAccessible {
    private final @NotNull Map<String, String> properties;
    private final @NotNull Set<RawRepository> repositories;
    private final @NotNull Set<RawDependency> dependencyManagement;
    private final @NotNull List<RawDependency> dependencies;

}
