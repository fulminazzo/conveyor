package it.fulminazzo.conveyor.model.pom.resolver;

import it.fulminazzo.conveyor.downloader.DownloadException;
import it.fulminazzo.conveyor.model.BuilderException;
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
    public PomResolverException(final @NotNull BuilderException cause) {
        this(cause.getMessage(), cause.getCause());
    }

    /**
     * Instantiates a new Pom resolver exception.
     *
     * @param cause the cause
     */
    public PomResolverException(final @NotNull XmlParserException cause) {
        this(cause.getMessage(), cause.getCause());
    }

    /**
     * Instantiates a new Pom resolver exception.
     *
     * @param cause the cause
     */
    public PomResolverException(final @NotNull DownloadException cause) {
        this(cause.getMessage(), cause.getCause());
    }

}
