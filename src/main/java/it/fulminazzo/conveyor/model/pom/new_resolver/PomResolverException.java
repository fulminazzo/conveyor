package it.fulminazzo.conveyor.model.pom.new_resolver;

import it.fulminazzo.conveyor.downloader.DownloadException;
import it.fulminazzo.conveyor.model.BuilderException;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.xml.XmlParserException;
import lombok.experimental.StandardException;
import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown by {@link PomResolver}.
 */
@StandardException
public final class PomResolverException extends Exception {

    /**
     * Instantiates a new Pom resolver exception.
     *
     * @param cause the cause
     */
    public PomResolverException(final @NotNull Artifact artifact,
                                final @NotNull BuilderException cause) {
        this(String.format("Resolving pom for artifact '%s' caused error: %s",
                artifact.getCoordinates(), cause.getMessage()),
                cause.getCause());
    }

    /**
     * Instantiates a new Pom resolver exception.
     *
     * @param cause the cause
     */
    public PomResolverException(final @NotNull Artifact artifact,
                                final @NotNull XmlParserException cause) {
        this(String.format("Resolving pom for artifact '%s' caused error: %s",
                artifact.getCoordinates(), cause.getMessage()),
                cause.getCause());
    }

    /**
     * Instantiates a new Pom resolver exception.
     *
     * @param cause the cause
     */
    public PomResolverException(final @NotNull Artifact artifact,
                                final @NotNull DownloadException cause) {
        this(String.format("Resolving pom for artifact '%s' caused error: %s",
                artifact.getCoordinates(), cause.getMessage()),
                cause.getCause());
    }

}
