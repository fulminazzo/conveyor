package it.fulminazzo.conveyor.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A collection of utilities to work with the HTTP protocol
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpUtils {
    /**
     * The timeout for connection and reading operations.
     */
    static final int CONNECT_READ_TIMEOUT = 10000;

    /**
     * Opens a new connection to the website for the requested resource,
     * and returns the data.
     *
     * @param website  the website
     * @param resource the resource
     * @return the data
     * @throws IOException in case of any errors (usually connection or not found)
     */
    public static @NotNull InputStream openConnection(final @NotNull String website,
                                                      @NotNull String resource) throws IOException {
        try {
            if (resource.startsWith("/")) resource = resource.substring(1);
            HttpURLConnection connection = (HttpURLConnection) new URL(website + resource).openConnection();
            connection.setConnectTimeout(CONNECT_READ_TIMEOUT);
            connection.setReadTimeout(CONNECT_READ_TIMEOUT);
            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_MOVED_PERM ||
                    status == HttpURLConnection.HTTP_MOVED_TEMP ||
                    status == 307 || status == 308) {
                String newUrl = connection.getHeaderField("Location");
                connection.disconnect();
                throw new IOException("Redirected to: " + newUrl);
            }
            return connection.getInputStream();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

}
