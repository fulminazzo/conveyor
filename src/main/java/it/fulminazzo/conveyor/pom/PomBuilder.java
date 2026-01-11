package it.fulminazzo.conveyor.pom;

import it.fulminazzo.conveyor.pom.artifact.Artifact;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

/**
 * Responsible for creating a {@link Pom} object.
 */
final class PomBuilder extends MavenModelBuilder {
    private @Nullable String packaging;
    private @Nullable Artifact parent;

    private PomBuilder(final @NotNull XMLStreamReader reader) {
        super(reader);
    }

    /**
     * Parses the given document trying to populate all the above fields.
     *
     * @return the project artifact
     * @throws ParserException in case of reading or parsing errors
     */
    @NotNull Artifact parseDocument() throws ParserException {
        Artifact.ArtifactBuilder<?, ?> builder = Artifact.builder();
        parseGeneric(t -> {
            switch (t) {
                case "groupId" -> builder.groupId(getElementText());
                case "artifactId" -> builder.artifactId(getElementText());
                case "version" -> builder.version(getElementText());
                case "classifier" -> builder.classifier(getElementText());
                case "packaging" -> this.packaging = getElementText();
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
     * @throws ParserException in case of reading or parsing errors
     */
    @NotNull Artifact parseParent() throws ParserException {
        Artifact.ArtifactBuilder<?, ?> builder = Artifact.builder();
        parseGeneric(t -> {
            switch (t) {
                case "groupId" -> builder.groupId(getElementText());
                case "artifactId" -> builder.artifactId(getElementText());
                case "version" -> builder.version(getElementText());
                case "classifier" -> builder.classifier(getElementText());
            }
        });
        return buildObject("parent", builder::build);
    }

    /**
     * Instantiates a new Pom builder.
     *
     * @param inputStream the input stream
     * @return the pom builder
     * @throws ParserException in case of reading or parsing errors
     */
    static @NotNull PomBuilder of(final @NotNull InputStream inputStream) throws ParserException {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            return new PomBuilder(factory.createXMLStreamReader(inputStream));
        } catch (XMLStreamException e) {
            throw ParserException.of("Error while creating PomBuilder", e);
        }
    }

}
