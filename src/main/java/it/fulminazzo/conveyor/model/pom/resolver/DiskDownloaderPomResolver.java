package it.fulminazzo.conveyor.model.pom.resolver;

import it.fulminazzo.conveyor.downloader.DownloadException;
import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.downloader.Downloader;
import it.fulminazzo.conveyor.manager.RepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;

/**
 * An implementation of {@link BasePomResolver} that
 * downloads the requested pom only if it is not already present
 * on disk (does not check for its validity).
 */
final class DiskDownloaderPomResolver extends BasePomResolver {
    private final @NotNull Downloader downloader;

    /**
     * Instantiates a new Disk downloader pom resolver.
     *
     * @param repositoryManager the repository manager
     * @param workingDir        the working dir
     * @param logger            the logger
     */
    public DiskDownloaderPomResolver(final @NotNull RepositoryManager repositoryManager,
                                     final @NotNull File workingDir,
                                     final @NotNull Logger logger) {
        super(repositoryManager);
        this.downloader = Downloader.newDownloader(workingDir, logger);
    }

    @Override
    protected @NotNull InputStream resolve(final @NotNull String artifactPath,
                                           final @NotNull Collection<DownloadSource> downloadSources) throws DownloadException {
        try {
            File file = this.downloader.getResourceFile(artifactPath);
            if (!file.exists())
                file = this.downloader.resolveToFile(artifactPath, downloadSources);
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            // should be impossible
            throw new DownloadException(e);
        }
    }

}
