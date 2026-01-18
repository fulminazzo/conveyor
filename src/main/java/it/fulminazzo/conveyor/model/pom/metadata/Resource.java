package it.fulminazzo.conveyor.model.pom.metadata;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

@Value
@Builder
public class Resource {

    @Nullable String targetPath;

    @Builder.Default
    @NotNull String filtering = "false";

    @Nullable String directory;

    @Builder.Default
    @NotNull List<String> include = new LinkedList<>();

    @Builder.Default
    @NotNull List<String> exclude = new LinkedList<>();

}
