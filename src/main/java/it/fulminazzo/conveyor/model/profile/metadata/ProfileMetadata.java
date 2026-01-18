package it.fulminazzo.conveyor.model.profile.metadata;

import it.fulminazzo.conveyor.model.metadata.MavenModelMetadata;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the {@link it.fulminazzo.conveyor.model.profile.Profile} metadata.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@SuperBuilder
public final class ProfileMetadata extends MavenModelMetadata {

    @Builder.Default
    @NotNull BuildBase build = BuildBase.builder().build();

}
