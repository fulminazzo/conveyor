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
        String version = artifactPath.substring(artifactPath.lastIndexOf("-") + 1);
        version = version.substring(0, version.indexOf("."));
        return new ByteArrayInputStream(String.format("""
                <project>
                    <groupId>it.fulminazzo</groupId>
                    <artifactId>conveyor</artifactId>
                    <version>%s</version>
                </project>
                """, version).getBytes());
    }

}
