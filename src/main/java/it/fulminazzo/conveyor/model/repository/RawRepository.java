package it.fulminazzo.conveyor.model.repository;

import it.fulminazzo.conveyor.model.Properties;
import it.fulminazzo.conveyor.model.RawObject;
import it.fulminazzo.conveyor.model.repository.update.UpdatePolicy;
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
public class RawRepository implements RepositoryLike, RawObject<Repository> {

    @NotNull String id;

    @NotNull String url;

    @Nullable String name;

    @Builder.Default
    @NotNull Policy releases = Policy.builder().enabled(Boolean.TRUE.toString()).build();

    @Builder.Default
    @NotNull Policy snapshots = Policy.builder().build();

    @Builder.Default
    @NotNull String uniqueVersion = "true";

    @Builder.Default
    @NotNull String layout = "default";

    @Override
    public @NotNull Repository applyProperties(final @NotNull Properties properties) {
        String name = getName();
        return Repository.builder()
                .id(properties.apply(getId()))
                .url(properties.apply(getUrl()))
                .name(name == null ? null : properties.apply(name))
                .releases(getReleases().applyProperties(properties))
                .snapshots(getSnapshots().applyProperties(properties))
                .build();
    }

    /**
     * Represents a raw {@link Repository.Policy},
     * are initialized in their <b>XML</b> form.
     */
    @Value
    @Builder
    public static class Policy implements RawObject<Repository.Policy> {
        @Builder.Default
        String enabled = String.valueOf(Boolean.FALSE);
        @Builder.Default
        @NotNull String updatePolicy = "daily";
        @Builder.Default
        @NotNull String checksumPolicy = ChecksumPolicy.WARN.value();

        @Override
        public @NotNull Repository.Policy applyProperties(final @NotNull Properties properties) {
            return Repository.Policy.builder()
                    .enabled(Boolean.parseBoolean(properties.apply(getEnabled())))
                    .updatePolicy(UpdatePolicy.of(properties.apply(getUpdatePolicy())))
                    .checksumPolicy(ChecksumPolicy.of(properties.apply(getChecksumPolicy())))
                    .build();
        }

    }

}
