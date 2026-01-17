package it.fulminazzo.conveyor.xml;

import org.jetbrains.annotations.NotNull;

/**
 * A wrapper for handling {@link XmlParserException} in an unchecked exception.
 */
public final class RuntimeXmlParserException extends RuntimeException {

    /**
     * Instantiates a new Runtime XML parser exception.
     *
     * @param cause the cause
     */
    RuntimeXmlParserException(final @NotNull XmlParserException cause) {
        super(cause);
    }

    @Override
    public synchronized @NotNull XmlParserException getCause() {
        return (XmlParserException) super.getCause();
    }

}
