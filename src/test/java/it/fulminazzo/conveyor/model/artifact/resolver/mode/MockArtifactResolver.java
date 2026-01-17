package it.fulminazzo.conveyor.model.artifact.resolver.mode;

import it.fulminazzo.conveyor.downloader.DownloadException;
import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.manager.RepositoryManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;

public class MockArtifactResolver extends BaseArtifactResolver {

    public MockArtifactResolver(final @NotNull RepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    protected @NotNull File resolve(final @NotNull String artifactPath,
                                    final @NotNull Collection<DownloadSource> downloadSources) throws DownloadException {
        return new File(artifactPath);
    }

}
