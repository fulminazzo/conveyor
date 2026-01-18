package it.fulminazzo.conveyor.model.pom.metadata;

import it.fulminazzo.conveyor.model.repository.RawRepository;
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

    @Builder.Default
    @NotNull List<String> modules = new LinkedList<>();

    @Nullable IssueManagement issueManagement;

    @Nullable CiManagement ciManagement;

    @Builder.Default
    @NotNull Set<RawRepository> pluginRepositories = new HashSet<>();

    @Builder.Default
    @NotNull Build build = Build.builder().build();

    @Builder.Default
    @NotNull Reporting reporting = Reporting.builder().build();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prerequisites {

        @Builder.Default
        @NotNull String maven = "2.0";

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IssueManagement {

        @Nullable String system;

        @Nullable String url;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CiManagement {

        @Nullable String system;

        @Nullable String url;

        @Builder.Default
        @NotNull List<Notifier> notifiers = new LinkedList<>();

    }

}
