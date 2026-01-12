package it.fulminazzo.conveyor.xml;

import org.jetbrains.annotations.NotNull;

/**
 * A cursor-based parser to read <b>XML</b> documents.
 */
public interface XmlParser {

    /**
     * Checks if another <b>XML</b> element is present.
     * <br>
     * <b>WARNING</b>: this method depends heavily on the current context.
     * For example, if the parser is reading the children of an element,
     * this method will return <code>false</code> for the last child,
     * even though another element after the parent one is available.
     * In this instance, calling the method again will return <code>true</code>.
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
     * @throws XmlParserException in case of any errors
     */
    @NotNull Iterable<String> children() throws XmlParserException;

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

}
