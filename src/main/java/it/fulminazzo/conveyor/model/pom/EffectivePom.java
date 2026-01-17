package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.Properties;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.dependency.Dependency;
import it.fulminazzo.conveyor.model.pom.resolver.RepositoryPomResolver;
import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext;
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

    /**
     * Obtains a new builder to create an {@link EffectivePom} object.
     *
     * @param startingPom the {@link Pom} object to build the effective pom from.
     *                    All the dependencies will be parsed using properties and dependency management
     * @param pomResolver a function to resolve the pom of an artifact
     * @param context     the base context where the builder should operate (with operating system data and similar).
     *                    A new {@link ActivationContext} will be instantiated for each pom request,
     *                    with the update of the internal packaging.
     * @return the builder
     */
    public static @NotNull EffectivePomBuilder builder(final @NotNull Pom startingPom,
                                                       final @NotNull RepositoryPomResolver pomResolver,
                                                       final @NotNull ActivationContext context) {
        return new EffectivePomBuilder(startingPom, pomResolver, context);
    }

}
