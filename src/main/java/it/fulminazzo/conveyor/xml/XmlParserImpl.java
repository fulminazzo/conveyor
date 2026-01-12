package it.fulminazzo.conveyor.xml;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.Stack;

/**
 * An implementation of {@link XmlParser} that uses the StAX XML library.
 */
@RequiredArgsConstructor
final class XmlParserImpl implements XmlParser {
    private final @NotNull Stack<String> scopes = new Stack<>();

    private final @NotNull XMLStreamReader reader;

    private @Nullable String nextTag;

    private @Nullable String currentTag;
    private @Nullable String currentContent;

    @Override
    public boolean hasNext() throws XmlParserException {
        if (this.nextTag != null) return true;
        this.nextTag = fetchNextTag();
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
    public @NotNull Iterable<String> children() throws XmlParserException {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull String getCurrentTag() throws XmlParserException {
        if (this.currentTag == null)
            throw XmlParserException.of("No current tag found. Has next() been called?");
        return this.currentTag;
    }

    @Override
    public @NotNull String getCurrentContent() throws XmlParserException {
        throw new UnsupportedOperationException();
    }

    private void updateTag(final @Nullable String newTag) {
        this.currentTag = newTag;
        this.currentContent = null;
    }

    private @Nullable String fetchNextTag() throws XmlParserException {
        try {
            while (this.reader.hasNext()) {
                int event = this.reader.next();
                if (event == XMLStreamConstants.START_ELEMENT)
                    return this.reader.getLocalName();
                else if (event == XMLStreamConstants.END_ELEMENT) {
                    this.scopes.pop();
                    updateTag(null);
                    return null;
                }
            }
            return null;
        } catch (XMLStreamException e) {
            throw XmlParserException.of("Could not fetch next tag from XML document", e);
        }
    }

}
