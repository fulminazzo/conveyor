package it.fulminazzo.conveyor.model.dependency;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Maven dependency.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public final class Dependency extends Artifact implements DependencyLike {

    @Builder.Default
    private final @NotNull String type = "jar";

    @Builder.Default
    private final @NotNull Scope scope = Scope.COMPILE;

    @Builder.Default
    private final boolean optional = false;

    @Builder.Default
    private final @NotNull ExclusionsManager exclusionsManager = new ExclusionsManager();

    @Override
    public @NotNull String getCoordinates() {
        return DependencyLike.super.getCoordinates();
    }

}
