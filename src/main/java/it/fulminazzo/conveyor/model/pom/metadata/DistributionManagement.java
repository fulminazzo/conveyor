package it.fulminazzo.conveyor.model.pom.metadata;

import it.fulminazzo.conveyor.model.repository.RawRepository;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Value
@Builder
public class DistributionManagement {

    @Nullable RawRepository repository;

    @Builder.Default
    @Nullable RawRepository snapshotRepository = this.repository;

    @Nullable Site site;

    @Nullable String downloadUrl;

    @Nullable Relocation relocation;

    @Builder.Default
    @NotNull String status = "none";

    @Value
    @Builder
    public static class Site {

        @Nullable String id;

        @Nullable String name;

        @Nullable String url;

    }

    @Value
    @Builder
    public static class Relocation {

        @Nullable String groupId;

        @Nullable String artifactId;

        @Nullable String version;

        @Nullable String message;

    }

}
