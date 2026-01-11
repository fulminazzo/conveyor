package it.fulminazzo.conveyor.artifact.pom;

import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for creating a {@link Pom} object.
 */
final class PomBuilder {
    private final @NotNull Map<String, String> properties = new HashMap<>();

    private final @NotNull XMLStreamReader reader;

    /**
     * Instantiates a new Pom builder.
     *
     * @param inputStream the input stream
     * @throws XMLStreamException in case of reading or parsing errors
     */
    public PomBuilder(final @NotNull InputStream inputStream) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        this.reader = factory.createXMLStreamReader(inputStream);
    }

    /**
     * Handles the <b>&lt;properties&gt;</b> tag in the document.
     *
     * @throws XMLStreamException in case of reading or parsing errors
     */
    void parseProperties() throws XMLStreamException {
        while (this.reader.hasNext())
            switch (this.reader.next()) {
                case XMLStreamConstants.START_ELEMENT -> {
                    String key = this.reader.getLocalName();
                    String value = this.reader.getElementText();
                    this.properties.put(key, value);
                }
                case XMLStreamConstants.END_ELEMENT -> {
                    return;
                }
            }
    }

}
