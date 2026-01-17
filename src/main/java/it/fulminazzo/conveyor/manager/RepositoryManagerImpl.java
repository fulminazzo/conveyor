package it.fulminazzo.conveyor.manager;

import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.downloader.policy.ChecksumPolicies;
import it.fulminazzo.conveyor.model.repository.Repository;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.util.Collection;

/**
 * A basic implementation for {@link RepositoryManager}.
 */
final class RepositoryManagerImpl implements RepositoryManager {
    private final @NotNull DownloadSourceManager releases = DownloadSourceManager.newManager();
    private final @NotNull DownloadSourceManager snapshots = DownloadSourceManager.newManager();

    @Override
    public @NotNull RepositoryManager addRepositories(final @NotNull Collection<Repository> repositories) {
        for (Repository repository : repositories) {
            try {
                Repository.Policy releases = repository.getReleases();
                if (releases.isEnabled())
                    this.releases.addDownloadSources(toDownloadSource(repository, releases));

                Repository.Policy snapshots = repository.getSnapshots();
                if (snapshots.isEnabled())
                    this.snapshots.addDownloadSources(toDownloadSource(repository, snapshots));
            } catch (MalformedURLException e) {
                //TODO: logging?
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    @Override
    public @NotNull Collection<DownloadSource> getReleasesRepositories() {
        return this.releases.getDownloadSources();
    }

    @Override
    public @NotNull Collection<DownloadSource> getSnapshotsRepositories() {
        return this.snapshots.getDownloadSources();
    }

    private static @NotNull DownloadSource toDownloadSource(final @NotNull Repository repository,
                                                            final @NotNull Repository.Policy policy) throws MalformedURLException {
        return new DownloadSource(repository.getUrl())
                .withCapability(ChecksumPolicies.valueOf(policy.getChecksumPolicy().name()));
    }

}
