package it.fulminazzo.conveyor.model;

import it.fulminazzo.conveyor.model.dependency.RawDependency;
import it.fulminazzo.conveyor.model.repository.RawRepository;
import it.fulminazzo.conveyor.property.PropertyAccessible;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a general Maven model object with
 * support for properties, repositories and dependencies.
 */
@Getter
@EqualsAndHashCode
@ToString
@SuperBuilder
public abstract class MavenModel implements PropertyAccessible {
    @Builder.Default
    private final @NotNull Map<String, String> properties = new HashMap<>();
    @Builder.Default
    private final @NotNull Set<RawRepository> repositories = new HashSet<>();
    @Builder.Default
    private final @NotNull Set<RawDependency> dependencyManagement = new HashSet<>();
    @Builder.Default
    private final @NotNull List<RawDependency> dependencies = new ArrayList<>();

}
