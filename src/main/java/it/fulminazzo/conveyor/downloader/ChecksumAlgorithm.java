package it.fulminazzo.conveyor.downloader;

import org.jetbrains.annotations.NotNull;

/**
 * Represents all the supported checksum algorithms.
 */
enum ChecksumAlgorithm {
    MD5,
    SHA1,
    SHA256,
    SHA512;

    /**
     * Gets the expected file extension associated with this algorithm.
     *
     * @return the extension
     */
    public @NotNull String getExtension() {
        return name().toLowerCase();
    }

    /**
     * Gets the associated algorithm from the given extension.
     *
     * @param extension the extension
     * @return the checksum algorithm
     */
    public static @NotNull ChecksumAlgorithm fromExtension(final @NotNull String extension) {
        for (ChecksumAlgorithm algorithm : values())
            if (algorithm.getExtension().equals(extension)) return algorithm;
        throw new IllegalArgumentException(String.format("Could not find matching algorithm from extension '%s'", extension));
    }

}
