package it.fulminazzo.conveyor.downloader;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * A specialized object to download files to the given path.
 */
public interface Downloader {

    /**
     * Loops through all the download sources trying to download the resource
     * at "&lt;url&gt;/&lt;resource_path&gt;".
     * Then, downloads it at "{@link #getWorkingDir()}/&lt;resource_path&gt;".
     *
     * @param resourcePath the resource path
     * @return the newly downloaded file
     * @throws DownloadException in case of any errors
     */
    default @NotNull File resolveToFile(final @NotNull String resourcePath) throws DownloadException {
        try (InputStream stream = resolve(resourcePath)) {
            File destination = getResourceFile(resourcePath);
            Files.createDirectories(destination.getParentFile().toPath());
            Files.copy(stream, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destination;
        } catch (IOException e) {
            throw new DownloadException(String.format("Error while downloading resource '%s'", resourcePath), e);
        }
    }

    /**
     * Loops through all the download sources trying to download the resource
     * at "&lt;url&gt;/&lt;resource_path&gt;".
     * Then, returns the resulting stream.
     *
     * @param resourcePath the resource path
     * @return the download stream
     * @throws DownloadException if the download could not be completed (mostly for resource not found)
     */
    @NotNull InputStream resolve(final @NotNull String resourcePath) throws DownloadException;

    /**
     * Gets the associated resource file.
     *
     * @param resourcePath the resource path
     * @return the resource file
     */
    default @NotNull File getResourceFile(final @NotNull String resourcePath) {
        return new File(getWorkingDir(), resourcePath);
    }

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
