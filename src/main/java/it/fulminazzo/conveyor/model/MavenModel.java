package it.fulminazzo.conveyor.model;

import it.fulminazzo.conveyor.model.dependency.Dependency;
import it.fulminazzo.conveyor.model.repository.Repository;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a general Maven model object with
 * support for properties, repositories and dependencies.
 */
@EqualsAndHashCode
@ToString
public abstract class MavenModel {
    private final @NotNull Map<String, String> properties;
    private final @NotNull Set<Repository> repositories;
    private final @NotNull Set<Dependency> dependencyManagement;
    private final @NotNull List<Dependency> dependencies;

    /**
     * Instantiates a new Maven model.
     *
     * @param properties           the properties
     * @param repositories         the repositories
     * @param dependencyManagement the dependency management
     * @param dependencies         the dependencies
     */
    protected MavenModel(final @NotNull Map<String, String> properties,
                         final @NotNull Collection<Repository> repositories,
                         final @NotNull Collection<Dependency> dependencyManagement,
                         final @NotNull Collection<Dependency> dependencies) {
        this.properties = Map.copyOf(properties);
        this.repositories = Set.copyOf(repositories);
        this.dependencyManagement = Set.copyOf(dependencyManagement);
        this.dependencies = List.copyOf(dependencies);
    }

}
