package it.fulminazzo.conveyor.model.artifact.resolver;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * An object to resolve an artifact package given its information and packaging.
 */
public interface ArtifactResolver {

    /**
     * Resolves the artifact package and returns the corresponding file.
     *
     * @param artifact  the artifact
     * @param packaging the packaging
     * @return the artifact file
     * @throws ArtifactResolverException in case of any exceptions
     */
    @NotNull File resolve(final @NotNull Artifact artifact, final @NotNull String packaging) throws ArtifactResolverException;

}
