package it.fulminazzo.conveyor.model.artifact;

import it.fulminazzo.conveyor.property.PropertyAccessible;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Defines a general artifact with bare minimum data.
 */
@Getter
@EqualsAndHashCode
@ToString
@SuperBuilder
public abstract class ArtifactLike implements PropertyAccessible {
    protected final @NotNull String groupId;
    protected final @NotNull String artifactId;

    @Builder.Default
    protected final @Nullable String classifier = null;

}
