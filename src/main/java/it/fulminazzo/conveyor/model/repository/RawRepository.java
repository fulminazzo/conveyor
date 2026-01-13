package it.fulminazzo.conveyor.model.repository;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a raw {@link Repository},
 * are initialized in their <b>XML</b> form.
 */
@Value
@Builder
public class RawRepository implements RepositoryLike {
    @NotNull String id;
    @NotNull String url;
    @Nullable String name;
    @Builder.Default
    @NotNull Policy releases = Policy.builder().enabled(Boolean.TRUE.toString()).build();
    @Builder.Default
    @NotNull Policy snapshots = Policy.builder().build();

    /**
     * Represents a raw {@link Repository.Policy},
     * are initialized in their <b>XML</b> form.
     */
    @Value
    @Builder
    public static class Policy {
        @Builder.Default
        String enabled = String.valueOf(Boolean.FALSE);
        @Builder.Default
        @NotNull String updatePolicy = "daily";
        @Builder.Default
        @NotNull String checksumPolicy = ChecksumPolicy.WARN.value();

    }

}
