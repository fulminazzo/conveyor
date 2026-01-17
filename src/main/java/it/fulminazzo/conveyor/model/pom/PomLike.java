package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a general pom data.
 */
interface PomLike {

    /**
     * Gets project.
     *
     * @return the project
     */
    @NotNull Artifact getProject();

    /**
     * Gets packaging.
     *
     * @return the packaging
     */
    @NotNull String getPackaging();

}
