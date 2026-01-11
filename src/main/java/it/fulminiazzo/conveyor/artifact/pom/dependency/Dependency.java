package it.fulminiazzo.conveyor.artifact.pom.dependency;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a Maven dependency.
 */
@Value
@Builder
public class Dependency {
    @NotNull String groupId;
    @NotNull String artifactId;
    @Builder.Default
    @Nullable String version = null;

    @Builder.Default
    @NotNull String type = "jar";

    @Builder.Default
    @Nullable String classifier = null;

    @Builder.Default
    @NotNull Scope scope = Scope.COMPILE;

    @Builder.Default
    boolean optional = false;

    @NotNull Exclusions exclusions = new Exclusions();

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
