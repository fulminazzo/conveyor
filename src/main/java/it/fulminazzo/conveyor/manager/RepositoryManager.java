package it.fulminazzo.conveyor.manager;

import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.model.repository.Repository;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

/**
 * Handles all the given {@link Repository},
 * separating them based on the {@link Repository.Policy} provided.
 */
public interface RepositoryManager {

    /**
     * Adds the repositories to the list.
     *
     * @param repositories the repositories
     * @return this manager
     */
    default @NotNull RepositoryManager addRepositories(final Repository @NotNull ... repositories) {
        return addRepositories(Arrays.asList(repositories));
    }

    /**
     * Adds the repositories to the list.
     *
     * @param repositories the repositories
     * @return this manager
     */
    @NotNull RepositoryManager addRepositories(final @NotNull Collection<Repository> repositories);

    /**
     * Gets all the repositories with the <b>releases</b>
     * {@link Repository.Policy#isEnabled()} set to true.
     *
     * @return the repositories
     */
    @NotNull Collection<DownloadSource> getReleasesRepositories();

    /**
     * Gets all the repositories with the <b>snapshots</b>
     * {@link Repository.Policy#isEnabled()} set to true.
     *
     * @return the repositories
     */
    @NotNull Collection<DownloadSource> getSnapshotsRepositories();

    /**
     * Instantiates a new Repository manager.
     *
     * @return the repository manager
     */
    static @NotNull RepositoryManager newManager() {
        return new RepositoryManagerImpl();
    }

}
