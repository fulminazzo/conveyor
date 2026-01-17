package it.fulminazzo.conveyor.xml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Stack;

/**
 * An implementation of {@link XmlParser} that uses the StAX XML library.
 */
final class XmlParserImpl implements XmlParser {
    private final @NotNull Stack<String> scopes = new Stack<>();

    private final @NotNull InputStream inputStream;
    private @Nullable XMLStreamReader reader;

    private @Nullable String nextTag;

    private @Nullable String currentTag;
    private @Nullable String currentContent;

    /**
     * Instantiates a new XML parser.
     *
     * @param inputStream the stream containing the XML document
     * @throws XmlParserException in case of initialization errors
     */
    public XmlParserImpl(final @NotNull InputStream inputStream) throws XmlParserException {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            this.inputStream = inputStream;
            this.reader = factory.createXMLStreamReader(this.inputStream);
        } catch (XMLStreamException e) {
            throw XmlParserException.of("Could not create XmlParser", e);
        }
    }

    @Override
    public boolean hasNext() throws XmlParserException {
        if (this.nextTag != null) return true;
        fetchNextTag();
        return this.nextTag != null;
    }

    @Override
    public @NotNull String next() throws XmlParserException {
        if (!hasNext()) throw XmlParserException.of("XML document does not have a next element");
        this.currentTag = this.nextTag;
        this.nextTag = null;
        this.scopes.push(this.currentTag);
        updateTag(this.currentTag);
        return this.currentTag;
    }

    @Override
    public @NotNull Iterable<String> children() throws RuntimeXmlParserException {
        int childrenDepth = this.scopes.size() + 1;
        return () -> new Iterator<>() {
            @Override
            public boolean hasNext() {
                try {
                    while (XmlParserImpl.this.hasNext()) {
                        int scopes = XmlParserImpl.this.scopes.size();
                        int currentDepth = scopes + (XmlParserImpl.this.nextTag == null ? 0 : 1);
                        if (scopes < childrenDepth - 1) return false;
                        if (currentDepth == childrenDepth) return true;
                        XmlParserImpl.this.next();
                    }
                    return false;
                } catch (XmlParserException e) {
                    throw new RuntimeXmlParserException(e);
                }
            }

            @Override
            public String next() {
                try {
                    return XmlParserImpl.this.next();
                } catch (XmlParserException e) {
                    throw new RuntimeXmlParserException(e);
                }
            }
        };
    }

    @Override
    public @NotNull String getCurrentTag() throws XmlParserException {
        if (this.currentTag == null)
            throw XmlParserException.of("No current tag found. Has next() been called?");
        return this.currentTag;
    }

    @Override
    public @NotNull String getCurrentContent() throws XmlParserException {
        if (this.currentContent == null) {
            if (isText()) this.currentContent = this.reader.getText();
            if (this.currentContent == null)
                throw XmlParserException.of(String.format("No text content of XML element '%s'", this.currentTag));
        }
        return this.currentContent;
    }

    @Override
    public void close() {
        try {
            this.inputStream.close();
            if (this.reader != null) this.reader.close();
        } catch (XMLStreamException | IOException ignored) {
        } finally {
            this.reader = null;
        }
    }

    private boolean isText() throws XmlParserException {
        try {
            if (this.reader == null) return false;
            while (this.reader.hasNext()) {
                int event = this.reader.next();
                if (event != XMLStreamConstants.CHARACTERS) {
                    handleEvent(event);
                    return false;
                }
                if (!this.reader.isWhiteSpace()) return true;
            }
            close();
            return false;
        } catch (XMLStreamException e) {
            close();
            throw XmlParserException.of("Could not get current content", e);
        }
    }

    private void updateTag(final @Nullable String newTag) {
        this.currentTag = newTag;
        this.currentContent = null;
    }

    private void fetchNextTag() throws XmlParserException {
        try {
            if (this.reader != null) {
                while (this.reader.hasNext())
                    if (handleEvent(this.reader.next())) return;
                close();
            }
        } catch (XMLStreamException e) {
            close();
            throw XmlParserException.of("Could not fetch next tag from XML document", e);
        }
    }

    private boolean handleEvent(int event) {
        if (this.reader == null) throw new IllegalStateException("reader has already been consumed");
        if (event == XMLStreamConstants.START_ELEMENT) {
            this.nextTag = this.reader.getLocalName();
            return true;
        } else if (event == XMLStreamConstants.END_ELEMENT) {
            this.scopes.pop();
            updateTag(null);
            this.nextTag = null;
        }
        return false;
    }

}
