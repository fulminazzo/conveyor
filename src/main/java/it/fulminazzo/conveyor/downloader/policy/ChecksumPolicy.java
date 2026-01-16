package it.fulminazzo.conveyor.downloader.policy;

import it.fulminazzo.conveyor.downloader.DownloadException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * A function to handle the failure of a checksum verification.
 */
@FunctionalInterface
public interface ChecksumPolicy {

    /**
     * Checks on the given cause and returns an appropriate result.
     *
     * @param resourcePath the resource path
     * @param cause        the cause
     * @param logger       the logger
     * @return <code>true</code> to verify the resource anyway
     * @throws DownloadException in case the verification is failed
     */
    boolean handleFailure(final @NotNull String resourcePath,
                          final @NotNull Throwable cause,
                          final @NotNull Logger logger) throws DownloadException;

}
