package it.fulminazzo.conveyor.manager;

import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.model.repository.Repository;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A basic implementation for {@link RepositoryManager}.
 */
final class RepositoryManagerImpl implements RepositoryManager {
    private final @NotNull DownloadSourceManager releases = DownloadSourceManager.newManager();
    private final @NotNull DownloadSourceManager snapshots = DownloadSourceManager.newManager();

    @Override
    public @NotNull RepositoryManager addRepositories(final @NotNull Collection<Repository> repositories) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Collection<DownloadSource> getReleasesRepositories() {
        return this.releases.getDownloadSources();
    }

    @Override
    public @NotNull Collection<DownloadSource> getSnapshotsRepositories() {
        return this.snapshots.getDownloadSources();
    }

}
