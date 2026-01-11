package it.fulminazzo.conveyor.pom;

import lombok.AccessLevel;
import lombok.experimental.StandardException;
import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown by {@link PomBuilder} and related classes.
 */
@StandardException(access = AccessLevel.PRIVATE)
public final class ParserException extends Exception {

    /**
     * Instantiates a new Parser exception.
     *
     * @param message the message
     * @return the parser exception
     */
    static @NotNull ParserException of(final @NotNull String message) {
        return new ParserException(message);
    }

    /**
     * Instantiates a new Parser exception.
     *
     * @param message the message
     * @param cause   the cause
     * @return the parser exception
     */
    static @NotNull ParserException of(final @NotNull String message,
                                       final @NotNull Throwable cause) {
        String finalMessage = message;
        String causeMessage = cause.getMessage();
        if (causeMessage != null) finalMessage += ": " + causeMessage;
        return new ParserException(finalMessage, cause);
    }

}
