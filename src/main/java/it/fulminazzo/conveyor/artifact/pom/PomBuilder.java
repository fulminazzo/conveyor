package it.fulminazzo.conveyor.artifact.pom;

import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

/**
 * Responsible for creating a {@link Pom} object.
 */
final class PomBuilder {
    private final @NotNull XMLStreamReader reader;

    /**
     * Instantiates a new Pom builder.
     *
     * @param inputStream the input stream
     * @throws XMLStreamException in case of reading errors
     */
    public PomBuilder(final @NotNull InputStream inputStream) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        this.reader = factory.createXMLStreamReader(inputStream);
    }

}
