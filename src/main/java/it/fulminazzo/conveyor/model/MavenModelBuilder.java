package it.fulminazzo.conveyor.model;

import it.fulminazzo.conveyor.model.dependency.Dependency;
import it.fulminazzo.conveyor.model.repository.ChecksumPolicy;
import it.fulminazzo.conveyor.model.repository.Repository;
import it.fulminazzo.conveyor.model.repository.update.UpdatePolicy;
import it.fulminazzo.conveyor.xml.XmlParser;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a general MavenModel object type builder.
 *
 * @param <O> the type of the built object
 */
public abstract class MavenModelBuilder<O extends MavenModel> extends XmlObjectBuilder<O> {
    protected final @NotNull Map<String, String> properties = new HashMap<>();
    protected final @NotNull Map<String, Repository> repositories = new LinkedHashMap<>();
    protected final @NotNull Map<String, Dependency> dependencyManagement = new LinkedHashMap<>();
    protected final @NotNull Map<String, Dependency> dependencies = new LinkedHashMap<>();

    /**
     * Instantiates a new Maven model builder.
     *
     * @param parser the XML parser to read data from
     */
    public MavenModelBuilder(final @NotNull XmlParser parser) {
        super(parser);
    }

    /**
     * Parses the given document trying to populate all the fields.
     *
     * @throws BuilderException in case of any errors
     */
    protected abstract void parseDocument() throws BuilderException;

    /**
     * Handles the <b>&lt;properties&gt;</b> tag in the document.
     *
     * @throws BuilderException in case of any errors
     */
    protected void parseProperties() throws BuilderException {
        onChildElements(t -> this.properties.put(t, getCurrentTextContent()));
    }

    /**
     * Handles the <b>&lt;repositories&gt;</b> tag in the document.
     *
     * @throws BuilderException in case of any errors
     */
    protected void parseRepositories() throws BuilderException {
        onChildElements(t -> {
            if (t.equals("repository")) {
                Repository repository = parseRepository();
                String key = repository.getId();
                this.repositories.put(key, repository);
            }
        });
    }

    /**
     * Handles a <b>&lt;repository&gt;</b> tag in the document.
     *
     * @return the repository
     * @throws BuilderException in case of any errors
     */
    protected @NotNull Repository parseRepository() throws BuilderException {
        final Repository.RepositoryBuilder builder = Repository.builder();
        onChildElements(t -> {
            switch (t) {
                case "id" -> builder.id(getCurrentTextContent());
                case "name" -> builder.name(getCurrentTextContent());
                case "url" -> builder.url(getCurrentTextContent());
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
     * @throws BuilderException in case of any errors
     */
    protected @NotNull Repository.Policy parseRepositoryPolicy() throws BuilderException {
        final Repository.Policy.PolicyBuilder builder = Repository.Policy.builder();
        onChildElements(t -> {
            String value = getCurrentTextContent();
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
     * @throws BuilderException in case of any errors
     */
    protected void parseDependencyManagement() throws BuilderException {
        onChildElements(t -> {
            if (t.equals("dependency")) {
                Dependency dependency = parseDependency();
                String key = dependency.getCoordinates();
                this.dependencyManagement.put(key, dependency);
            }
        });
    }

    /**
     * Handles a <b>&lt;dependencies&gt;</b> tag in the document.
     *
     * @throws BuilderException in case of any errors
     */
    protected void parseDependencies() throws BuilderException {
        onChildElements(t -> {
            if (t.equals("dependency")) {
                Dependency dependency = parseDependency();
                String key = dependency.getCoordinates();
                this.dependencies.put(key, dependency);
            }
        });
    }

    /**
     * Handles a <b>&lt;dependency&gt;</b> tag in the document.
     *
     * @return the dependency
     * @throws BuilderException in case of any errors
     */
    protected @NotNull Dependency parseDependency() throws BuilderException {
        final Dependency.DependencyBuilder<?, ?> builder = Dependency.builder();
        List<String[]> exclusions = new ArrayList<>();
        onChildElements(t -> {
            switch (t) {
                case "groupId" -> builder.groupId(getCurrentTextContent());
                case "artifactId" -> builder.artifactId(getCurrentTextContent());
                case "version" -> builder.version(getCurrentTextContent());
                case "type" -> builder.type(getCurrentTextContent());
                case "classifier" -> builder.classifier(getCurrentTextContent());
                case "scope" -> builder.scope(Dependency.Scope.valueOf(getCurrentTextContent().toUpperCase()));
                case "optional" -> builder.optional(Boolean.parseBoolean(getCurrentTextContent()));
                case "exclusions" -> onChildElements(l -> {
                    if (l.equals("exclusion")) {
                        String[] exclusionData = new String[2];
                        onChildElements(e -> {
                            switch (e) {
                                case "groupId" -> exclusionData[0] = getCurrentTextContent();
                                case "artifactId" -> exclusionData[1] = getCurrentTextContent();
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

}
