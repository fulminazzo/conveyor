package it.fulminazzo.conveyor.model.profile.activation;

import it.fulminazzo.conveyor.model.BuilderException;
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

    /**
     * Attempts to retrieve a {@link BooleanActivation} from the parser.
     *
     * @return the activation
     * @throws BuilderException in case of any errors
     */
    @NotNull BooleanActivation parseActiveByDefault() throws BuilderException {
        return new BooleanActivation(Boolean.parseBoolean(getCurrentTextContent()));
    }

    /**
     * Attempts to retrieve a {@link JdkActivation} from the parser.
     *
     * @return the jdk activation
     * @throws BuilderException in case of any errors
     */
    @NotNull JdkActivation parseJdk() throws BuilderException {
        return new JdkActivation(getCurrentTextContent());
    }

}
