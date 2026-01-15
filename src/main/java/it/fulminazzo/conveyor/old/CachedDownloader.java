package it.fulminazzo.conveyor.old;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * A special {@link Downloader} that will check if the requested
 * resource is already present on disk before downloading.
 * If found, it will attempt to verify its validity through checksum.
 */
final class CachedDownloader extends DownloaderImpl {
    private static final int readingBufferSize = 8192;

    private boolean redownloadOnUnverified = true;

    /**
     * Instantiates a new Cached downloader.
     *
     * @param workingDir the working dir
     */
    public CachedDownloader(final @NotNull File workingDir) {
        super(workingDir);
    }

    /**
     * Looks in the current {@link #getWorkingDir()} for a file
     * matching the resource path.
     * <br>
     * If found, it is verified using {@link ChecksumAlgorithm}s.
     * <br>
     * If not found, or the verification fails, it is downloaded.
     *
     * @param resourcePath the resource path
     * @return the file (whether already present or newly downloaded)
     * @throws DownloadException in case of any errors
     */
    @Override
    public @NotNull File resolveToFile(final @NotNull String resourcePath) throws DownloadException {
        File resourceFile = getResourceFile(resourcePath);
        if (resourceFile.exists()) {
            if (verifyCachedResource(resourcePath) || !this.redownloadOnUnverified)
                return resourceFile;
        }
        return super.resolveToFile(resourcePath);
    }

    /**
     * Uses all the {@link ChecksumAlgorithm}s to verify if
     * the corresponding resource file is valid or not.
     * <br>
     * <b>WARNING</b>: will <b>NOT</b> check for the file existence.
     *
     * @param resourcePath the resource path
     * @return true if it is
     */
    public boolean verifyCachedResource(final @NotNull String resourcePath) {
        for (ChecksumAlgorithm algorithm : ChecksumAlgorithm.values())
            try {
                String expected = resolveChecksum(resourcePath, algorithm);
                String actual = computeChecksum(resourcePath, algorithm);
                if (expected.equals(actual)) return true;
            } catch (DownloadException | IOException ignored) {
            }
        return false;
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
    @NotNull String resolveChecksum(final @NotNull String resourcePath,
                                    final @NotNull ChecksumAlgorithm algorithm) throws DownloadException {
        String finalPath = resourcePath + "." + algorithm.getExtension();
        try (InputStream stream = resolve(finalPath)) {
            String checksum = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            if (checksum.endsWith("\n")) checksum = checksum.substring(0, checksum.length() - 1);
            // remove the name of the file
            checksum = checksum.split(" ")[0];
            return checksum;
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
     * If <code>redownloadOnUnverified</code> is set to <code>true</code>,
     * when {@link #resolveToFile(String)} is invoked but {@link #verifyCachedResource(String)} fails,
     * it will attempt to re-download the resource.
     * <br>
     * <code>true</code> by default.
     *
     * @param redownloadOnUnverified the redownload on unverified
     * @return this cached downloader
     */
    public @NotNull CachedDownloader setRedownloadOnUnverified(final boolean redownloadOnUnverified) {
        this.redownloadOnUnverified = redownloadOnUnverified;
        return this;
    }

}
