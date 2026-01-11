package it.fulminazzo.conveyor.downloader;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A special {@link Downloader} that will check if the requested
 * resource is already present on disk before downloading.
 * If found, it will attempt to verify its validity through checksum.
 */
final class CachedDownloader extends DownloaderImpl {
    private static final int readingBufferSize = 8192;

    /**
     * Instantiates a new Cached downloader.
     *
     * @param workingDir the working dir
     */
    public CachedDownloader(final @NotNull File workingDir) {
        super(workingDir);
    }

    @Override
    public @NotNull File resolveToFile(final @NotNull String resourcePath) throws DownloadException {
        throw new UnsupportedOperationException("Should check cache");
    }

    @Override
    public @NotNull InputStream resolve(final @NotNull String resourcePath) throws DownloadException {
        throw new UnsupportedOperationException("Should check cache");
    }

    /**
     * Tries to obtain the given resource associated checksum.
     * The checksum resource location is computed as
     * "&lt;resource_path&gt;.&lt;algorithm_extension&gt;".
     *
     * @param resourcePath the resource path
     * @param algorithm    the algorithm
     * @return the checksum
     * @throws DownloadException in case of any errors
     */
    public @NotNull String resolveChecksum(final @NotNull String resourcePath,
                                           final @NotNull ChecksumAlgorithm algorithm) throws DownloadException {
        String finalPath = resourcePath + "." + algorithm.getExtension();
        try (InputStream stream = resolve(finalPath)) {
            return stream.toString();
        } catch (IOException e) {
            throw new DownloadException(String.format("Error while downloading resource '%s'", finalPath), e);
        }
    }

    /**
     * Computes the checksum for the given file.
     *
     * @param resourcePath the resource path
     * @param algorithm    the algorithm to use
     * @return the checksum
     * @throws IOException in case of any errors
     */
    public @NotNull String computeChecksum(final @NotNull String resourcePath,
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

        StringBuilder result = new StringBuilder();
        for (byte b : messageDigest.digest())
            result.append(String.format("%02x", b));

        return result.toString();
    }

}
