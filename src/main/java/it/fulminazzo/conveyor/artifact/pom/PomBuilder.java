package it.fulminazzo.conveyor.artifact.pom;

import it.fulminazzo.conveyor.artifact.pom.repository.ChecksumPolicy;
import it.fulminazzo.conveyor.artifact.pom.repository.Repository;
import it.fulminazzo.conveyor.artifact.pom.repository.update.UpdatePolicy;
import it.fulminazzo.conveyor.function.ConsumerException;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Responsible for creating a {@link Pom} object.
 */
final class PomBuilder {
    private final @NotNull Map<String, String> properties = new HashMap<>();
    private final @NotNull Set<Repository> repositories = new HashSet<>();

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
        parseGeneric(t -> this.properties.put(t, getElementText()));
    }

    /**
     * Handles the <b>&lt;repositories&gt;</b> tag in the document.
     *
     * @throws XMLStreamException in case of reading or parsing errors
     */
    void parseRepositories() throws XMLStreamException {
        parseGeneric(t -> {
            if (t.equals("repository"))
                this.repositories.add(parseRepository());
        });
    }

    /**
     * Handles a <b>&lt;repository&gt;</b> tag in the document.
     *
     * @return the repository
     * @throws XMLStreamException in case of reading or parsing errors
     */
    @NotNull Repository parseRepository() throws XMLStreamException {
        final Repository.RepositoryBuilder builder = Repository.builder();
        parseGeneric(t -> {
            switch (t) {
                case "id" -> builder.id(getElementText());
                case "name" -> builder.name(getElementText());
                case "url" -> builder.url(getElementText());
                case "releases" -> builder.releases(parseRepositoryPolicy());
                case "snapshots" -> builder.snapshots(parseRepositoryPolicy());
            }
        });
        return builder.build();
    }

    /**
     * Generates a {@link Repository.Policy} from the current reader.
     *
     * @return the repository policy
     * @throws XMLStreamException in case of reading or parsing errors
     */
    @NotNull Repository.Policy parseRepositoryPolicy() throws XMLStreamException {
        final Repository.Policy.PolicyBuilder builder = Repository.Policy.builder();
        parseGeneric(t -> {
            String value = getElementText();
            switch (t) {
                case "enabled" -> builder.enabled(Boolean.parseBoolean(value));
                case "updatePolicy" -> builder.updatePolicy(UpdatePolicy.of(value));
                case "checksumPolicy" -> builder.checksumPolicy(ChecksumPolicy.valueOf(value.toUpperCase()));
            }
        });
        return builder.build();
    }

    private void parseGeneric(final @NotNull ConsumerException<String, XMLStreamException> onElement) throws XMLStreamException {
        String name = this.reader.getLocalName();
        while (this.reader.hasNext())
            switch (this.reader.next()) {
                case XMLStreamConstants.START_ELEMENT -> onElement.accept(this.reader.getLocalName());
                case XMLStreamConstants.END_ELEMENT -> {
                    if (this.reader.getLocalName().equals(name)) return;
                }
            }
    }

    private @NotNull String getElementText() throws XMLStreamException {
        return this.reader.getElementText();
    }

}
