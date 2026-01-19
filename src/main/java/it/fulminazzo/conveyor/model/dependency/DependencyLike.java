package it.fulminazzo.conveyor.model.dependency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a general dependency interface.
 */
interface DependencyLike {

    /**
     * Gets the coordinates of the current dependency.
     *
     * @return the coordinates
     */
    default @NotNull String getCoordinates() {
        final String separator = ":";
        String classifier = getClassifier();
        return getGroupId() + separator +
                getArtifactId() + separator +
                getType() + separator +
                (classifier == null ? "" : classifier);
    }

    /**
     * Gets group id.
     *
     * @return the group id
     */
    @NotNull String getGroupId();

    /**
     * Gets artifact id.
     *
     * @return the artifact id
     */
    @NotNull String getArtifactId();

    /**
     * Gets type.
     *
     * @return the type
     */
    @NotNull String getType();

    /**
     * Gets classifier.
     *
     * @return the classifier
     */
    @Nullable String getClassifier();

    /**
     * Gets version.
     *
     * @return the version
     */
    String getVersion();

    /**
     * Gets exclusions.
     *
     * @return the exclusions
     */
    @NotNull ExclusionsManager getExclusionsManager();

}
