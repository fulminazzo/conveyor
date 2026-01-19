package it.fulminazzo.conveyor.model.profile;

import it.fulminazzo.conveyor.model.MavenModel;
import it.fulminazzo.conveyor.model.profile.activation.Activation;
import it.fulminazzo.conveyor.model.profile.metadata.ProfileMetadata;
import it.fulminazzo.conveyor.xml.XmlParser;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Delegate;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Maven profile.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public final class Profile extends MavenModel {

    private final @NotNull String id;

    @Builder.Default
    private final @NotNull Activation activation = Activation.alwaysFalse();

    @Delegate
    @Builder.Default
    private final @NotNull ProfileMetadata metadata = ProfileMetadata.builder().build();

    /**
     * Instantiates a new builder to create a {@link Profile} object.
     *
     * @return the builder
     */
    public static @NotNull ProfileBuilder<?, ?> builder() {
        return new ProfileBuilderImpl();
    }

    /**
     * Instantiates a new builder to create a {@link Profile} object.
     *
     * @param parser the XML parser
     * @return the builder
     */
    public static @NotNull XmlProfileBuilder builder(final @NotNull XmlParser parser) {
        return new XmlProfileBuilder(parser);
    }

}
