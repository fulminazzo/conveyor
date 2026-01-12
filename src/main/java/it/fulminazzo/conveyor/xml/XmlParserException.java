package it.fulminazzo.conveyor.xml;

import lombok.AccessLevel;
import lombok.experimental.StandardException;
import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown by {@link XmlParser}.
 */
@StandardException(access = AccessLevel.PRIVATE)
public final class XmlParserException extends Exception {

    /**
     * Instantiates a new XML parser exception.
     *
     * @param message the message
     * @return the XML parser exception
     */
    static @NotNull XmlParserException of(final @NotNull String message) {
        return new XmlParserException(message);
    }

    /**
     * Instantiates a new XML parser exception.
     *
     * @param message the message
     * @param cause   the cause
     * @return the XML parser exception
     */
    static @NotNull XmlParserException of(final @NotNull String message,
                                          final @NotNull Throwable cause) {
        String finalMessage = message;
        String causeMessage = cause.getMessage();
        if (causeMessage != null) finalMessage += ": " + causeMessage;
        return new XmlParserException(finalMessage, cause);
    }

}