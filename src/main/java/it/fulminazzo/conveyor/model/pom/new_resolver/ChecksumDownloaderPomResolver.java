package it.fulminazzo.conveyor.model.pom.new_resolver;

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
 * An implementation of {@link BasePomResolver}  that
 * checks if the pom is already present, if it is it attempts
 * to compute and verify its checksum, otherwise proceeds to
 * download the resource.
 */
final class ChecksumDownloaderPomResolver extends BasePomResolver {
    private final @NotNull Downloader downloader;

    /**
     * Instantiates a new Checksum downloader pom resolver.
     *
     * @param repositoryManager the repository manager
     * @param workingDir        the working dir
     * @param logger            the logger
     */
    public ChecksumDownloaderPomResolver(final @NotNull RepositoryManager repositoryManager,
                                         final @NotNull File workingDir,
                                         final @NotNull Logger logger) {
        super(repositoryManager);
        this.downloader = Downloader.newChecksumDownloader(workingDir, logger);
    }

    @Override
    protected @NotNull InputStream resolve(final @NotNull String artifactPath,
                                           final @NotNull Collection<DownloadSource> downloadSources) throws DownloadException {
        try {
            File file = this.downloader.resolveToFile(artifactPath, downloadSources);
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            // should be impossible
            throw new DownloadException(e);
        }
    }

}
