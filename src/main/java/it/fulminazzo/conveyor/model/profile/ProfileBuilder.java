package it.fulminazzo.conveyor.model.profile;

import it.fulminazzo.conveyor.model.BuilderException;
import it.fulminazzo.conveyor.model.MavenModelBuilder;
import it.fulminazzo.conveyor.model.profile.activation.Activation;
import it.fulminazzo.conveyor.xml.XmlParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A builder for creating {@link Profile} objects from <b>XML</b>.
 */
public final class ProfileBuilder extends MavenModelBuilder<Profile> {
    private @Nullable String id;
    private @Nullable Activation activation;

    /**
     * Instantiates a new Profile builder.
     *
     * @param parser the XML parser
     */
    ProfileBuilder(final @NotNull XmlParser parser) {
        super(parser);
    }

    @Override
    public Profile build() throws BuilderException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void parseDocument() throws BuilderException {
        throw new UnsupportedOperationException();
    }

}
