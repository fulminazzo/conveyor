package it.fulminazzo.conveyor.model.pom.metadata;

import it.fulminazzo.conveyor.model.metadata.MavenModelMetadata;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Represents the {@link it.fulminazzo.conveyor.model.pom.Pom} metadata.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@SuperBuilder
public final class PomMetadata extends MavenModelMetadata {
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

    @Nullable IssueManagement issueManagement;

    @Nullable CiManagement ciManagement;

    @Builder.Default
    @NotNull Build build = Build.builder().build();

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
