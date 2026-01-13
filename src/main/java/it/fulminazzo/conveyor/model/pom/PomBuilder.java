package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.BuilderException;
import it.fulminazzo.conveyor.model.MavenModelBuilder;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.xml.XmlParser;
import it.fulminazzo.conveyor.xml.XmlParserException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

/**
 * Responsible for creating a {@link Pom} object.
 */
final class PomBuilder extends MavenModelBuilder<Object> {
    private @Nullable String packaging;
    private @Nullable Artifact parent;
    
    public PomBuilder(final @NotNull XmlParser parser) {
        super(parser);
    }

    @Override
    public Object build() {
        throw new UnsupportedOperationException();
    }

    /**
     * Parses the given document trying to populate all the above fields.
     *
     * @return the project artifact
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull Artifact parseDocument() throws BuilderException {
        Artifact.ArtifactBuilder<?, ?> builder = Artifact.builder();
        onChildElements(t -> {
            switch (t) {
                case "groupId" -> builder.groupId(getCurrentTextContent());
                case "artifactId" -> builder.artifactId(getCurrentTextContent());
                case "version" -> builder.version(getCurrentTextContent());
                case "classifier" -> builder.classifier(getCurrentTextContent());
                case "packaging" -> this.packaging = getCurrentTextContent();
                case "parent" -> this.parent = parseParent();
                case "properties" -> parseProperties();
                case "repositories" -> parseRepositories();
                case "dependencyManagement" -> parseDependencyManagement();
                case "dependencies" -> parseDependencies();
            }
        });
        return buildObject("project", builder::build);
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

    /**
     * Instantiates a new Pom builder.
     *
     * @param inputStream the input stream
     * @return the pom builder
     * @throws BuilderException in case of reading or parsing errors
     */
    static @NotNull PomBuilder of(final @NotNull InputStream inputStream) throws BuilderException {
        try {
            XmlParser parser = XmlParser.newParser(inputStream);
            return new PomBuilder(parser);
        } catch (XmlParserException e) {
            throw new BuilderException(e);
        }
    }

}
