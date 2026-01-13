package it.fulminazzo.conveyor.model.dependency;

import org.jetbrains.annotations.NotNull;

/**
 * Defines the scope of a {@link Dependency}.
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
    IMPORT;

    /**
     * Gets the <b>XML</b> value of this scope.
     *
     * @return the value
     */
    public @NotNull String value() {
        return name().toLowerCase();
    }

    /**
     * Gets the scope whose {@link #value()} equals to the given one.
     *
     * @param value the value
     * @return the scope
     */
    public static @NotNull Scope of(final @NotNull String value) {
        for (Scope scope : values())
            if (scope.value().equals(value))
                return scope;
        throw new IllegalArgumentException(String.format("Could not find scope of '%s'", value));
    }

}
