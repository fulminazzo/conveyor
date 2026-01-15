package it.fulminazzo.conveyor.downloader.policy;

import it.fulminazzo.conveyor.downloader.DownloadException;
import org.jetbrains.annotations.NotNull;

/**
 * A function to handle the failure of a checksum verification.
 */
@FunctionalInterface
public interface ChecksumPolicy {

    /**
     * Checks on the given cause and returns an appropriate result.
     *
     * @param cause the cause
     * @return <code>true</code> to verify the resource anyway
     * @throws DownloadException in case the verification is failed
     */
    boolean handleFailure(final @NotNull Throwable cause) throws DownloadException;

}
