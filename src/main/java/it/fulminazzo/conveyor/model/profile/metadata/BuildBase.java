package it.fulminazzo.conveyor.model.profile.metadata;

import it.fulminazzo.conveyor.model.pom.metadata.Plugin;
import it.fulminazzo.conveyor.model.pom.metadata.Resource;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@SuperBuilder
public class BuildBase {

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
