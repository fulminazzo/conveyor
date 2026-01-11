package it.fulminazzo.conveyor.pom;

import it.fulminazzo.conveyor.pom.dependency.Dependency;
import it.fulminazzo.conveyor.pom.repository.ChecksumPolicy;
import it.fulminazzo.conveyor.pom.repository.Repository;
import it.fulminazzo.conveyor.pom.repository.update.UpdatePolicy;
import it.fulminazzo.conveyor.function.ConsumerException;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.*;

/**
 * Responsible for creating a {@link Pom} object.
 */
final class PomBuilder {
    private final @NotNull Map<String, String> properties = new HashMap<>();
    private final @NotNull Set<Repository> repositories = new HashSet<>();
    private final @NotNull Set<Dependency> dependencyManagement = new HashSet<>();
    private final @NotNull List<Dependency> dependencies = new ArrayList<>();

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

    /**
     * Handles a <b>&lt;dependencyManagement&gt;</b> tag in the document.
     *
     * @throws XMLStreamException in case of reading or parsing errors
     */
    void parseDependencyManagement() throws XMLStreamException {
        parseGeneric(t -> {
            if (t.equals("dependency"))
                this.dependencyManagement.add(parseDependency());
        });
    }

    /**
     * Handles a <b>&lt;dependencies&gt;</b> tag in the document.
     *
     * @throws XMLStreamException in case of reading or parsing errors
     */
    void parseDependencies() throws XMLStreamException {
        parseGeneric(t -> {
            if (t.equals("dependency"))
                this.dependencies.add(parseDependency());
        });
    }

    /**
     * Handles a <b>&lt;dependency&gt;</b> tag in the document.
     *
     * @return the dependency
     * @throws XMLStreamException in case of reading or parsing errors
     */
    @NotNull Dependency parseDependency() throws XMLStreamException {
        final Dependency.DependencyBuilder builder = Dependency.builder();
        List<String[]> exclusions = new ArrayList<>();
        parseGeneric(t -> {
            switch (t) {
                case "groupId" -> builder.groupId(getElementText());
                case "artifactId" -> builder.artifactId(getElementText());
                case "version" -> builder.version(getElementText());
                case "type" -> builder.type(getElementText());
                case "classifier" -> builder.classifier(getElementText());
                case "scope" -> builder.scope(Dependency.Scope.valueOf(getElementText().toUpperCase()));
                case "optional" -> builder.optional(Boolean.parseBoolean(getElementText()));
                case "exclusions" -> parseGeneric(l -> {
                    if (l.equals("exclusion")) {
                        String[] exclusionData = new String[2];
                        parseGeneric(e -> {
                            switch (e) {
                                case "groupId" -> exclusionData[0] = getElementText();
                                case "artifactId" -> exclusionData[1] = getElementText();
                            }
                        });
                        exclusions.add(exclusionData);
                    }
                });
            }
        });
        Dependency dependency = builder.build();
        exclusions.forEach(a -> dependency.getExclusions().add(a[0], a[1]));
        return dependency;
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
