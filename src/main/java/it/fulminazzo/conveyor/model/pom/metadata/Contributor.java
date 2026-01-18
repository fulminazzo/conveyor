package it.fulminazzo.conveyor.model.pom.metadata;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Value
@Builder
public class Contributor {

    @Nullable String name;

    @Nullable String email;

    @Nullable String url;

    @Nullable String organization;

    @Nullable String organizationUrl;

    @Builder.Default
    @NotNull List<String> roles = new LinkedList<>();

    @Nullable String timezone;

    @Builder.Default
    @NotNull Map<String, String> properties = new HashMap<>();

}
