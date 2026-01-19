package it.fulminazzo.conveyor.model.tree;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.dependency.RawDependency;
import it.fulminazzo.conveyor.model.pom.Pom;
import it.fulminazzo.conveyor.model.pom.resolver.RepositoryPomResolver;
import it.fulminazzo.conveyor.model.repository.Repository;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * main:1.0 -> {dep1:1.0, dep2:1.0}
 * dep1:1.0 -> {dep2:2.0, dep3:2.0}
 * dep2:1.0 -> {dep4:3.0}
 * dep3:2.0 -> {}
 * dep4:3.0 -> {}
 */
class MockResolver implements RepositoryPomResolver {
    private static final @NotNull Collection<Pom> poms = new LinkedList<>();

    static {
        poms.add(newPom("main", "1.0",
                newDependency("dep1", "1.0"),
                newDependency("dep2", "1.0")
        ));
        poms.add(newPom("dep1", "1.0",
                newDependency("dep2", "2.0"),
                newDependency("dep3", "2.0")
        ));
        poms.add(newPom("dep2", "1.0", newDependency("dep4", "3.0")));
        poms.add(newPom("dep3", "2.0"));
        poms.add(newPom("dep4", "3.0"));

        poms.add(newPom("problematic1", "1.0", newDependency("problematic2", "1.0")));
        poms.add(newPom("problematic2", "1.0", newDependency("problematic1", "1.0")));
    }

    @Override
    public @NotNull Pom resolve(@NotNull Artifact artifact) {
        artifact = Artifact.builder()
                .groupId(artifact.getGroupId())
                .artifactId(artifact.getArtifactId())
                .version(artifact.getVersion())
                .build();
        for (Pom pom : poms)
            if (pom.getProject().equals(artifact))
                return pom;
        throw new IllegalArgumentException("Could not get pom of artifact: " + artifact);
    }

    @Override
    public @NotNull RepositoryPomResolver addRepositories(final @NotNull Collection<Repository> repositories) {
        return this;
    }

    private static @NotNull Pom newPom(final @NotNull String artifactId,
                                       final @NotNull String version,
                                       final RawDependency @NotNull ... dependencies) {
        return Pom.builder()
                .project(newArtifact(artifactId, version))
                .dependencies(Arrays.asList(dependencies))
                .build();
    }

    private static @NotNull RawDependency newDependency(final @NotNull String artifactId,
                                                        final @NotNull String version) {
        return RawDependency.builder()
                .groupId("it.fulminazzo")
                .artifactId(artifactId)
                .version(version)
                .build();
    }

    private static @NotNull Artifact newArtifact(final @NotNull String artifactId,
                                                 final @NotNull String version) {
        return new Artifact("it.fulminazzo", artifactId, version);
    }

}
