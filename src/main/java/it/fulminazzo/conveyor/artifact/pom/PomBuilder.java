package it.fulminazzo.conveyor.artifact.pom;

import it.fulminazzo.conveyor.artifact.pom.repository.ChecksumPolicy;
import it.fulminazzo.conveyor.artifact.pom.repository.Repository;
import it.fulminazzo.conveyor.artifact.pom.repository.update.UpdatePolicy;
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
                    String value = getElementText();
                    this.properties.put(key, value);
                }
                case XMLStreamConstants.END_ELEMENT -> {
                    return;
                }
            }
    }

    /**
     * Handles a <b>&lt;repository&gt;</b> tag in the document.
     *
     * @return the repository
     * @throws XMLStreamException in case of reading or parsing errors
     */
    @NotNull Repository parseRepository() throws XMLStreamException {
        Repository.RepositoryBuilder builder = Repository.builder();
        while (this.reader.hasNext())
            switch (this.reader.next()) {
                case XMLStreamConstants.START_ELEMENT -> {
                    String tagName = this.reader.getLocalName();
                    switch (tagName) {
                        case "id" -> builder.id(getElementText());
                        case "name" -> builder.name(getElementText());
                        case "url" -> builder.url(getElementText());
                        case "releases" -> builder.releases(parseRepositoryPolicy());
                        case "snapshots" -> builder.snapshots(parseRepositoryPolicy());
                    }
                }
                case XMLStreamConstants.END_ELEMENT -> {
                    if (this.reader.getLocalName().equals("repository"))
                        return builder.build();
                }
            }
        return builder.build();
    }

    /**
     * Generates a {@link Repository.Policy} from the current reader.
     *
     * @return the repository policy
     * @throws XMLStreamException in case of reading or parsing errors
     */
    @NotNull Repository.Policy parseRepositoryPolicy() throws XMLStreamException {
        Repository.Policy.PolicyBuilder builder = Repository.Policy.builder();
        String name = this.reader.getLocalName();
        while (this.reader.hasNext())
            switch (this.reader.next()) {
                case XMLStreamConstants.START_ELEMENT -> {
                    String tagName = this.reader.getLocalName();
                    String value = getElementText();
                    switch (tagName) {
                        case "enabled" -> builder.enabled(Boolean.parseBoolean(value));
                        case "updatePolicy" -> builder.updatePolicy(UpdatePolicy.of(value));
                        case "checksumPolicy" -> builder.checksumPolicy(ChecksumPolicy.valueOf(value.toUpperCase()));
                    }
                }
                case XMLStreamConstants.END_ELEMENT -> {
                    if (this.reader.getLocalName().equals(name))
                        return builder.build();
                }
            }
        return builder.build();
    }

    private @NotNull String getElementText() throws XMLStreamException {
        return this.reader.getElementText();
    }

}
