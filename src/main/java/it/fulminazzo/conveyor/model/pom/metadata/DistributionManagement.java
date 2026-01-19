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

    @Nullable RawRepository snapshotRepository;

    @Nullable Site site;

    @Nullable String downloadUrl;

    @Nullable Relocation relocation;

    @Builder.Default
    @NotNull String status = "none";

    public DistributionManagement(final @Nullable RawRepository repository,
                                  final @Nullable RawRepository snapshotRepository,
                                  final @Nullable Site site,
                                  final @Nullable String downloadUrl,
                                  final @Nullable Relocation relocation,
                                  final @NotNull String status) {
        this.repository = repository;
        this.snapshotRepository = snapshotRepository == null ? repository : snapshotRepository;
        this.site = site;
        this.downloadUrl = downloadUrl;
        this.relocation = relocation;
        this.status = status;
    }

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
