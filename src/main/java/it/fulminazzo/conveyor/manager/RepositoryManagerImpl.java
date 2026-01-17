package it.fulminazzo.conveyor.manager;

import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.downloader.policy.ChecksumPolicies;
import it.fulminazzo.conveyor.model.repository.Repository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.util.Collection;

/**
 * A basic implementation for {@link RepositoryManager}.
 */
@RequiredArgsConstructor
final class RepositoryManagerImpl implements RepositoryManager {
    private final @NotNull DownloadSourceManager releases = DownloadSourceManager.newManager();
    private final @NotNull DownloadSourceManager snapshots = DownloadSourceManager.newManager();

    private final @NotNull Logger logger;

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
                this.logger.warn("Skipping repository: {}", e.getMessage());
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
