package it.fulminazzo.conveyor.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
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

    private static final String protocolRegex = "^([a-zA-Z][a-zA-Z0-9+.-]*)://(.*)$";
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
    public static @NotNull InputStream openHttpConnection(final @NotNull String website,
                                                          @NotNull String resource) throws IOException {
        final String url = formatUrl(website);
        if (resource.startsWith("/")) resource = resource.substring(1);

        RedirectInfo info = redirects.get(url);
        if (info != null && info.getRedirects() > MAX_REDIRECTS) {
            return openHttpConnection(info.getUrl(), resource);
        }

        HttpURLConnection connection = openHttpConnection(url + resource);
        int status = connection.getResponseCode();
        if (REDIRECTS_STATUSES.contains(status)) {
            String newUrl = connection.getHeaderField("Location");
            connection.disconnect();
            throw new IOException("Redirected to: " + newUrl);
        }
        return connection.getInputStream();
    }

    static @NotNull InputStream handleRedirect(final @NotNull String url,
                                               final @NotNull String redirect,
                                               final @NotNull String resourcePath) throws IOException {
        if (redirect.startsWith("/")) {
            throw new UnsupportedOperationException("Handle relative redirect");
        } else {
            throw new UnsupportedOperationException("Handle absolute redirect");
        }
    }

    /**
     * Opens an HTTP connection to the given url.
     *
     * @param url the url
     * @return the connection
     * @throws IOException in case of any errors (usually connection or not found)
     */
    static @NotNull HttpURLConnection openHttpConnection(final @NotNull String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(CONNECT_READ_TIMEOUT);
        connection.setReadTimeout(CONNECT_READ_TIMEOUT);
        return connection;
    }

    /**
     * Attempts to complete the URL if protocol or leading slash are missing.
     *
     * @param url the URL
     * @return the formatted URL
     * @throws MalformedURLException in case of invalid URL
     */
    public static @NotNull String formatUrl(final @NotNull String url) throws MalformedURLException {
        String modifiedUrl = url;
        if (!modifiedUrl.matches(protocolRegex)) modifiedUrl = "https://" + modifiedUrl;
        if (!modifiedUrl.endsWith("/")) modifiedUrl += "/";
        try {
            new URI(modifiedUrl);
        } catch (URISyntaxException e) {
            throw new MalformedURLException(String.format("Invalid URL '%s'", url));
        }
        return modifiedUrl;
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
