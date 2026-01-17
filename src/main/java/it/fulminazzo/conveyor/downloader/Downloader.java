package it.fulminazzo.conveyor.downloader;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

/**
 * A specialized object to download files to the given path.
 */
public interface Downloader {

    /**
     * Loops through all the download sources trying to download the resource
     * at "&lt;url&gt;/&lt;resource_path&gt;".
     * Then, downloads it at "{@link #getWorkingDir()}/&lt;resource_path&gt;".
     *
     * @param resourcePath    the resource path
     * @param downloadSources the download sources
     * @return the newly downloaded file
     * @throws DownloadException in case of any errors
     */
    @NotNull File resolveToFile(final @NotNull String resourcePath,
                                final @NotNull Collection<DownloadSource> downloadSources) throws DownloadException;

    /**
     * Loops through all the download sources trying to download the resource
     * at "&lt;url&gt;/&lt;resource_path&gt;".
     * Then, returns the resulting stream.
     *
     * @param resourcePath    the resource path
     * @param downloadSources the download sources
     * @return the download stream
     * @throws DownloadException if the download could not be completed (mostly for resource not found)
     */
    @NotNull InputStream resolve(final @NotNull String resourcePath,
                                 final @NotNull Collection<DownloadSource> downloadSources) throws DownloadException;

    /**
     * Gets the associated resource file.
     *
     * @param resourcePath the resource path
     * @return the resource file
     */
    @NotNull File getResourceFile(final @NotNull String resourcePath);

    /**
     * Gets the working directory (where the downloads will be stored).
     *
     * @return the directory
     */
    @NotNull File getWorkingDir();

    /**
     * Instantiates a new downloader.
     *
     * @param workingDir the working dir
     * @param logger     the logger
     * @return the downloader
     */
    static @NotNull Downloader newDownloader(final @NotNull File workingDir,
                                             final @NotNull Logger logger) {
        return new BaseDownloader(workingDir, logger);
    }

    /**
     * Instantiates a new checksum downloader.
     *
     * @param workingDir the working dir
     * @param logger     the logger
     * @return the downloader
     */
    static @NotNull Downloader newChecksumDownloader(final @NotNull File workingDir,
                                                     final @NotNull Logger logger) {
        return new ChecksumDownloader(
                newDownloader(workingDir, logger),
                logger
        );
    }

}
