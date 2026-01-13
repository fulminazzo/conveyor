package it.fulminazzo.conveyor.xml;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

/**
 * A cursor-based parser to read <b>XML</b> documents.
 */
public interface XmlParser extends AutoCloseable {

    /**
     * Checks if another <b>XML</b> element is present.
     *
     * @return true if there is
     * @throws XmlParserException in case of any errors
     */
    boolean hasNext() throws XmlParserException;

    /**
     * Attempts to get the next element in the document.
     *
     * @return the element tag
     * @throws XmlParserException in case of any errors or missing element
     */
    @NotNull String next() throws XmlParserException;

    /**
     * Gets all the children of the current element.
     *
     * @return the children
     * @throws RuntimeXmlParserException if there were errors in the internal {@link java.util.Iterator}.                                   The actual {@link XmlParserException} that caused the error will be available in                                   {@link RuntimeXmlParserException#getCause()}.
     */
    @NotNull Iterable<String> children() throws RuntimeXmlParserException;

    /**
     * Gets the tag of the latest read element.
     * Requires {@link #next()} to be called.
     *
     * @return the current tag
     * @throws XmlParserException if the tag has not been read yet
     */
    @NotNull String getCurrentTag() throws XmlParserException;

    /**
     * Gets the textual contents of the current element.
     *
     * @return the contents
     * @throws XmlParserException if the element does not have any
     */
    @NotNull String getCurrentContent() throws XmlParserException;

    /**
     * Closes the current parser.
     */
    @Override
    void close();

    /**
     * Instantiates a new XML parser.
     *
     * @param inputStream the stream containing the XML document
     * @return the XML parser
     * @throws XmlParserException in case of initialization errors
     */
    static @NotNull XmlParser newParser(final @NotNull InputStream inputStream) throws XmlParserException {
        return new XmlParserImpl(inputStream);
    }

}
