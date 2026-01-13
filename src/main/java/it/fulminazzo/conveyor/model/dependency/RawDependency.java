package it.fulminazzo.conveyor.model.dependency;

import it.fulminazzo.conveyor.model.artifact.ArtifactLike;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a raw {@link Dependency},
 * where all the fields (except {@link #exclusions})
 * are initialized in their <b>XML</b> form.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public final class RawDependency extends ArtifactLike implements DependencyLike {

    @Builder.Default
    private final @Nullable String version = null;

    @Builder.Default
    private final @NotNull String type = "jar";

    @Builder.Default
    private final @NotNull String scope = Dependency.Scope.COMPILE.value();

    @Builder.Default
    private final @NotNull String optional = String.valueOf(Boolean.FALSE);

    private final @NotNull Exclusions exclusions = new Exclusions();

}
