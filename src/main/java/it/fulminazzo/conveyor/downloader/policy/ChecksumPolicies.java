package it.fulminazzo.conveyor.downloader.policy;

import it.fulminazzo.conveyor.downloader.DownloadException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * A collection of known {@link ChecksumPolicy}.
 */
@Slf4j
public enum ChecksumPolicies implements ChecksumPolicy {
    /**
     * Warns about the invalid verification, but utilizes the file anyway.
     */
    WARN {
        @Override
        public boolean handleFailure(final @NotNull String resourcePath,
                                     final @NotNull Throwable cause,
                                     final @NotNull Logger logger) {
            logger.warn("Could not validate checksum for resource '{}'", resourcePath);
            logger.warn("The local version of the resource will be used anyway");
            return true;
        }
    },
    /**
     * Fails the entire process with an exception.
     */
    FAIL {
        @Override
        public boolean handleFailure(final @NotNull String resourcePath,
                                     final @NotNull Throwable cause,
                                     final @NotNull Logger logger) throws DownloadException {
            throw new DownloadException(
                    String.format("Could not validate checksum for resource '%s'", resourcePath),
                    cause);
        }
    },
    /**
     * Ignores the invalid checksum and re-downloads the file.
     */
    IGNORE {
        @Override
        public boolean handleFailure(final @NotNull String resourcePath,
                                     final @NotNull Throwable cause,
                                     final @NotNull Logger logger) {
            return true;
        }
    },
    ;

}
