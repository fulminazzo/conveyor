package it.fulminazzo.conveyor.model.profile;

import it.fulminazzo.conveyor.model.BuilderException;
import it.fulminazzo.conveyor.model.MavenModelBuilder;
import it.fulminazzo.conveyor.model.profile.activation.Activation;
import it.fulminazzo.conveyor.xml.XmlParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
    public @NotNull Profile build() throws BuilderException {
        parseDocument();
        return buildObject("profile", () -> new Profile(
                Objects.requireNonNull(this.id, "id is marked non-null but is null"),
                this.activation == null ? Activation.alwaysFalse() : this.activation,
                this.properties,
                this.repositories.values(),
                this.dependencyManagement.values(),
                this.dependencies.values()
        ));
    }

    @Override
    protected void parseDocument() throws BuilderException {
        onChildElements(t -> {
            switch (t) {
                case "id" -> parseId();
                case "activation" -> parseActivation();
                case "properties" -> parseProperties();
                case "repositories" -> parseRepositories();
                case "dependencyManagement" -> parseDependencyManagement();
                case "dependencies" -> parseDependencies();
            }
        });
    }

    /**
     * Handles the <b>&lt;id&gt;</b> tag in the document.
     *
     * @throws BuilderException in case of reading or parsing errors
     */
    void parseId() throws BuilderException {
        this.id = getCurrentTextContent();
    }

    /**
     * Handles the <b>&lt;activation&gt;</b> tag in the document.
     *
     * @throws BuilderException in case of any errors
     */
    void parseActivation() throws BuilderException {
        this.activation = Activation.builder(getParser()).build();
    }

}
