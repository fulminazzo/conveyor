package it.fulminazzo.conveyor.model.pom.metadata;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Value
@Builder
public class Build {

    @Builder.Default
    @NotNull String sourceDirectory = "src/main/java";

    @Builder.Default
    @NotNull String scriptSourceDirectory = "src/main/scripts";

    @Builder.Default
    @NotNull String testSourceDirectory = "src/test/java";

    @Builder.Default
    @NotNull String outputDirectory = "target/classes";

    @Builder.Default
    @NotNull String testOutputDirectory = "target/test-classes";

    @Builder.Default
    @NotNull List<Artifact> extensions = new LinkedList<>();

    @Nullable String defaultGoal;

    @Builder.Default
    @NotNull List<Resource> resources = new LinkedList<>();

    @Builder.Default
    @NotNull List<Resource> testResources = new LinkedList<>();

    @Builder.Default
    @NotNull String directory = "target";

    @Builder.Default
    @NotNull String finalName = "${artifactId}-${version}";

    @Builder.Default
    @NotNull List<String> filters = new LinkedList<>();

    @Builder.Default
    @NotNull Set<Plugin> pluginManagement = new HashSet<>();

    @Builder.Default
    @NotNull List<Plugin> plugins = new LinkedList<>();

}
