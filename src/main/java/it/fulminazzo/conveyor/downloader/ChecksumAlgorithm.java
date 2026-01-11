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

}
