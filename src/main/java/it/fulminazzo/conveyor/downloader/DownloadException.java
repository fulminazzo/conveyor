package it.fulminazzo.conveyor.downloader;

import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown by {@link DownloadException}.
 */
public final class DownloadException extends Exception {

    /**
     * Instantiates a new Download exception.
     *
     * @param message the message
     */
    DownloadException(final @NotNull String message) {
        super(message);
    }

    /**
     * Instantiates a new Download exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    DownloadException(final @NotNull String message, final @NotNull Throwable cause) {
        super(message, cause);
    }

}
