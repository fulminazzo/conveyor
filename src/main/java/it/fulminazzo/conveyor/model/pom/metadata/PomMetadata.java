package it.fulminazzo.conveyor.model.pom.metadata;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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

    @Builder.Default
    @NotNull Set<Developer> developers = new HashSet<>();

    @Builder.Default
    @NotNull Set<Contributor> contributors = new HashSet<>();

    @Builder.Default
    @NotNull List<MailingList> mailingLists = new LinkedList<>();

    @Builder.Default
    @NotNull Prerequisites prerequisites = new Prerequisites();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prerequisites {

        @Builder.Default
        @NotNull String maven = "2.0";

    }

}
