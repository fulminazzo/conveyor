package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.Properties;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.dependency.Dependency;
import it.fulminazzo.conveyor.model.dependency.RawDependency;
import it.fulminazzo.conveyor.model.dependency.Scope;
import it.fulminazzo.conveyor.model.profile.Profile;
import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Responsible for creating a {@link EffectivePom} object.
 */
@RequiredArgsConstructor
final class EffectivePomBuilder {
    private final @NotNull Pom startingPom;
    private final @NotNull PomResolver pomResolver;
    private final @NotNull ActivationContext context;

    private final @NotNull Set<Profile> activeProfiles = new HashSet<>();
    private final @NotNull Properties properties = new Properties();
    private final @NotNull Map<String, String> dependencyManagement = new HashMap<>();

    private @Nullable EffectivePomBuilder parentEffectivePomBuilder;

    /**
     * Populates some of the fields of this builder.
     * Used by the builder itself to fetch information
     * from other artifacts.
     *
     * @return this builder
     */
    @NotNull EffectivePomBuilder buildIncomplete() {
        return populateActiveProfiles()
                .resolveParentEffectivePom()
                .populateProperties()
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
     */
    @NotNull EffectivePomBuilder populateDependencyManagement() {
        this.dependencyManagement.clear();

        if (this.parentEffectivePomBuilder != null)
            this.dependencyManagement.putAll(this.parentEffectivePomBuilder.dependencyManagement);

        populateDependencyManagementSingle(this.startingPom.getDependencyManagement());
        for (Profile profile : this.activeProfiles)
            populateDependencyManagementSingle(profile.getDependencyManagement());

        return this;
    }

    private void populateDependencyManagementSingle(final @NotNull Set<RawDependency> dependencyManagement) {
        final @NotNull Map<String, String> dependencies = new LinkedHashMap<>();
        for (final RawDependency raw : dependencyManagement) {
            Dependency dependency = raw.applyProperties(this.properties);
            if (dependency.getScope() == Scope.IMPORT) {
                Pom dependencyPom = this.pomResolver.resolve(dependency);
                EffectivePomBuilder dependencyPomBuilder = newBuilder(dependencyPom).buildIncomplete();
                this.dependencyManagement.putAll(dependencyPomBuilder.dependencyManagement);
            }
            dependencies.put(dependency.getCoordinates(), dependency.getVersion());
        }
        this.dependencyManagement.putAll(dependencies);
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
        this.properties.clear();
        if (this.parentEffectivePomBuilder != null)
            this.properties.putAll(this.parentEffectivePomBuilder.properties);
        this.properties.putAll(this.startingPom.getProperties());
        this.activeProfiles.forEach(p ->
                this.properties.putAll(p.getProperties())
        );
        return this;
    }

    /**
     * If the {@link #startingPom} contains a parent {@link Artifact},
     * populates the {@link #parentEffectivePomBuilder} with a new builder
     * with incomplete information (but enough to complete the building
     * of the current builder).
     *
     * @return this builder
     */
    @NotNull EffectivePomBuilder resolveParentEffectivePom() {
        this.parentEffectivePomBuilder = null;
        Artifact parent = this.startingPom.getParent();
        if (parent != null) {
            Pom parentPom = this.pomResolver.resolve(parent);
            this.parentEffectivePomBuilder = newBuilder(parentPom).buildIncomplete();
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
        this.activeProfiles.clear();
        this.startingPom.getProfiles().stream()
                .filter(p -> p.getActivation().isEnabled(this.context))
                .forEach(this.activeProfiles::add);
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
            version = this.dependencyManagement.get(dependency.getCoordinates());
        RawDependency copy = RawDependency.builder()
                .groupId(dependency.getGroupId())
                .artifactId(dependency.getArtifactId())
                .classifier(dependency.getClassifier())
                .version(version)
                .scope(dependency.getScope())
                .optional(dependency.getOptional())
                .build();
        copy.getExclusions().addAll(dependency.getExclusions());
        return copy.applyProperties(this.properties);
    }

    private @NotNull EffectivePomBuilder newBuilder(final @NotNull Pom pom) {
        return new EffectivePomBuilder(pom, this.pomResolver, this.context);
    }

}
