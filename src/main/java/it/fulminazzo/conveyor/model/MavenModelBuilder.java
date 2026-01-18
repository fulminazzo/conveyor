package it.fulminazzo.conveyor.model;

import it.fulminazzo.conveyor.model.dependency.RawDependency;
import it.fulminazzo.conveyor.model.repository.RawRepository;
import it.fulminazzo.conveyor.xml.XmlParser;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a general MavenModel object type builder.
 *
 * @param <O> the type of the built object
 */
public abstract class MavenModelBuilder<O extends MavenModel> extends XmlObjectBuilder<O> {

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
     * @return the properties
     * @throws BuilderException in case of any errors
     */
    protected @NotNull Map<String, String> parseProperties() throws BuilderException {
        final @NotNull Map<String, String> properties = new HashMap<>();
        onChildElements(t -> {
            String value;
            try {
                value = getCurrentTextContent();
            } catch (BuilderException e) {
                value = "";
            }
            properties.put(t, value);
        });
        return properties;
    }

    /**
     * Handles the <b>&lt;repositories&gt;</b> tag in the document.
     *
     * @return the repositories
     * @throws BuilderException in case of any errors
     */
    protected @NotNull Set<RawRepository> parseRepositories() throws BuilderException {
        final @NotNull Map<String, RawRepository> repositories = new LinkedHashMap<>();
        onChildElements(t -> {
            if (t.equals("repository")) {
                RawRepository repository = parseRepository();
                String key = repository.getId();
                repositories.put(key, repository);
            }
        });
        return Set.copyOf(repositories.values());
    }

    /**
     * Handles a <b>&lt;repository&gt;</b> tag in the document.
     *
     * @return the repository
     * @throws BuilderException in case of any errors
     */
    protected @NotNull RawRepository parseRepository() throws BuilderException {
        final RawRepository.RawRepositoryBuilder builder = RawRepository.builder();
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
     * Generates a {@link RawRepository.Policy} from the current reader.
     *
     * @return the repository policy
     * @throws BuilderException in case of any errors
     */
    protected @NotNull RawRepository.Policy parseRepositoryPolicy() throws BuilderException {
        final RawRepository.Policy.PolicyBuilder builder = RawRepository.Policy.builder();
        onChildElements(t -> {
            String value = getCurrentTextContent();
            switch (t) {
                case "enabled" -> builder.enabled(value);
                case "updatePolicy" -> builder.updatePolicy(value);
                case "checksumPolicy" -> builder.checksumPolicy(value);
            }
        });
        return buildObject("repository policy", builder::build);
    }

    /**
     * Handles a <b>&lt;dependencyManagement&gt;</b> tag in the document.
     *
     * @return the dependency management
     * @throws BuilderException in case of any errors
     */
    protected @NotNull Set<RawDependency> parseDependencyManagement() throws BuilderException {
        final @NotNull Map<String, RawDependency> dependencyManagement = new LinkedHashMap<>();
        onChildElements(t -> {
            if (t.equals("dependencies"))
                onChildElements(t2 -> {
                    if (t2.equals("dependency")) {
                        RawDependency dependency = parseDependency();
                        String key = dependency.getCoordinates();
                        dependencyManagement.put(key, dependency);
                    }
                });
        });
        return Set.copyOf(dependencyManagement.values());
    }

    /**
     * Handles a <b>&lt;dependencies&gt;</b> tag in the document.
     *
     * @return the dependencies
     * @throws BuilderException in case of any errors
     */
    protected @NotNull List<RawDependency> parseDependencies() throws BuilderException {
        final @NotNull Map<String, RawDependency> dependencies = new LinkedHashMap<>();
        onChildElements(t -> {
            if (t.equals("dependency")) {
                RawDependency dependency = parseDependency();
                String key = dependency.getCoordinates();
                dependencies.put(key, dependency);
            }
        });
        return List.copyOf(dependencies.values());
    }

    /**
     * Handles a <b>&lt;dependency&gt;</b> tag in the document.
     *
     * @return the dependency
     * @throws BuilderException in case of any errors
     */
    protected @NotNull RawDependency parseDependency() throws BuilderException {
        final RawDependency.RawDependencyBuilder<?, ?> builder = RawDependency.builder();
        List<String[]> exclusions = new ArrayList<>();
        onChildElements(t -> {
            switch (t) {
                case "groupId" -> builder.groupId(getCurrentTextContent());
                case "artifactId" -> builder.artifactId(getCurrentTextContent());
                case "version" -> builder.version(getCurrentTextContent());
                case "type" -> builder.type(getCurrentTextContent());
                case "classifier" -> builder.classifier(getCurrentTextContent());
                case "scope" -> builder.scope(getCurrentTextContent());
                case "optional" -> builder.optional(getCurrentTextContent());
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
        RawDependency dependency = buildObject("dependency", builder::build);
        exclusions.forEach(a -> dependency.getExclusions().add(a[0], a[1]));
        return dependency;
    }

}
