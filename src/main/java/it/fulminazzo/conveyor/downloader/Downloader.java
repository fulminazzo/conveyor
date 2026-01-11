package it.fulminazzo.conveyor.downloader;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;

/**
 * A specialized object to download files to the given path.
 */
public interface Downloader {

    /**
     * Loops through all the base URLs trying to download the resource
     * at "&lt;base_url&gt;/&lt;resource_path&gt;".
     * Then, downloads it at "{@link #getWorkingDir()}/&lt;resource_path&gt;".
     *
     * @param resourcePath the resource path
     * @return the newly downloaded file
     * @throws DownloadException in case of any errors
     */
    default @NotNull File resolveToFile(final @NotNull String resourcePath) throws DownloadException {
        try (InputStream stream = resolve(resourcePath)) {
            File destination = new File(getWorkingDir(), resourcePath);
            Files.createDirectories(destination.getParentFile().toPath());
            Files.copy(stream, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destination;
        } catch (IOException e) {
            throw new DownloadException(String.format("Error while downloading resource '%s'", resourcePath), e);
        }
    }

    /**
     * Loops through all the base URLs trying to download the resource
     * at "&lt;base_url&gt;/&lt;resource_path&gt;".
     * Then, returns the resulting stream.
     *
     * @param resourcePath the resource path
     * @return the download stream
     * @throws DownloadException if the download could not be completed (mostly for resource not found)
     */
    @NotNull InputStream resolve(final @NotNull String resourcePath) throws DownloadException;

    /**
     * Checks if the given URLs are valid.
     * Then, adds them all to the internal list.
     * <br>
     * <b>WARNING</b>: only <i>base</i> urls should be passed.
     * <br>
     * If the goal is to download a resource in "https://www.example.com/resource/path",
     * then here only "https://www.example.com/" should be provided.
     *
     * @param urls the URLs
     * @return this downloader
     * @throws MalformedURLException if any of the URLs is malformed
     */
    default @NotNull Downloader addBaseUrls(final String @NotNull ... urls) throws MalformedURLException {
        return addBaseUrls(Arrays.asList(urls));
    }

    /**
     * Checks if the given URLs are valid.
     * Then, adds them all to the internal list.
     * <br>
     * <b>WARNING</b>: only <i>base</i> urls should be passed.
     * <br>
     * If the goal is to download a resource in "https://www.example.com/resource/path",
     * then here only "https://www.example.com/" should be provided.
     *
     * @param urls the URLs
     * @return this downloader
     * @throws MalformedURLException if any of the URLs is malformed
     */
    @NotNull Downloader addBaseUrls(final @NotNull Collection<String> urls) throws MalformedURLException;

    /**
     * Gets the working directory (where the downloads will be stored).
     *
     * @return the directory
     */
    @NotNull File getWorkingDir();

}
