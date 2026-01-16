package it.fulminazzo.conveyor.model.pom.resolver;

import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.downloader.policy.ChecksumPolicies;
import it.fulminazzo.conveyor.model.repository.Repository;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A special type of {@link RepositoryBasedPomResolver}
 * that uses only {@link Repository} with an enabled
 * snapshots policy.
 */
final class SnapshotsRepositoryBasedPomResolver extends BaseRepositoryBasedPomResolver {

    /**
     * Instantiates a new Snapshots repository based pom resolver.
     *
     * @param workingDir the working dir
     * @param logger     the logger
     */
    public SnapshotsRepositoryBasedPomResolver(final @NotNull File workingDir,
                                               final @NotNull Logger logger) {
        super(workingDir, logger);
    }

    @SneakyThrows
    @Override
    public @NotNull RepositoryBasedPomResolver addRepositories(final @NotNull Collection<Repository> repositories) {
        List<DownloadSource> sources = new ArrayList<>();
        for (Repository repository : repositories) {
            Repository.Policy snapshotsPolicy = repository.getSnapshots();
            if (snapshotsPolicy.isEnabled())
                sources.add(new DownloadSource(repository.getUrl())
                        .withCapability(ChecksumPolicies.valueOf(snapshotsPolicy.getChecksumPolicy().name()))
                );
        }
        getEngine().addSources(sources);
        return this;
    }

}
