package it.fulminazzo.conveyor.model;

import it.fulminazzo.conveyor.function.SupplierException;
import it.fulminazzo.conveyor.model.dependency.RawDependency;
import it.fulminazzo.conveyor.model.metadata.DistributionManagement;
import it.fulminazzo.conveyor.model.metadata.Plugin;
import it.fulminazzo.conveyor.model.metadata.Reporting;
import it.fulminazzo.conveyor.model.metadata.Resource;
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
                case "uniqueVersion" -> builder.uniqueVersion(getCurrentTextContent());
                case "layout" -> builder.layout(getCurrentTextContent());
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

    /*
     * METADATA
     */

    /**
     * Handles the <b>&lt;pluginRepositories&gt;</b> tag in the document.
     *
     * @return the plugin repositories
     * @throws BuilderException in case of any errors
     */
    protected @NotNull Set<RawRepository> parsePluginRepositories() throws BuilderException {
        final @NotNull Map<String, RawRepository> repositories = new LinkedHashMap<>();
        onChildElements(t -> {
            if (t.equals("pluginRepository")) {
                RawRepository repository = parseRepository();
                String key = repository.getId();
                repositories.put(key, repository);
            }
        });
        return Set.copyOf(repositories.values());
    }

    /**
     * Handles the <b>&lt;modules&gt;</b> tag in the document.
     *
     * @return the modules
     * @throws BuilderException in case of any errors
     */
    protected @NotNull Collection<String> parseModules() throws BuilderException {
        return parseStringList("module");
    }

    /**
     * Handles the <b>&lt;distributionManagement&gt;</b> tag in the document.
     *
     * @return the distribution management
     * @throws BuilderException in case of reading or parsing errors
     */
    protected @NotNull DistributionManagement parseDistributionManagement() throws BuilderException {
        DistributionManagement.DistributionManagementBuilder builder = DistributionManagement.builder();
        onChildElements(t -> {
            switch (t) {
                case "repository" -> builder.repository(parseRepository());
                case "snapshotRepository" -> builder.snapshotRepository(parseRepository());
                case "site" -> builder.site(parseSite());
                case "downloadUrl" -> builder.downloadUrl(getCurrentTextContent());
                case "relocation" -> builder.relocation(parseRelocation());
                case "status" -> builder.status(getCurrentTextContent());
            }
        });
        return buildObject("distributionManagement", builder::build);
    }

    /**
     * Handles the <b>&lt;site&gt;</b> tag in the document.
     *
     * @return the site
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull DistributionManagement.Site parseSite() throws BuilderException {
        DistributionManagement.Site.SiteBuilder builder = DistributionManagement.Site.builder();
        onChildElements(t -> {
            switch (t) {
                case "id" -> builder.id(getCurrentTextContent());
                case "name" -> builder.name(getCurrentTextContent());
                case "url" -> builder.url(getCurrentTextContent());
            }
        });
        return buildObject("site", builder::build);
    }

    /**
     * Handles the <b>&lt;relocation&gt;</b> tag in the document.
     *
     * @return the relocation
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull DistributionManagement.Relocation parseRelocation() throws BuilderException {
        DistributionManagement.Relocation.RelocationBuilder builder = DistributionManagement.Relocation.builder();
        onChildElements(t -> {
            switch (t) {
                case "groupId" -> builder.groupId(getCurrentTextContent());
                case "artifactId" -> builder.artifactId(getCurrentTextContent());
                case "version" -> builder.version(getCurrentTextContent());
                case "message" -> builder.message(getCurrentTextContent());
            }
        });
        return buildObject("relocation", builder::build);
    }

    /**
     * Handles the <b>&lt;reporting&gt;</b> tag in the document.
     *
     * @return the reporting
     * @throws BuilderException in case of reading or parsing errors
     */
    protected @NotNull Reporting parseReporting() throws BuilderException {
        Reporting.ReportingBuilder builder = Reporting.builder();
        onChildElements(t -> {
            switch (t) {
                case "excludeDefaults" -> builder.excludeDefaults(getCurrentTextContent());
                case "outputDirectory" -> builder.outputDirectory(getCurrentTextContent());
                case "plugins" -> builder.plugins(List.copyOf(parseList("plugin", this::parsePlugin)));
            }
        });
        return buildObject("reporting", builder::build);
    }

    /**
     * Handles the <b>&lt;resource&gt;</b> tag in the document.
     *
     * @return the resource
     * @throws BuilderException in case of reading or parsing errors
     */
    protected @NotNull Resource parseResource() throws BuilderException {
        Resource.ResourceBuilder builder = Resource.builder();
        onChildElements(t -> {
            switch (t) {
                case "targetPath" -> builder.targetPath(getCurrentTextContent());
                case "filtering" -> builder.filtering(getCurrentTextContent());
                case "directory" -> builder.directory(getCurrentTextContent());
                case "includes" -> builder.includes(parseStringList("include"));
                case "excludes" -> builder.excludes(parseStringList("exclude"));
            }
        });
        return buildObject("resource", builder::build);
    }

    /**
     * Handles the <b>&lt;pluginManagement&gt;</b> tag in the document.
     *
     * @return the pluginManagement
     * @throws BuilderException in case of reading or parsing errors
     */
    protected @NotNull Collection<Plugin> parsePluginManagement() throws BuilderException {
        List<Plugin> plugins = new LinkedList<>();
        onChildElements(t -> {
            if (t.equals("plugins"))
                plugins.addAll(parseList("plugin", this::parsePlugin));
        });
        return plugins;
    }

    /**
     * Handles the <b>&lt;plugin&gt;</b> tag in the document.
     *
     * @return the plugin
     * @throws BuilderException in case of reading or parsing errors
     */
    protected @NotNull Plugin parsePlugin() throws BuilderException {
        Plugin.PluginBuilder builder = Plugin.builder();
        onChildElements(t -> {
            switch (t) {
                case "groupId" -> builder.groupId(getCurrentTextContent());
                case "artifactId" -> builder.artifactId(getCurrentTextContent());
                case "version" -> builder.version(getCurrentTextContent());
                case "extensions" -> builder.extensions(getCurrentTextContent());
                case "executions" -> builder.executions(List.copyOf(parseList("execution", this::parseExecution)));
                case "dependencies" -> builder.dependencies(List.copyOf(parseDependencies()));
                case "inherited" -> builder.inherited(getCurrentTextContent());
            }
        });
        return buildObject("plugin", builder::build);
    }

    /**
     * Handles the <b>&lt;execution&gt;</b> tag in the document.
     *
     * @return the execution
     * @throws BuilderException in case of reading or parsing errors
     */
    protected @NotNull Plugin.Execution parseExecution() throws BuilderException {
        Plugin.Execution.ExecutionBuilder builder = Plugin.Execution.builder();
        onChildElements(t -> {
            switch (t) {
                case "id" -> builder.id(getCurrentTextContent());
                case "phase" -> builder.phase(getCurrentTextContent());
                case "goals" -> builder.goals(parseStringList("goal"));
                case "inherited" -> builder.inherited(getCurrentTextContent());
            }
        });
        return buildObject("execution", builder::build);
    }

    /*
     * UTILS
     */

    /**
     * Reads a general list of strings from input.
     *
     * @param tagName the tag name identifying the string nodes
     * @return the list
     * @throws BuilderException in case of reading or parsing errors
     */
    protected @NotNull List<String> parseStringList(final String tagName) throws BuilderException {
        return parseList(tagName, this::getCurrentTextContent);
    }

    /**
     * Reads a general list from input.
     *
     * @param <T>             the type of the built object
     * @param tagName         the tag name
     * @param builderFunction the builder function
     * @return the list of objects
     * @throws BuilderException in case of reading or parsing errors
     */
    protected <T> @NotNull List<T> parseList(final @NotNull String tagName,
                                             final @NotNull SupplierException<T, BuilderException> builderFunction) throws BuilderException {
        List<T> list = new LinkedList<>();
        onChildElements(t -> {
            if (t.equals(tagName))
                list.add(builderFunction.get());
        });
        return list;
    }

}
