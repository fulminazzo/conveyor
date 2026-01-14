package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.Properties;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.dependency.Dependency;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Represents the actual <b>pom.xml</b> file of a project,
 * with a list of all the required dependencies to make it work.
 */
@Value
public class EffectivePom implements PomLike {
    @NotNull Artifact project;
    @NotNull String packaging;

    @NotNull Properties properties;
    @NotNull List<Dependency> dependencies;

    /**
     * Instantiates a new Effective pom.
     *
     * @param project      the project
     * @param packaging    the packaging
     * @param properties   the properties
     * @param dependencies the dependencies
     */
    public EffectivePom(final @NotNull Artifact project,
                        final @NotNull String packaging,
                        final @NotNull Properties properties,
                        final @NotNull Collection<Dependency> dependencies) {
        this.project = project;
        this.packaging = packaging;
        this.properties = properties;
        this.dependencies = List.copyOf(dependencies);
    }

}
