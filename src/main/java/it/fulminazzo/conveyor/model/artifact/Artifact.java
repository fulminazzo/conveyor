package it.fulminazzo.conveyor.model.artifact;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a general Maven artifact.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public class Artifact extends ArtifactLike {
    private final @NotNull String version;

}
