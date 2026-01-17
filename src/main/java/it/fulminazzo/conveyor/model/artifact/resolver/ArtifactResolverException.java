package it.fulminazzo.conveyor.model.artifact.resolver;

import it.fulminazzo.conveyor.downloader.DownloadException;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import lombok.experimental.StandardException;
import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown by {@link ArtifactResolver}.
 */
@StandardException
public final class ArtifactResolverException extends Exception {

    /**
     * Instantiates a new Artifact resolver exception.
     *
     * @param cause the cause
     */
    public ArtifactResolverException(final @NotNull Artifact artifact,
                                     final @NotNull DownloadException cause) {
        this(String.format("Resolving artifact for artifact '%s' caused error: %s",
                        artifact.getCoordinates(), cause.getMessage()),
                cause.getCause());
    }

}
