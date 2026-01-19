package it.fulminazzo.conveyor.model.metadata;

import it.fulminazzo.conveyor.model.dependency.RawDependency;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

@Value
@Builder
public class Plugin {

    @Builder.Default
    @NotNull String groupId = "org.apache.maven.plugins";

    @Nullable String artifactId;

    @Nullable String version;

    @Builder.Default
    @Nullable String extensions = "false";

    @Builder.Default
    @NotNull List<Execution> executions = new LinkedList<>();

    @Builder.Default
    @NotNull List<RawDependency> dependencies = new LinkedList<>();

    @Builder.Default
    @Nullable String inherited = "true";

    @Value
    @Builder
    public static class Execution {

        @Builder.Default
        @NotNull String id = "default";

        @Nullable String phase;

        @Builder.Default
        @NotNull List<String> goals = new LinkedList<>();

        @Builder.Default
        @Nullable String inherited = "true";

    }

}
