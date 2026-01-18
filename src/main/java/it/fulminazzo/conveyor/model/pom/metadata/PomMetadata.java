package it.fulminazzo.conveyor.model.pom.metadata;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the {@link it.fulminazzo.conveyor.model.pom.Pom} metadata.
 */
@Value
@Builder
public class PomMetadata {
    @Nullable String modelVersion;

    @Nullable String name;
    @Nullable String description;
    @Nullable String url;
    @Nullable String inceptionYear;

    @Nullable Organization organization;

    @Builder.Default
    @NotNull Set<License> licenses = new HashSet<>();


}
