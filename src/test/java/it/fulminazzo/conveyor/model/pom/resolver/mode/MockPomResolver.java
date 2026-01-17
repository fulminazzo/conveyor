package it.fulminazzo.conveyor.model.pom.resolver.mode;

import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.manager.RepositoryManager;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;

public class MockPomResolver extends BasePomResolver {

    public MockPomResolver(final @NotNull RepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    protected @NotNull InputStream resolve(final @NotNull String artifactPath,
                                           final @NotNull Collection<DownloadSource> downloadSources) {
        return new ByteArrayInputStream(new byte[0]);
    }

}
