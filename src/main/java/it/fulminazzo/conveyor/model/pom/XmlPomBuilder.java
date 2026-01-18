package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.BuilderException;
import it.fulminazzo.conveyor.model.MavenModel;
import it.fulminazzo.conveyor.model.MavenModelBuilder;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.pom.metadata.Organization;
import it.fulminazzo.conveyor.model.pom.metadata.PomMetadata;
import it.fulminazzo.conveyor.model.profile.Profile;
import it.fulminazzo.conveyor.xml.XmlParser;
import it.fulminazzo.conveyor.xml.XmlParserException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Responsible for creating a {@link Pom} object.
 */
public final class XmlPomBuilder extends MavenModelBuilder<MavenModel> {
    private final @NotNull Pom.PomBuilder<?, ?> builder = Pom.builder();
    private final @NotNull PomMetadata.PomMetadataBuilder pomMetadataBuilder = PomMetadata.builder();
    
    private @Nullable String groupId;
    private @Nullable String artifactId;
    private @Nullable String classifier;
    private @Nullable String version;
    
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
        return buildObject("pom", () -> this.builder
                .project(Artifact.builder()
                        .groupId(Objects.requireNonNull(this.groupId, "groupId is marked non-null but is null"))
                        .artifactId(Objects.requireNonNull(this.artifactId, "artifactId is marked non-null but is null"))
                        .version(Objects.requireNonNull(this.version, "version is marked non-null but is null"))
                        .classifier(this.classifier)
                        .build())
                .metadata(this.pomMetadataBuilder.build())
                .parent(this.parent)
                .profiles(Set.copyOf(this.profiles.values()))
                .properties(this.properties)
                .repositories(Set.copyOf(this.repositories.values()))
                .dependencyManagement(Set.copyOf(this.dependencyManagement.values()))
                .dependencies(List.copyOf(this.dependencies.values()))
                .build()
        );
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
                case "packaging" -> this.builder.packaging(getCurrentTextContent());
                case "parent" -> this.parent = parseParent();
                case "profiles" -> parseProfiles();
                case "properties" -> parseProperties();
                case "repositories" -> parseRepositories();
                case "dependencyManagement" -> parseDependencyManagement();
                case "dependencies" -> parseDependencies();
                // METADATA
                case "modelVersion" -> this.pomMetadataBuilder.modelVersion(getCurrentTextContent());
                case "name" -> this.pomMetadataBuilder.name(getCurrentTextContent());
                case "description" -> this.pomMetadataBuilder.description(getCurrentTextContent());
                case "url" -> this.pomMetadataBuilder.url(getCurrentTextContent());
                case "inceptionYear" -> this.pomMetadataBuilder.inceptionYear(getCurrentTextContent());
                case "organization" -> this.pomMetadataBuilder.organization(parseOrganization());
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

    /**
     * METADATA
     */

    /**
     * Handles the <b>&lt;organization&gt;</b> tag in the document.
     *
     * @return the organization
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull Organization parseOrganization() throws BuilderException {
        Organization.OrganizationBuilder builder = Organization.builder();
        onChildElements(t -> {
            switch (t) {
                case "name" -> builder.name(getCurrentTextContent());
                case "url" -> builder.url(getCurrentTextContent());
            }
        });
        return buildObject("organization", builder::build);
    }

}
