package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.BuilderException;
import it.fulminazzo.conveyor.model.MavenModel;
import it.fulminazzo.conveyor.model.MavenModelBuilder;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.xml.XmlParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Responsible for creating a {@link Pom} object.
 */
public final class PomBuilder extends MavenModelBuilder<MavenModel> {
    private final @NotNull Artifact.ArtifactBuilder<?, ?> projectBuilder = Artifact.builder();
    private @Nullable String packaging;
    private @Nullable Artifact parent;

    /**
     * Instantiates a new Pom builder.
     *
     * @param parser the XML parser
     */
    PomBuilder(final @NotNull XmlParser parser) {
        super(parser);
    }

    @Override
    public MavenModel build() {
        throw new UnsupportedOperationException();
    }

    /**
     * Parses the given document trying to populate all the above fields.
     *
     * @throws BuilderException in case of any errors
     */
    protected void parseDocument() throws BuilderException {
        onChildElements(t -> {
            switch (t) {
                case "groupId" -> this.projectBuilder.groupId(getCurrentTextContent());
                case "artifactId" -> this.projectBuilder.artifactId(getCurrentTextContent());
                case "version" -> this.projectBuilder.version(getCurrentTextContent());
                case "classifier" -> this.projectBuilder.classifier(getCurrentTextContent());
                case "packaging" -> this.packaging = getCurrentTextContent();
                case "parent" -> this.parent = parseParent();
                case "properties" -> parseProperties();
                case "repositories" -> parseRepositories();
                case "dependencyManagement" -> parseDependencyManagement();
                case "dependencies" -> parseDependencies();
            }
        });
    }

    /**
     * Handles the <b>&lt;parent&gt;</b> tag in the document.
     *
     * @return the parent artifact
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull Artifact parseParent() throws BuilderException {
        Artifact.ArtifactBuilder<?, ?> builder = Artifact.builder();
        onChildElements(t -> {
            switch (t) {
                case "groupId" -> builder.groupId(getCurrentTextContent());
                case "artifactId" -> builder.artifactId(getCurrentTextContent());
                case "version" -> builder.version(getCurrentTextContent());
                case "classifier" -> builder.classifier(getCurrentTextContent());
            }
        });
        return buildObject("parent", builder::build);
    }

}
