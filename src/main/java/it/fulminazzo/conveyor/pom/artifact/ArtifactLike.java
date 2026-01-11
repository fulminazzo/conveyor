package it.fulminazzo.conveyor.pom.artifact;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

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

}
