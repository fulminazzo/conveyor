package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.BuilderException;
import it.fulminazzo.conveyor.model.MavenModel;
import it.fulminazzo.conveyor.model.MavenModelBuilder;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.profile.Profile;
import it.fulminazzo.conveyor.xml.XmlParser;
import it.fulminazzo.conveyor.xml.XmlParserException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Responsible for creating a {@link Pom} object.
 */
public final class XmlPomBuilder extends MavenModelBuilder<MavenModel> {
    private static final String defaultPackaging = "jar";

    private @Nullable String groupId;
    private @Nullable String artifactId;
    private @Nullable String classifier;
    private @Nullable String version;
    private @Nullable String packaging;
    private @Nullable String name;
    private @Nullable String description;
    private @Nullable String url;
    private @Nullable Artifact parent;

    private final @NotNull Map<String, Profile> profiles = new LinkedHashMap<>();

    /**
     * Instantiates a new Pom builder.
     *
     * @param parser the XML parser
     */
    XmlPomBuilder(final @NotNull XmlParser parser) {
        super(parser);
    }

    @Override
    public Pom build() throws BuilderException {
        try {
            XmlParser parser = getParser();
            if (parser.hasNext()) parser.next();
        } catch (XmlParserException e) {
            throw new BuilderException(e);
        }
        parseDocument();
        if (this.parent != null) {
            if (this.groupId == null) this.groupId = this.parent.getGroupId();
            if (this.version == null) this.version = this.parent.getVersion();
        }
        return buildObject("pom", () -> new Pom(
                Artifact.builder()
                        .groupId(Objects.requireNonNull(this.groupId, "groupId is marked non-null but is null"))
                        .artifactId(Objects.requireNonNull(this.artifactId, "artifactId is marked non-null but is null"))
                        .version(Objects.requireNonNull(this.version, "version is marked non-null but is null"))
                        .classifier(this.classifier)
                        .build(),
                this.name,
                this.description,
                this.url,
                this.packaging == null ? defaultPackaging : this.packaging,
                this.parent,
                this.profiles.values(),
                this.properties,
                this.repositories.values(),
                this.dependencyManagement.values(),
                this.dependencies.values()
        ));
    }

    /**
     * Parses the given document trying to populate all the above fields.
     *
     * @throws BuilderException in case of any errors
     */
    protected void parseDocument() throws BuilderException {
        onChildElements(t -> {
            switch (t) {
                case "groupId" -> this.groupId = getCurrentTextContent();
                case "artifactId" -> this.artifactId = getCurrentTextContent();
                case "version" -> this.version = getCurrentTextContent();
                case "classifier" -> this.classifier = getCurrentTextContent();
                case "name" -> this.name = getCurrentTextContent();
                case "description" -> this.description = getCurrentTextContent();
                case "url" -> this.url = getCurrentTextContent();
                case "packaging" -> this.packaging = getCurrentTextContent();
                case "parent" -> this.parent = parseParent();
                case "profiles" -> parseProfiles();
                case "properties" -> parseProperties();
                case "repositories" -> parseRepositories();
                case "dependencyManagement" -> parseDependencyManagement();
                case "dependencies" -> parseDependencies();
            }
        });
    }

    /**
     * Handles the <b>&lt;profiles&gt;</b> tag in the document.
     *
     * @throws BuilderException in case of any errors
     */
    void parseProfiles() throws BuilderException {
        onChildElements(t -> {
            if (t.equals("profile")) {
                Profile profile = Profile.builder(getParser()).build();
                this.profiles.put(profile.getId(), profile);
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
