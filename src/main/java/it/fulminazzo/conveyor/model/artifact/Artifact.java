package it.fulminazzo.conveyor.model.artifact;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a general Maven artifact.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public class Artifact extends ArtifactLike {
    private final @NotNull String version;

    /**
     * Gets the full path of the file representing this artifact
     * in a repository.
     *
     * @param extension the extension
     * @return the full path
     */
    public @NotNull String getFullPath(final @NotNull String extension) {
        return getPath() + getFileName(extension);
    }

    /**
     * Gets the path of the artifact in a repository.
     * <br>
     * The path is defined as:
     * "&lt;groupId&gt;/&lt;artifactId&gt;/&lt;version&gt;/"
     *
     * @return the path
     */
    public @NotNull String getPath() {
        return getGroupId().replace(".", "/") + "/" +
                getArtifactId() + "/" +
                getVersion() + "/";
    }

    /**
     * Gets the expected file name of the file representing this artifact.
     *
     * @param extension the extension
     * @return the file name
     */
    public @NotNull String getFileName(final @NotNull String extension) {
        String fileName = getArtifactId() + "-" + getVersion();
        String classifier = getClassifier();
        if (classifier != null) fileName += "-" + classifier;
        return fileName + "." + extension;
    }

}
