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

    private final @NotNull Exclusions exclusions = new Exclusions();

    /**
     * Defines the scope of this dependency.
     */
    public enum Scope {
        /**
         * The dependency will be present anywhere.
         */
        COMPILE,
        /**
         * The dependency will be only used for compilation.
         */
        PROVIDED,
        /**
         * The dependency will be only used on runtime.
         */
        RUNTIME,
        /**
         * The dependency will be loaded from disk and used anywhere.
         */
        SYSTEM,
        /**
         * The dependency will be only used for testing.
         */
        TEST,
        /**
         * The dependency will be used to import other attributes.
         */
        IMPORT
    }

}
