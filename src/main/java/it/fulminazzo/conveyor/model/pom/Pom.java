package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.MavenModel;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.pom.metadata.PomMetadata;
import it.fulminazzo.conveyor.model.profile.Profile;
import it.fulminazzo.conveyor.property.DelegateProperties;
import it.fulminazzo.conveyor.xml.XmlParser;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Maven project.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public final class Pom extends MavenModel implements PomLike {

    private final @Nullable Artifact parent;

    @DelegateProperties
    private final @NotNull Artifact project;

    @Builder.Default
    @DelegateProperties
    private final @NotNull PomMetadata metadata = PomMetadata.builder().build();

    @Builder.Default
    private final @NotNull String packaging = "jar";

    @Builder.Default
    private final @NotNull Set<Profile> profiles = new HashSet<>();

    /**
     * Instantiates a new builder to create a {@link Pom} object.
     *
     * @return the builder
     */
    public static @NotNull PomBuilder<?,?> builder() {
        return new PomBuilderImpl();
    }

    /**
     * Instantiates a new builder to create a {@link Pom} object.
     *
     * @param parser the XML parser
     * @return the builder
     */
    public static @NotNull XmlPomBuilder builder(final @NotNull XmlParser parser) {
        return new XmlPomBuilder(parser);
    }

}
