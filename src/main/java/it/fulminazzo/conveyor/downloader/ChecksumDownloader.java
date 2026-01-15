package it.fulminazzo.conveyor.downloader;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private final @NotNull Downloader delegate;

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

    @Override
    public @NotNull InputStream resolve(final @NotNull String resourcePath) throws DownloadException {
        return this.delegate.resolve(resourcePath);
    }

    @Override
    public @NotNull Downloader addDownloadSources(final @NotNull Collection<DownloadSource> sources) {
        return this.delegate.addDownloadSources(sources);
    }

    @Override
    public @NotNull File getWorkingDir() {
        return this.delegate.getWorkingDir();
    }

}
