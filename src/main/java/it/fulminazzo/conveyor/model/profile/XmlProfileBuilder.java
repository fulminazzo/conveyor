package it.fulminazzo.conveyor.model.profile;

import it.fulminazzo.conveyor.model.BuilderException;
import it.fulminazzo.conveyor.model.MavenModelBuilder;
import it.fulminazzo.conveyor.model.profile.activation.Activation;
import it.fulminazzo.conveyor.model.profile.metadata.BuildBase;
import it.fulminazzo.conveyor.model.profile.metadata.ProfileMetadata;
import it.fulminazzo.conveyor.xml.XmlParser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * A builder for creating {@link Profile} objects from <b>XML</b>.
 */
public final class XmlProfileBuilder extends MavenModelBuilder<Profile> {
    private final @NotNull Profile.ProfileBuilder<?, ?> builder = Profile.builder();
    private final @NotNull ProfileMetadata.ProfileMetadataBuilder<?, ?> profileMetadataBuilder = ProfileMetadata.builder();

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
        return buildObject("profile", () -> this.builder
                .metadata(this.profileMetadataBuilder.build())
                .build());
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
                // METADATA
                case "modules" -> this.profileMetadataBuilder.modules(List.copyOf(parseModules()));
                case "distributionManagement" -> this.profileMetadataBuilder.distributionManagement(parseDistributionManagement());
                case "pluginRepositories" -> this.profileMetadataBuilder.pluginRepositories(parsePluginRepositories());
                case "reporting" -> this.profileMetadataBuilder.reporting(parseReporting());
                case "build" -> this.profileMetadataBuilder.build(parseBuild());
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

    /*
     * METADATA
     */

    /**
     * Handles the <b>&lt;build&gt;</b> tag in the document.
     *
     * @return the build
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull BuildBase parseBuild() throws BuilderException {
        BuildBase.BuildBaseBuilder<?, ?> builder = BuildBase.builder();
        onChildElements(t -> {
            switch (t) {
                case "defaultGoal" -> builder.defaultGoal(getCurrentTextContent());
                case "resources" -> builder.resources(List.copyOf(parseResources()));
                case "testResources" -> builder.testResources(List.copyOf(parseTestResources()));
                case "directory" -> builder.directory(getCurrentTextContent());
                case "finalName" -> builder.finalName(getCurrentTextContent());
                case "filters" -> builder.filters(parseStringList("filter"));
                case "pluginManagement" -> builder.pluginManagement(Set.copyOf(parsePluginManagement()));
                case "plugins" -> builder.plugins(List.copyOf(parsePlugins()));
            }
        });
        return buildObject("build", builder::build);
    }

}
