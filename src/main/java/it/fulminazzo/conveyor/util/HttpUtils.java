package it.fulminazzo.conveyor.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Stores all the information about a redirect.
     */
    @Value
    static class RedirectInfo {
        /**
         * How many milliseconds a redirect should be considered valid.
         */
        static final long REDIRECT_LIFE_TIME = 60 * 60 * 1000;

        @NotNull String url;
        @NotNull List<Long> timestamps = new ArrayList<>();

        /**
         * Gets all the redirects of the last {@link #REDIRECT_LIFE_TIME} milliseconds.
         *
         * @return the number of redirects
         */
        public int getRedirects() {
            purgeRedirects();
            return this.timestamps.size();
        }

        /**
         * Adds a new redirect timestamp.
         *
         * @return this redirect info
         */
        public @NotNull RedirectInfo addRedirect() {
            this.timestamps.add(System.currentTimeMillis());
            return this;
        }

        private void purgeRedirects() {
            this.timestamps.removeIf(l -> l + REDIRECT_LIFE_TIME <= System.currentTimeMillis());
        }

    }

}
