package it.fulminazzo.conveyor.model.pom.resolver;

import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.model.repository.Repository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.stream.Collectors;

final class MockRepositoryBasedPomResolver extends BaseRepositoryBasedPomResolver {

    public MockRepositoryBasedPomResolver(final @NotNull File workingDir,
                                          final @NotNull Logger logger) {
        super(workingDir, logger);
    }

    @Override
    public @NotNull RepositoryBasedPomResolver addRepositories(final @NotNull Collection<Repository> repositories) {
        getEngine().addSources(repositories.stream()
                .map(Repository::getUrl)
                .map(s -> {
                    try {
                        return new DownloadSource(s);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet())
        );
        return this;
    }

}
