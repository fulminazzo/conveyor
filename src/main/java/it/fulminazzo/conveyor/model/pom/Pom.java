package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.MavenModel;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.dependency.RawDependency;
import it.fulminazzo.conveyor.model.profile.Profile;
import it.fulminazzo.conveyor.model.repository.RawRepository;
import it.fulminazzo.conveyor.xml.XmlParser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Represents a Maven project.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class Pom extends MavenModel implements PomLike {
    private final @NotNull Artifact project;
    private final @NotNull String packaging;
    private final @Nullable Artifact parent;
    private final @NotNull Set<Profile> profiles;

    /**
     * Instantiates a new Pom.
     *
     * @param project              the project
     * @param packaging            the packaging
     * @param parent               the parent
     * @param profiles             the profiles
     * @param properties           the properties
     * @param repositories         the repositories
     * @param dependencyManagement the dependency management
     * @param dependencies         the dependencies
     */
    Pom(final @NotNull Artifact project,
        final @NotNull String packaging,
        final @Nullable Artifact parent,
        final @NotNull Collection<Profile> profiles,
        final @NotNull Map<String, String> properties,
        final @NotNull Collection<RawRepository> repositories,
        final @NotNull Collection<RawDependency> dependencyManagement,
        final @NotNull Collection<RawDependency> dependencies
    ) {
        super(properties, repositories, dependencyManagement, dependencies);
        this.project = project;
        this.packaging = packaging;
        this.parent = parent;
        this.profiles = Set.copyOf(profiles);
    }

    /**
     * Instantiates a new builder to create a {@link Pom} object.
     *
     * @param parser the XML parser
     * @return the builder
     */
    public static @NotNull PomBuilder builder(final @NotNull XmlParser parser) {
        return new PomBuilder(parser);
    }

}
