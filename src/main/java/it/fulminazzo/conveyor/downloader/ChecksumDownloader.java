package it.fulminazzo.conveyor.downloader;

import it.fulminazzo.conveyor.downloader.policy.ChecksumPolicy;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HexFormat;

/**
 * A special type of {@link Downloader} that supports
 * <b>checksum</b> checking, to verify if the resource
 * already present on disk is valid.
 * <br>
 * It expects the corresponding checksum to be at
 * "&lt;url&gt;/&lt;resource_path&gt;.&lt;checksum_algorithm&gt;"
 */
@RequiredArgsConstructor
final class ChecksumDownloader implements Downloader {
    private static final int readingBufferSize = 8192;

    @Delegate
    private final @NotNull Downloader delegate;
    private final @NotNull Logger logger;

    /**
     * Looks in the current {@link #getWorkingDir()} for a file
     * matching the resource path.
     * <br>
     * If found, it is verified using {@link ChecksumAlgorithm}s.
     * <br>
     * If not found, it is downloaded.
     *
     * @param resourcePath    the resource path
     * @param downloadSources the download sources
     * @return the file (whether already present or newly downloaded)
     * @throws DownloadException in case of any errors
     */
    @Override
    public @NotNull File resolveToFile(final @NotNull String resourcePath,
                                       final @NotNull Collection<DownloadSource> downloadSources) throws DownloadException {
        File resourceFile = getResourceFile(resourcePath);
        if (resourceFile.exists()) {
            if (verifyChecksum(resourcePath, downloadSources))
                return resourceFile;
        }
        return this.delegate.resolveToFile(resourcePath, downloadSources);
    }

    /**
     * Uses all the {@link ChecksumAlgorithm}s to verify if
     * the corresponding resource file is valid or not.
     * <br>
     * <b>WARNING</b>: will <b>NOT</b> check for the file existence.
     *
     * @param resourcePath    the resource path
     * @param downloadSources the download sources to resolve the expected checksum
     * @return true if it is
     * @throws DownloadException in case of verification errors
     */
    public boolean verifyChecksum(final @NotNull String resourcePath,
                                  final @NotNull Collection<DownloadSource> downloadSources) throws DownloadException {
        for (ChecksumAlgorithm algorithm : ChecksumAlgorithm.values()) {
            final ChecksumResult result;
            try {
                result = resolveChecksum(resourcePath, algorithm, downloadSources);
            } catch (DownloadException e) {
                this.logger.debug("Could not resolve checksum with algorithm '{}' for resource '{}'", algorithm, resourcePath);
                continue;
            }
            try {
                final String expected = result.checksum();
                final String actual = computeChecksum(resourcePath, algorithm);
                if (expected.equals(actual)) return true;
                throw new InvalidChecksumException(algorithm, expected, actual);
            } catch (IOException | InvalidChecksumException e) {
                DownloadSource source = result.source();
                ChecksumPolicy policy = source.getCapability(ChecksumPolicy.class).orElse(null);
                this.logger.debug("Could not verify checksum with algorithm {} for resource '{}' from source '{}'",
                        algorithm, resourcePath, source.getUrl(), e);
                if (policy != null)
                    return policy.handleFailure(resourcePath, e, this.logger);
            }
        }
        return false;
    }

    /**
     * Attempts to obtain the given resource associated checksum.
     * The checksum resource location is computed as
     * "&lt;resource_path&gt;.&lt;algorithm_extension&gt;".
     *
     * @param resourcePath    the resource path
     * @param algorithm       the algorithm
     * @param downloadSources the download sources to resolve the checksum
     * @return a tuple containing the checksum and the used {@link DownloadSource}
     * @throws DownloadException in case it was not possible to download the checksum with the given algorithm
     */
    @NotNull ChecksumResult resolveChecksum(final @NotNull String resourcePath,
                                            final @NotNull ChecksumAlgorithm algorithm,
                                            final @NotNull Collection<DownloadSource> downloadSources) throws DownloadException {
        String finalPath = resourcePath + "." + algorithm.getExtension();
        for (DownloadSource downloadSource : downloadSources)
            try (InputStream stream = downloadSource.resolveResource(finalPath)) {
                String checksum = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                if (checksum.endsWith("\n")) checksum = checksum.substring(0, checksum.length() - 1);
                // remove the name of the file
                checksum = checksum.split(" ")[0];
                return new ChecksumResult(checksum, downloadSource);
            } catch (IOException ignored) {
                this.logger.debug("Could not resolve '{}' from source '{}'", finalPath, downloadSource.getUrl());
            }
        throw new DownloadException(String.format("Could not download checksum %s of resource '%s'", resourcePath, algorithm));
    }

    /**
     * Computes the checksum for the given file.
     *
     * @param resourcePath the resource path
     * @param algorithm    the algorithm to use
     * @return the checksum
     * @throws IOException in case of any errors
     */
    @NotNull String computeChecksum(final @NotNull String resourcePath,
                                    final @NotNull ChecksumAlgorithm algorithm) throws IOException {
        final File resourceFile = getResourceFile(resourcePath);

        final MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm.name());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unreachable code");
        }

        try (InputStream inputStream = new FileInputStream(resourceFile)) {
            byte[] buffer = new byte[readingBufferSize];
            int read;
            while ((read = inputStream.read(buffer)) != -1)
                messageDigest.update(buffer, 0, read);
        }

        return HexFormat.of().formatHex(messageDigest.digest());
    }

    /**
     * Represents the result of querying a {@link DownloadSource}
     * for obtaining the checksum of a resource.
     *
     * @param checksum the checksum
     * @param source   the used download source
     */
    record ChecksumResult(@NotNull String checksum, @NotNull DownloadSource source) {
    }

    private static final class InvalidChecksumException extends Exception {

        /**
         * Instantiates a new Invalid checksum exception.
         *
         * @param algorithm the algorithm
         * @param expected  the expected
         * @param actual    the actual
         */
        public InvalidChecksumException(final @NotNull ChecksumAlgorithm algorithm,
                                        final @NotNull String expected,
                                        final @NotNull String actual) {
            super(String.format("Checksum '%s' of algorithm '%s' did not match expected '%s'",
                    actual, algorithm, expected));
        }

    }

}
