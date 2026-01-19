package it.fulminazzo.conveyor.model.dependency;

import it.fulminazzo.conveyor.model.properties.MavenProjectProperties;
import it.fulminazzo.conveyor.model.RawObject;
import it.fulminazzo.conveyor.model.artifact.ArtifactLike;
import it.fulminazzo.conveyor.property.DelegateProperties;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a raw {@link Dependency},
 * where all the fields (except {@link #exclusionsManager})
 * are initialized in their <b>XML</b> form.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public final class RawDependency extends ArtifactLike implements DependencyLike, RawObject<Dependency> {

    @Builder.Default
    private final @Nullable String version = null;

    @Builder.Default
    private final @NotNull String type = "jar";

    @Builder.Default
    private final @NotNull String scope = Scope.COMPILE.value();

    @Builder.Default
    private final @NotNull String optional = String.valueOf(Boolean.FALSE);

    @DelegateProperties
    @Builder.Default
    private final @NotNull ExclusionsManager exclusionsManager = new ExclusionsManager();

    @Override
    public @NotNull Dependency applyProperties(final @NotNull MavenProjectProperties properties) {
        final String classifier = getClassifier();
        return Dependency.builder()
                .groupId(properties.apply(getGroupId()))
                .artifactId(properties.apply(getArtifactId()))
                .classifier(classifier != null ? properties.apply(classifier) : null)
                .version(properties.apply(Objects.requireNonNull(getVersion(), "Could not get version of dependency " + getCoordinates())))
                .type(properties.apply(getType()))
                .scope(Scope.of(properties.apply(getScope())))
                .optional(Boolean.parseBoolean(properties.apply(getOptional())))
                .exclusionsManager(getExclusionsManager().applyProperties(properties))
                .build();
    }

}
