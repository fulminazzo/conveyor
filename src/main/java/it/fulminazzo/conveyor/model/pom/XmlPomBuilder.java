package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.BuilderException;
import it.fulminazzo.conveyor.model.MavenModel;
import it.fulminazzo.conveyor.model.MavenModelBuilder;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.pom.metadata.*;
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
    private final @NotNull PomMetadata.PomMetadataBuilder<?, ?> pomMetadataBuilder = PomMetadata.builder();

    private @Nullable String groupId;
    private @Nullable String artifactId;
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
                        .build())
                .metadata(this.pomMetadataBuilder.build())
                .parent(this.parent)
                .profiles(Set.copyOf(this.profiles.values()))
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
                case "packaging" -> this.builder.packaging(getCurrentTextContent());
                case "parent" -> this.parent = parseParent();
                case "profiles" -> parseProfiles();
                case "properties" -> this.builder.properties(parseProperties());
                case "repositories" -> this.builder.repositories(parseRepositories());
                case "dependencyManagement" -> this.builder.dependencyManagement(parseDependencyManagement());
                case "dependencies" -> this.builder.dependencies(parseDependencies());
                // METADATA
                case "modelVersion" -> this.pomMetadataBuilder.modelVersion(getCurrentTextContent());
                case "name" -> this.pomMetadataBuilder.name(getCurrentTextContent());
                case "description" -> this.pomMetadataBuilder.description(getCurrentTextContent());
                case "url" -> this.pomMetadataBuilder.url(getCurrentTextContent());
                case "inceptionYear" -> this.pomMetadataBuilder.inceptionYear(getCurrentTextContent());
                case "organization" -> this.pomMetadataBuilder.organization(parseOrganization());
                case "licenses" -> this.pomMetadataBuilder.licenses(Set.copyOf(parseList("license", this::parseLicense)));
                case "developers" -> this.pomMetadataBuilder.developers(Set.copyOf(parseList("developer", this::parseDeveloper)));
                case "contributors" -> this.pomMetadataBuilder.contributors(Set.copyOf(parseList("contributor", this::parseContributor)));
                case "mailingLists" -> this.pomMetadataBuilder.mailingLists(List.copyOf(parseList("mailingList", this::parseMailingList)));
                case "prerequisites" -> this.pomMetadataBuilder.prerequisites(parsePrerequisites());
                case "modules" -> this.pomMetadataBuilder.modules(List.copyOf(parseModules()));
                case "scm" -> this.pomMetadataBuilder.scm(parseSCManagement());
                case "issueManagement" -> this.pomMetadataBuilder.issueManagement(parseIssueManagement());
                case "ciManagement" -> this.pomMetadataBuilder.ciManagement(parseCiManagement());
                case "distributionManagement" -> this.pomMetadataBuilder.distributionManagement(parseDistributionManagement());
                case "pluginRepositories" -> this.pomMetadataBuilder.pluginRepositories(parsePluginRepositories());
                case "build" -> this.pomMetadataBuilder.build(parseBuild());
                case "reporting" -> this.pomMetadataBuilder.reporting(parseReporting());
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

    /*
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

    /**
     * Handles the <b>&lt;license&gt;</b> tag in the document.
     *
     * @return the license
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull License parseLicense() throws BuilderException {
        License.LicenseBuilder builder = License.builder();
        onChildElements(t -> {
            switch (t) {
                case "name" -> builder.name(getCurrentTextContent());
                case "url" -> builder.url(getCurrentTextContent());
                case "distribution" -> builder.distribution(getCurrentTextContent());
                case "comments" -> builder.comments(getCurrentTextContent());
            }
        });
        return buildObject("license", builder::build);
    }

    /**
     * Handles the <b>&lt;developer&gt;</b> tag in the document.
     *
     * @return the developer
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull Developer parseDeveloper() throws BuilderException {
        Developer.DeveloperBuilder builder = Developer.builder();
        onChildElements(t -> {
            switch (t) {
                case "id" -> builder.id(getCurrentTextContent());
                case "name" -> builder.name(getCurrentTextContent());
                case "email" -> builder.email(getCurrentTextContent());
                case "url" -> builder.url(getCurrentTextContent());
                case "organization" -> builder.organization(getCurrentTextContent());
                case "organizationUrl" -> builder.organizationUrl(getCurrentTextContent());
                case "roles" -> builder.roles(parseRoles());
                case "timezone" -> builder.timezone(getCurrentTextContent());
                case "properties" -> builder.properties(parseProperties());
            }
        });
        return buildObject("developer", builder::build);
    }

    /**
     * Handles the <b>&lt;contributor&gt;</b> tag in the document.
     *
     * @return the contributor
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull Contributor parseContributor() throws BuilderException {
        Contributor.ContributorBuilder builder = Contributor.builder();
        onChildElements(t -> {
            switch (t) {
                case "name" -> builder.name(getCurrentTextContent());
                case "email" -> builder.email(getCurrentTextContent());
                case "url" -> builder.url(getCurrentTextContent());
                case "organization" -> builder.organization(getCurrentTextContent());
                case "organizationUrl" -> builder.organizationUrl(getCurrentTextContent());
                case "roles" -> builder.roles(parseRoles());
                case "timezone" -> builder.timezone(getCurrentTextContent());
                case "properties" -> builder.properties(parseProperties());
            }
        });
        return buildObject("contributor", builder::build);
    }

    /**
     * Handles the <b>&lt;roles&gt;</b> tag in the document.
     *
     * @return the roles
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull List<String> parseRoles() throws BuilderException {
        return parseStringList("role");
    }

    /**
     * Handles the <b>&lt;mailingList&gt;</b> tag in the document.
     *
     * @return the mailingList
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull MailingList parseMailingList() throws BuilderException {
        MailingList.MailingListBuilder builder = MailingList.builder();
        onChildElements(t -> {
            switch (t) {
                case "name" -> builder.name(getCurrentTextContent());
                case "subscribe" -> builder.subscribe(getCurrentTextContent());
                case "unsubscribe" -> builder.unsubscribe(getCurrentTextContent());
                case "post" -> builder.post(getCurrentTextContent());
                case "archive" -> builder.archive(getCurrentTextContent());
                case "otherArchives" -> builder.otherArchives(parseOtherArchives());
            }
        });
        return buildObject("mailingList", builder::build);
    }

    /**
     * Handles the <b>&lt;otherArchives&gt;</b> tag in the document.
     *
     * @return the archives
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull List<String> parseOtherArchives() throws BuilderException {
        return parseStringList("otherArchive");
    }

    /**
     * Handles the <b>&lt;prerequisites&gt;</b> tag in the document.
     *
     * @return the prerequisites
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull PomMetadata.Prerequisites parsePrerequisites() throws BuilderException {
        PomMetadata.Prerequisites.PrerequisitesBuilder builder = PomMetadata.Prerequisites.builder();
        onChildElements(t -> {
            if (t.equals("maven"))
                builder.maven(getCurrentTextContent());
        });
        return buildObject("prerequisites", builder::build);
    }

    /**
     * Handles the <b>&lt;scm&gt;</b> tag in the document.
     *
     * @return the sc management
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull PomMetadata.SCManagement parseSCManagement() throws BuilderException {
        PomMetadata.SCManagement.SCManagementBuilder builder = PomMetadata.SCManagement.builder();
        onChildElements(t -> {
            switch (t) {
                case "connection" -> builder.connection(getCurrentTextContent());
                case "developerConnection" -> builder.developerConnection(getCurrentTextContent());
                case "tag" -> builder.tag(getCurrentTextContent());
                case "url" -> builder.url(getCurrentTextContent());
            }
        });
        return buildObject("scm", builder::build);
    }

    /**
     * Handles the <b>&lt;issueManagement&gt;</b> tag in the document.
     *
     * @return the issueManagement
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull PomMetadata.IssueManagement parseIssueManagement() throws BuilderException {
        PomMetadata.IssueManagement.IssueManagementBuilder builder = PomMetadata.IssueManagement.builder();
        onChildElements(t -> {
            switch (t) {
                case "system" -> builder.system(getCurrentTextContent());
                case "url" -> builder.url(getCurrentTextContent());
            }
        });
        return buildObject("issueManagement", builder::build);
    }

    /**
     * Handles the <b>&lt;ciManagement&gt;</b> tag in the document.
     *
     * @return the ciManagement
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull PomMetadata.CiManagement parseCiManagement() throws BuilderException {
        PomMetadata.CiManagement.CiManagementBuilder builder = PomMetadata.CiManagement.builder();
        onChildElements(t -> {
            switch (t) {
                case "system" -> builder.system(getCurrentTextContent());
                case "url" -> builder.url(getCurrentTextContent());
                case "notifiers" -> builder.notifiers(List.copyOf(parseList("notifier", this::parseNotifier)));
            }
        });
        return buildObject("ciManagement", builder::build);
    }

    /**
     * Handles the <b>&lt;notifier&gt;</b> tag in the document.
     *
     * @return the notifier
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull Notifier parseNotifier() throws BuilderException {
        Notifier.NotifierBuilder builder = Notifier.builder();
        onChildElements(t -> {
            switch (t) {
                case "type" -> builder.type(getCurrentTextContent());
                case "sendOnError" -> builder.sendOnError(getCurrentTextContent());
                case "sendOnFailure" -> builder.sendOnFailure(getCurrentTextContent());
                case "sendOnSuccess" -> builder.sendOnSuccess(getCurrentTextContent());
                case "sendOnWarning" -> builder.sendOnWarning(getCurrentTextContent());
                case "address" -> builder.address(getCurrentTextContent());
                case "configuration" -> builder.configuration(parseProperties());
            }
        });
        return buildObject("notifier", builder::build);
    }

    /**
     * Handles the <b>&lt;build&gt;</b> tag in the document.
     *
     * @return the build
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull Build parseBuild() throws BuilderException {
        Build.BuildBuilder<?, ?> builder = Build.builder();
        onChildElements(t -> {
            switch (t) {
                case "sourceDirectory" -> builder.sourceDirectory(getCurrentTextContent());
                case "scriptSourceDirectory" -> builder.scriptSourceDirectory(getCurrentTextContent());
                case "testSourceDirectory" -> builder.testSourceDirectory(getCurrentTextContent());
                case "outputDirectory" -> builder.outputDirectory(getCurrentTextContent());
                case "testOutputDirectory" -> builder.testOutputDirectory(getCurrentTextContent());
                case "extensions" -> builder.extensions(List.copyOf(parseList("extension", this::parseExtension)));
                case "defaultGoal" -> builder.defaultGoal(getCurrentTextContent());
                case "resources" -> builder.resources(List.copyOf(parseList("resource", this::parseResource)));
                case "testResources" -> builder.testResources(List.copyOf(parseList("testResource", this::parseResource)));
                case "directory" -> builder.directory(getCurrentTextContent());
                case "finalName" -> builder.finalName(getCurrentTextContent());
                case "filters" -> builder.filters(parseStringList("filter"));
                case "pluginManagement" -> builder.pluginManagement(Set.copyOf(parsePluginManagement()));
                case "plugins" -> builder.plugins(List.copyOf(parseList("plugin", this::parsePlugin)));
            }
        });
        return buildObject("build", builder::build);
    }

    /**
     * Handles the <b>&lt;extension&gt;</b> tag in the document.
     *
     * @return the extension artifact
     * @throws BuilderException in case of reading or parsing errors
     */
    @NotNull Artifact parseExtension() throws BuilderException {
        Artifact.ArtifactBuilder<?, ?> builder = Artifact.builder();
        onChildElements(t -> {
            switch (t) {
                case "groupId" -> builder.groupId(getCurrentTextContent());
                case "artifactId" -> builder.artifactId(getCurrentTextContent());
                case "version" -> builder.version(getCurrentTextContent());
            }
        });
        return buildObject("extension", builder::build);
    }

}
