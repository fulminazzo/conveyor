package it.fulminazzo.conveyor.pom.repository;

import it.fulminazzo.conveyor.pom.repository.update.UpdatePolicy;
import lombok.*;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Maven repository.
 */
@Value
@Builder
public class Repository {
    @NotNull String id;
    @Builder.Default
    @NotNull String name = "";
    @NotNull String url;
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
