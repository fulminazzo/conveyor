package it.fulminazzo.conveyor.model.profile;

import it.fulminazzo.conveyor.model.MavenModel;
import it.fulminazzo.conveyor.model.dependency.Dependency;
import it.fulminazzo.conveyor.model.profile.activation.Activation;
import it.fulminazzo.conveyor.model.repository.Repository;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

/**
 * Represents a Maven profile.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class Profile extends MavenModel {
    private final @NotNull String id;
    private final @NotNull Activation activation;

    /**
     * Instantiates a new Profile.
     *
     * @param id                   the id
     * @param activation           the activation
     * @param properties           the properties
     * @param repositories         the repositories
     * @param dependencyManagement the dependency management
     * @param dependencies         the dependencies
     */
    Profile(final @NotNull String id,
            final @NotNull Activation activation,
            final @NotNull Map<String, String> properties,
            final @NotNull Collection<Repository> repositories,
            final @NotNull Collection<Dependency> dependencyManagement,
            final @NotNull Collection<Dependency> dependencies) {
        super(properties, repositories, dependencyManagement, dependencies);
        this.id = id;
        this.activation = activation;
    }

}
