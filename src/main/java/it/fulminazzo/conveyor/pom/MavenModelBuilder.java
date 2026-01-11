package it.fulminazzo.conveyor.pom;

import it.fulminazzo.conveyor.function.ConsumerException;
import it.fulminazzo.conveyor.pom.dependency.Dependency;
import it.fulminazzo.conveyor.pom.repository.ChecksumPolicy;
import it.fulminazzo.conveyor.pom.repository.Repository;
import it.fulminazzo.conveyor.pom.repository.update.UpdatePolicy;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.*;
import java.util.function.Supplier;

/**
 * Represents a general MavenModel object type builder.
 */
@RequiredArgsConstructor
abstract class MavenModelBuilder {
    /**
     * The Properties.
     */
    protected final @NotNull Map<String, String> properties = new HashMap<>();
    /**
     * The Repositories.
     */
    protected final @NotNull Set<Repository> repositories = new HashSet<>();
    /**
     * The Dependency management.
     */
    protected final @NotNull Set<Dependency> dependencyManagement = new HashSet<>();
    /**
     * The Dependencies.
     */
    protected final @NotNull List<Dependency> dependencies = new ArrayList<>();

    /**
     * The Reader.
     */
    protected final @NotNull XMLStreamReader reader;

    /**
     * Handles the <b>&lt;properties&gt;</b> tag in the document.
     *
     * @throws ParserException in case of reading or parsing errors
     */
    protected void parseProperties() throws ParserException {
        parseGeneric(t -> this.properties.put(t, getElementText()));
    }

    /**
     * Handles the <b>&lt;repositories&gt;</b> tag in the document.
     *
     * @throws ParserException in case of reading or parsing errors
     */
    protected void parseRepositories() throws ParserException {
        parseGeneric(t -> {
            if (t.equals("repository"))
                this.repositories.add(parseRepository());
        });
    }

    /**
     * Handles a <b>&lt;repository&gt;</b> tag in the document.
     *
     * @return the repository
     * @throws ParserException in case of reading or parsing errors
     */
    protected @NotNull Repository parseRepository() throws ParserException {
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
        return buildObject("repository", builder::build);
    }

    /**
     * Generates a {@link Repository.Policy} from the current reader.
     *
     * @return the repository policy
     * @throws ParserException in case of reading or parsing errors
     */
    protected @NotNull Repository.Policy parseRepositoryPolicy() throws ParserException {
        final Repository.Policy.PolicyBuilder builder = Repository.Policy.builder();
        parseGeneric(t -> {
            String value = getElementText();
            switch (t) {
                case "enabled" -> builder.enabled(Boolean.parseBoolean(value));
                case "updatePolicy" -> builder.updatePolicy(UpdatePolicy.of(value));
                case "checksumPolicy" -> builder.checksumPolicy(ChecksumPolicy.valueOf(value.toUpperCase()));
            }
        });
        return buildObject("repository policy", builder::build);
    }

    /**
     * Handles a <b>&lt;dependencyManagement&gt;</b> tag in the document.
     *
     * @throws ParserException in case of reading or parsing errors
     */
    protected void parseDependencyManagement() throws ParserException {
        parseGeneric(t -> {
            if (t.equals("dependency"))
                this.dependencyManagement.add(parseDependency());
        });
    }

    /**
     * Handles a <b>&lt;dependencies&gt;</b> tag in the document.
     *
     * @throws ParserException in case of reading or parsing errors
     */
    protected void parseDependencies() throws ParserException {
        parseGeneric(t -> {
            if (t.equals("dependency"))
                this.dependencies.add(parseDependency());
        });
    }

    /**
     * Handles a <b>&lt;dependency&gt;</b> tag in the document.
     *
     * @return the dependency
     * @throws ParserException in case of reading or parsing errors
     */
    protected @NotNull Dependency parseDependency() throws ParserException {
        final Dependency.DependencyBuilder<?, ?> builder = Dependency.builder();
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
        Dependency dependency = buildObject("dependency", builder::build);
        exclusions.forEach(a -> dependency.getExclusions().add(a[0], a[1]));
        return dependency;
    }

    /**
     * Replaces many <code>Builder#build()</code> calls in this class.
     * Replaces any building errors with a {@link ParserException}.
     *
     * @param <T>           the type of the built object
     * @param name          the name of the building object
     * @param buildFunction the build function
     * @return the built object
     * @throws ParserException in case of any errors
     */
    protected <T> @NotNull T buildObject(final @NotNull String name,
                               final @NotNull Supplier<T> buildFunction) throws ParserException {
        try {
            return buildFunction.get();
        } catch (RuntimeException e) {
            throw ParserException.of(String.format("Could not build %s", name), e);
        }
    }

    /**
     * Parses a generic XML element.
     * Applies the given function, until a matching tag is met.
     *
     * @param onElement the function to apply
     * @throws ParserException in case of any errors
     */
    protected void parseGeneric(final @NotNull ConsumerException<String, ParserException> onElement) throws ParserException {
        String name = this.reader.getLocalName();
        try {
            while (this.reader.hasNext())
                switch (this.reader.next()) {
                    case XMLStreamConstants.START_ELEMENT -> onElement.accept(this.reader.getLocalName());
                    case XMLStreamConstants.END_ELEMENT -> {
                        if (this.reader.getLocalName().equals(name)) return;
                    }
                }
        } catch (XMLStreamException e) {
            throw ParserException.of("Could not get next XML element", e);
        }
    }

    /**
     * Gets element text.
     *
     * @return the element text
     * @throws ParserException in case of reading or parsing errors
     */
    protected @NotNull String getElementText() throws ParserException {
        try {
            return this.reader.getElementText();
        } catch (XMLStreamException e) {
            throw ParserException.of(String.format("Could not get textual element of '%s'",
                    this.reader.getLocalName()), e
            );
        }
    }

}
