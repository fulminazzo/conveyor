package it.fulminazzo.conveyor.model.profile.metadata;

import it.fulminazzo.conveyor.model.metadata.MavenModelMetadata;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

/**
 * Represents the {@link it.fulminazzo.conveyor.model.profile.Profile} metadata.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@SuperBuilder
public final class ProfileMetadata extends MavenModelMetadata {

}
