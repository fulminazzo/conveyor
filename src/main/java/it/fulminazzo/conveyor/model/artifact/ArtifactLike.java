package it.fulminazzo.conveyor.model.artifact;

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
public abstract class ArtifactLike {
    protected final @NotNull String groupId;
    protected final @NotNull String artifactId;

    @Builder.Default
    private final @Nullable String classifier = null;

}
