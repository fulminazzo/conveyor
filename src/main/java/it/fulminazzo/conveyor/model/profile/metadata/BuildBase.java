package it.fulminazzo.conveyor.model.profile.metadata;

import it.fulminazzo.conveyor.model.metadata.Plugin;
import it.fulminazzo.conveyor.model.metadata.Resource;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@SuperBuilder
public class BuildBase {

    @Nullable String defaultGoal;

    @Builder.Default
    @NotNull List<Resource> resources = List.of(Resource.builder().directory("src/main/resources").build());

    @Builder.Default
    @NotNull List<Resource> testResources = List.of(Resource.builder().directory("src/test/resources").build());

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
