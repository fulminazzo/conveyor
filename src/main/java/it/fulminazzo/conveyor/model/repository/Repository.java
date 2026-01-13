package it.fulminazzo.conveyor.model.repository;

import it.fulminazzo.conveyor.model.repository.update.UpdatePolicy;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a Maven repository.
 */
@Value
@Builder
public class Repository implements RepositoryLike {
    @NotNull String id;
    @NotNull String url;
    @Nullable String name;
    @Builder.Default
    @NotNull Policy releases = Policy.builder().enabled(true).build();
    @Builder.Default
    @NotNull Policy snapshots = Policy.builder().build();

    /**
     * Represents the repository policy.
     */
    @Value
    @Builder
    public static class Policy {
        @Builder.Default
        boolean enabled = false;
        @Builder.Default
        @NotNull UpdatePolicy updatePolicy = UpdatePolicy.of("daily");
        @Builder.Default
        @NotNull ChecksumPolicy checksumPolicy = ChecksumPolicy.WARN;

    }

}
