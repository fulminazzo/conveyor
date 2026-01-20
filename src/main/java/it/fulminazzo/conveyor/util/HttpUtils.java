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
        if (!resource.startsWith("/")) resource = "/" + resource;

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
     * Handle redirect input stream.
     *
     * @param url          the url
     * @param redirect     the redirect
     * @param resourcePath the resource path
     * @return the input stream
     * @throws IOException the io exception
     */
    static @NotNull InputStream handleRedirect(@NotNull String url,
                                               final @NotNull String redirect,
                                               @NotNull String resourcePath) throws IOException {
        int index = StringUtils.findCommonSuffix(redirect, resourcePath);
        if (redirect.startsWith("/")) {
            if (index > -1 && index < redirect.length()) {
                url += redirect.substring(0, index);
                resourcePath = redirect.substring(index);
            } else {
                url = extractUrl(url);
                resourcePath = redirect;
            }
        } else {
            if (index > -1 && index < redirect.length()) {
                String redirectUrl = formatUrl(redirect.substring(0, index));

                RedirectInfo info = redirects.get(url);
                if (info == null || !info.getUrl().equals(redirectUrl)) {
                    info = new RedirectInfo(redirectUrl);
                    redirects.put(url, info);
                }
                info.addRedirect();

                url = redirectUrl;
                resourcePath = redirect.substring(index);
            } else {
                url = extractUrl(redirect);
                resourcePath = redirect.substring(url.length());
            }
        }
        return openHttpConnection(url, resourcePath);
    }

    /**
     * Given a URL, attempts to retrieve only the link to the website,
     * removing any leading resource path.
     *
     * @param url the url
     * @return the extracted url
     * @throws MalformedURLException in case of invalid URL
     */
    static @NotNull String extractUrl(final @NotNull String url) throws MalformedURLException {
        try {
            URI uri = new URI(url);
            String website = uri.getScheme() + "://" + uri.getHost();
            int port = uri.getPort();
            if (port != -1) website += ":" + port;
            return formatUrl(website);
        } catch (URISyntaxException e) {
            throw new MalformedURLException(String.format("Invalid URL '%s'", url));
        }
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
        if (modifiedUrl.endsWith("/")) modifiedUrl = modifiedUrl.substring(0, modifiedUrl.length() - 1);
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
