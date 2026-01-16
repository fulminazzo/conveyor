package it.fulminazzo.conveyor.model.pom.resolver.engine;

import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.model.pom.resolver.PomResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * An engine to actually resolve artifacts poms.
 */
public interface PomResolverEngine extends PomResolver  {

    /**
     * Transfers the currently stored download sources to the given engine.
     *
     * @param other the other engine
     */
    void transferSources(final @NotNull PomResolverEngine other);

    /**
     * Adds the download sources to the current engine.
     *
     * @param sources the download sources
     * @return this engine
     */
    @NotNull PomResolverEngine addSources(final @NotNull Collection<DownloadSource> sources);

}
