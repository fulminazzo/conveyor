package it.fulminazzo.conveyor.manager;

import it.fulminazzo.conveyor.downloader.DownloadSource;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

/**
 * Keeps track of all the {@link DownloadSource}s.
 */
public interface DownloadSourceManager {

    /**
     * Gets the currently stored download sources.
     *
     * @return the download sources
     */
    @NotNull Collection<DownloadSource> getDownloadSources();

    /**
     * Adds all the given {@link DownloadSource}s to the list.
     *
     * @param sources the download sources
     * @return this manager
     */
    default @NotNull DownloadSourceManager addDownloadSources(final DownloadSource @NotNull ... sources) {
        return addDownloadSources(Arrays.asList(sources));
    }

    /**
     * Adds all the given {@link DownloadSource}s to the list.
     *
     * @param sources the download sources
     * @return this manager
     */
    @NotNull DownloadSourceManager addDownloadSources(final @NotNull Collection<DownloadSource> sources);

    /**
     * Instantiates a new Download source manager.
     *
     * @return the download source manager
     */
    static @NotNull DownloadSourceManager newManager() {
        return new DownloadSourceManagerImpl();
    }

}
