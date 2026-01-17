package it.fulminazzo.conveyor.model.repository;

import org.jetbrains.annotations.NotNull;

/**
 * Represents how an invalid checksum should be handled.
 */
public enum ChecksumPolicy {
    /**
     * Warns about the error, but proceeds.
     */
    WARN,
    /**
     * Fails the entire process.
     */
    FAIL,
    /**
     * Ignores the error.
     */
    IGNORE;

    /**
     * Gets the <b>XML</b> value of this checksum policy.
     *
     * @return the value
     */
    public @NotNull String value() {
        return name().toLowerCase();
    }

    /**
     * Gets the checksum policy whose {@link #value()} equals to the given one.
     *
     * @param value the value
     * @return the checksum policy
     */
    public static @NotNull ChecksumPolicy of(final @NotNull String value) {
        for (ChecksumPolicy policy : values())
            if (policy.value().equals(value))
                return policy;
        throw new IllegalArgumentException(String.format("Could not find checksum policy of '%s'", value));
    }

}
