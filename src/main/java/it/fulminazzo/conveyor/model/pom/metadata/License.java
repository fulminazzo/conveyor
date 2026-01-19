package it.fulminazzo.conveyor.model.pom.metadata;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value
@Builder
public class License {

    @Nullable String name;

    @Nullable String url;

    @Nullable String distribution;

    @Nullable String comments;

}
