package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.dependency.Dependency;
import it.fulminazzo.conveyor.model.dependency.RawDependency;
import it.fulminazzo.conveyor.model.dependency.Scope;
import it.fulminazzo.conveyor.model.pom.resolver.PomResolverException;
import it.fulminazzo.conveyor.model.pom.resolver.RepositoryPomResolver;
import it.fulminazzo.conveyor.model.profile.Profile;
import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext;
import it.fulminazzo.conveyor.model.properties.MavenProjectProperties;
import it.fulminazzo.conveyor.model.properties.Properties;
import it.fulminazzo.conveyor.model.repository.RawRepository;
import it.fulminazzo.conveyor.model.repository.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Responsible for creating a {@link EffectivePom} object.
 */
public final class EffectivePomBuilder {
    private final @NotNull Pom startingPom;
    private final @NotNull RepositoryPomResolver pomResolver;
    private final @NotNull File workingDir;

    private final @NotNull Logger logger;

    private final @NotNull Set<Profile> activeProfiles = new HashSet<>();
    private final @NotNull MavenProjectProperties properties;
    private final @NotNull Map<String, String> dependencyManagement = new HashMap<>();
    private final @NotNull Map<String, Dependency> dependencies = new LinkedHashMap<>();

    private @Nullable EffectivePomBuilder parentEffectivePomBuilder;

    /**
     * Instantiates a new Effective pom builder.
     *
     * @param startingPom the starting pom
     * @param pomResolver the pom resolver
     * @param workingDir  the working directory
     */
    EffectivePomBuilder(final @NotNull Pom startingPom,
                        final @NotNull RepositoryPomResolver pomResolver,
                        final @NotNull File workingDir) {
        this.startingPom = startingPom;
        this.pomResolver = pomResolver;
        this.workingDir = workingDir;

        this.logger = LoggerFactory.getLogger(String.format("EffectivePOMBuilder(%s)", startingPom.getProject().getGAVCoordinates()));

        this.properties = Properties.newProjectProperties(startingPom, workingDir);
    }

    /**
     * Builds the effective pom out of
     * the given {@link #startingPom}.
     *
     * @return the effective pom
     * @throws PomResolverException in case of any errors during pom construction
     */
    public @NotNull EffectivePom build() throws PomResolverException {
        buildIncomplete().populateDependencies();
        return new EffectivePom(
                this.startingPom.getProject(),
                this.startingPom.getPackaging(),
                this.properties,
                this.dependencies.values()
        );
    }

    /**
     * Loads all the dependencies for the dependencies of the final pom.
     *
     * @return this builder
     */
    @NotNull EffectivePomBuilder populateDependencies() {
        this.logger.debug("Loading dependencies");
        this.dependencies.clear();

        if (this.parentEffectivePomBuilder != null)
            this.dependencies.putAll(this.parentEffectivePomBuilder.dependencies);

        populateDependenciesSingle(this.startingPom.getDependencies());
        for (Profile profile : this.activeProfiles)
            populateDependenciesSingle(profile.getDependencies());
        this.logger.debug("Loaded {} dependencies", this.dependencies.size());

        return this;
    }

    private void populateDependenciesSingle(final @NotNull Collection<RawDependency> dependencies) {
        for (final RawDependency raw : dependencies) {
            Dependency dependency = getDependency(raw);
            this.dependencies.put(dependency.getCoordinates(), dependency);
        }
    }

    /**
     * Populates some of the fields of this builder.
     * Used by the builder itself to fetch information
     * from other artifacts.
     *
     * @return this builder
     * @throws PomResolverException in case of any errors during pom construction
     */
    @NotNull EffectivePomBuilder buildIncomplete() throws PomResolverException {
        return populateActiveProfiles()
                .resolveParentEffectivePom()
                .populateProperties()
                .populateRepositories()
                .populateDependencyManagement();
    }

    /**
     * Loads all the dependencies for the dependency management of the final pom.
     * <br>
     * The loading order is the following (from lowest to highest priority):
     * <ol>
     *     <li>dependencies with scope {@link Scope#IMPORT} from the <b>parent</b> dependency management;</li>
     *     <li>dependencies with scope {@link Scope#IMPORT} from the <b>active profiles</b> of the <b>parent</b> dependency management;</li>
     *     <li><b>parent</b> dependency management;</li>
     *     <li><b>active profiles</b> of the <b>parent</b> dependency management;</li>
     *     <li>dependencies with scope {@link Scope#IMPORT} from the <b>starting pom</b> dependency management;</li>
     *     <li>dependencies with scope {@link Scope#IMPORT} from the <b>active profiles</b> of the <b>starting pom</b> dependency management;</li>
     *     <li><b>starting pom</b> dependency management;</li>
     *     <li><b>active profiles</b> of the <b>starting pom</b> dependency management.</li>
     * </ol>
     *
     * @return this builder
     * @throws PomResolverException in case of any errors during pom construction
     */
    @NotNull EffectivePomBuilder populateDependencyManagement() throws PomResolverException {
        this.logger.debug("Loading dependency management dependencies");
        this.dependencyManagement.clear();

        if (this.parentEffectivePomBuilder != null)
            this.dependencyManagement.putAll(this.parentEffectivePomBuilder.dependencyManagement);

        populateDependencyManagementSingle(this.startingPom.getDependencyManagement());
        for (Profile profile : this.activeProfiles)
            populateDependencyManagementSingle(profile.getDependencyManagement());

        this.logger.debug("Loaded {} dependencies in dependency management", this.dependencyManagement.size());
        return this;
    }

    private void populateDependencyManagementSingle(final @NotNull Collection<RawDependency> dependencyManagement) throws PomResolverException {
        final @NotNull Map<String, String> dependencies = new LinkedHashMap<>();
        for (final RawDependency raw : dependencyManagement) {
            Dependency dependency = raw.applyProperties(this.properties);
            if (dependency.getScope() == Scope.IMPORT) {
                this.logger.debug("Resolving dependency with scope IMPORT '{}'", dependency.getGAVCoordinates());
                Pom dependencyPom = this.pomResolver.resolve(dependency);
                EffectivePomBuilder dependencyPomBuilder = newBuilder(dependencyPom).buildIncomplete();
                this.dependencyManagement.putAll(dependencyPomBuilder.dependencyManagement);
            }
            dependencies.put(dependency.getCoordinates(), dependency.getVersion());
        }
        this.dependencyManagement.putAll(dependencies);
    }

    /**
     * Loads all the properties in the {@link #pomResolver}.
     *
     * @return this builder
     */
    @NotNull EffectivePomBuilder populateRepositories() {
        this.logger.debug("Parsing and storing new repositories");
        if (this.parentEffectivePomBuilder != null)
            this.pomResolver.addRepositories(this.parentEffectivePomBuilder.getRepositories());
        this.pomResolver.addRepositories(getRepositories());
        this.logger.debug("Stored repositories in POM resolver");
        return this;
    }

    private @NotNull Collection<Repository> getRepositories() {
        Map<String, Repository> repositories = new HashMap<>();
        getRepositories(this.startingPom.getRepositories()).forEach(r -> repositories.put(r.getId(), r));
        for (Profile profile : this.activeProfiles)
            getRepositories(profile.getRepositories()).forEach(r -> repositories.put(r.getId(), r));
        return repositories.values();
    }

    private @NotNull Collection<Repository> getRepositories(final @NotNull Collection<RawRepository> repositories) {
        return repositories.stream()
                .map(r -> r.applyProperties(this.properties))
                .collect(Collectors.toSet());
    }

    /**
     * Loads all the properties of the final pom.
     * <br>
     * The loading order is the following (from lowest to highest priority):
     * <ol>
     *     <li><b>parent</b> properties;</li>
     *     <li><b>active profiles</b> of the <b>parent</b> properties;</li>
     *     <li><b>starting pom</b> properties;</li>
     *     <li><b>active profiles</b> of the <b>starting pom</b> properties.</li>
     * </ol>
     *
     * @return this builder
     */
    @NotNull EffectivePomBuilder populateProperties() {
        this.logger.debug("Loading properties");
        this.properties.clear();
        if (this.parentEffectivePomBuilder != null)
            this.properties.addAll(this.parentEffectivePomBuilder.properties);
        this.properties.addAll(this.startingPom.getProperties());
        this.activeProfiles.forEach(p -> this.properties.addAll(p.getProperties()));
        this.logger.debug("Loaded {} properties", this.properties.size());
        return this;
    }

    /**
     * If the {@link #startingPom} contains a parent {@link Artifact},
     * populates the {@link #parentEffectivePomBuilder} with a new builder
     * with incomplete information (but enough to complete the building
     * of the current builder).
     *
     * @return this builder
     * @throws PomResolverException in case of any errors during pom construction
     */
    @NotNull EffectivePomBuilder resolveParentEffectivePom() throws PomResolverException {
        this.parentEffectivePomBuilder = null;
        Artifact parent = this.startingPom.getParent();
        if (parent != null) {
            this.logger.debug("Resolving parent '{}'", parent.getGAVCoordinates());
            Pom parentPom = this.pomResolver.resolve(parent);
            this.parentEffectivePomBuilder = newBuilder(parentPom)
                    .buildIncomplete()
                    .populateDependencies();
            this.logger.debug("Resolved parent '{}'", parentPom.getProject().getGAVCoordinates());
        }
        return this;
    }

    /**
     * Loads all the profiles of the given {@link #startingPom}
     * whose conditions are met under the given context.
     *
     * @return this builder
     */
    @NotNull EffectivePomBuilder populateActiveProfiles() {
        this.logger.debug("Preparing active profiles");
        this.activeProfiles.clear();
        final ActivationContext context = ActivationContext.current(this.properties);
        this.startingPom.getProfiles().stream()
                .filter(p -> p.getActivation().isEnabled(context))
                .forEach(this.activeProfiles::add);
        this.logger.debug("Verified and activated {} profiles", this.activeProfiles.size());
        return this;
    }

    /**
     * Converts the given {@link RawDependency} to a {@link Dependency},
     * with applied {@link #properties} and version from {@link #dependencyManagement}
     * if missing.
     *
     * @param dependency the raw dependency
     * @return the dependency
     */
    @NotNull Dependency getDependency(final @NotNull RawDependency dependency) {
        String version = dependency.getVersion();
        if (version == null)
            version = this.dependencyManagement.get(this.properties.apply(dependency.getCoordinates()));
        RawDependency copy = RawDependency.builder()
                .groupId(dependency.getGroupId())
                .artifactId(dependency.getArtifactId())
                .classifier(dependency.getClassifier())
                .version(version)
                .scope(dependency.getScope())
                .optional(dependency.getOptional())
                .build();
        copy.getExclusionsManager().addAll(dependency.getExclusionsManager());
        return copy.applyProperties(this.properties);
    }

    private @NotNull EffectivePomBuilder newBuilder(final @NotNull Pom pom) {
        return new EffectivePomBuilder(pom, this.pomResolver, this.workingDir);
    }

}
