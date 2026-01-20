package it.fulminazzo.conveyor.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

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
     * All the HTTP status codes considered as redirection.
     */
    static final List<Integer> REDIRECTS_STATUSES = Arrays.asList(
            HttpURLConnection.HTTP_MOVED_PERM,
            HttpURLConnection.HTTP_MOVED_TEMP,
            307,
            308
    );
    /**
     * If in the last {@link RedirectInfo#REDIRECT_LIFE_TIME} milliseconds,
     * there have been more than the specified redirects for the current URL,
     * it will be automatically redirected to the redirect URL.
     */
    static final int MAX_REDIRECTS = 5;

    private static final @NotNull Map<String, RedirectInfo> redirects = new HashMap<>();

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
        if (resource.startsWith("/")) resource = resource.substring(1);

        RedirectInfo info = redirects.get(website);
        if (info != null && info.getRedirects() > MAX_REDIRECTS) {
            return openConnection(info.getUrl(), resource);
        }

        HttpURLConnection connection = openConnection(website + resource);
        int status = connection.getResponseCode();
        if (REDIRECTS_STATUSES.contains(status)) {
            String newUrl = connection.getHeaderField("Location");
            connection.disconnect();
            throw new IOException("Redirected to: " + newUrl);
        }
        return connection.getInputStream();
    }

    /**
     * Opens an HTTP connection to the given url.
     *
     * @param url the url
     * @return the connection
     * @throws IOException in case of any errors (usually connection or not found)
     */
    static @NotNull HttpURLConnection openConnection(final @NotNull String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(CONNECT_READ_TIMEOUT);
        connection.setReadTimeout(CONNECT_READ_TIMEOUT);
        return connection;
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
