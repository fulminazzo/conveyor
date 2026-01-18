package it.fulminazzo.conveyor.model.pom.metadata;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

@Value
@Builder
public class Reporting {

    @Builder.Default
    @NotNull String excludeDefaults = "false";

    @Builder.Default
    @NotNull String outputDirectory = "${project.build.directory}/site";

    @Builder.Default
    @NotNull List<Plugin> plugins = new LinkedList<>();

}
