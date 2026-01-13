package it.fulminazzo.conveyor.model.profile.activation;

import it.fulminazzo.conveyor.model.XmlObjectBuilder;
import it.fulminazzo.conveyor.xml.XmlParser;
import org.jetbrains.annotations.NotNull;

/**
 * A builder for creating {@link Activation} objects from <b>XML</b>.
 */
public final class ActivationBuilder extends XmlObjectBuilder<Activation> {

    /**
     * Instantiates a new Activation builder.
     *
     * @param parser the XML parser
     */
    ActivationBuilder(final @NotNull XmlParser parser) {
        super(parser);
    }

    @Override
    public Activation build() {
        throw new UnsupportedOperationException();
    }

}
