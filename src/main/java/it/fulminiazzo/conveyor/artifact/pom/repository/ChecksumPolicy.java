package it.fulminiazzo.conveyor.artifact.pom.repository;

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
    IGNORE

}
