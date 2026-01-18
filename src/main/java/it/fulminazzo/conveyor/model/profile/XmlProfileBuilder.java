package it.fulminazzo.conveyor.model.profile;

import it.fulminazzo.conveyor.model.BuilderException;
import it.fulminazzo.conveyor.model.MavenModelBuilder;
import it.fulminazzo.conveyor.model.profile.activation.Activation;
import it.fulminazzo.conveyor.xml.XmlParser;
import org.jetbrains.annotations.NotNull;

/**
 * A builder for creating {@link Profile} objects from <b>XML</b>.
 */
public final class XmlProfileBuilder extends MavenModelBuilder<Profile> {
    private final @NotNull Profile.ProfileBuilder<?, ?> builder = Profile.builder();

    /**
     * Instantiates a new Profile builder.
     *
     * @param parser the XML parser
     */
    XmlProfileBuilder(final @NotNull XmlParser parser) {
        super(parser);
    }

    @Override
    public @NotNull Profile build() throws BuilderException {
        parseDocument();
        return buildObject("profile", this.builder::build);
    }

    @Override
    protected void parseDocument() throws BuilderException {
        onChildElements(t -> {
            switch (t) {
                case "id" -> this.builder.id(getCurrentTextContent());
                case "activation" -> parseActivation();
                case "properties" -> this.builder.properties(parseProperties());
                case "repositories" -> this.builder.repositories(parseRepositories());
                case "dependencyManagement" -> this.builder.dependencyManagement(parseDependencyManagement());
                case "dependencies" -> this.builder.dependencies(parseDependencies());
            }
        });
    }

    /**
     * Handles the <b>&lt;activation&gt;</b> tag in the document.
     *
     * @throws BuilderException in case of any errors
     */
    void parseActivation() throws BuilderException {
        this.builder.activation(Activation.builder(getParser()).build());
    }

}
