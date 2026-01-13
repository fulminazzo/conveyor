package it.fulminazzo.conveyor.model;

import it.fulminazzo.conveyor.xml.XmlParserException;
import lombok.AccessLevel;
import lombok.experimental.StandardException;
import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown by {@link XmlDtoBuilder} and subclasses.
 */
@StandardException(access = AccessLevel.PACKAGE)
public final class BuilderException extends Exception {

    /**
     * Instantiates a new Builder exception.
     *
     * @param cause the cause of the exception
     */
    BuilderException(final @NotNull XmlParserException cause) {
        this(cause.getMessage(), cause.getCause());
    }

}
