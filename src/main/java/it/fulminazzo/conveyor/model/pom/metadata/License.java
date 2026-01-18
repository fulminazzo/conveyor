package it.fulminazzo.conveyor.model.pom.metadata;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value
@Builder
public class License {

    @NotNull String name;

    @NotNull String url;

    @Nullable String distribution;

    @Nullable String comments;

}
