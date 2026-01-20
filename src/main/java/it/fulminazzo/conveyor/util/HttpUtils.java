package it.fulminazzo.conveyor.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Arrays;
import java.util.List;

/**
 * A collection of utilities to work with the HTTP protocol
 */
@Slf4j
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

    private static final String protocolRegex = "^([a-zA-Z][a-zA-Z0-9+.-]*)://(.*)$";

    /**
     * Opens a new connection to the website for the requested resource and returns the data.
     *
     * @param website  the website
     * @param resource the resource
     * @return the data
     * @throws IOException in case of any errors (usually connection or not found)
     */
    public static @NotNull InputStream openHttpConnection(@NotNull String website,
                                                          @NotNull String resource) throws IOException {
        website = formatUrl(website);
        if (!resource.startsWith("/")) resource = "/" + resource;
        final String url = website + resource;
        HttpURLConnection connection = openHttpConnection(url);
        int status = connection.getResponseCode();
        log.debug("{} HTTP/1.1 -> {} -- {}", connection.getRequestMethod(), status, url);
        if (REDIRECTS_STATUSES.contains(status)) {
            String newUrl = connection.getHeaderField("Location");
            log.debug("{} -> {}", url, newUrl);
            connection.disconnect();
            return handleRedirect(website, newUrl, resource);
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
     * Handles the redirection of the requested resource.
     * <ul>
     *     <li>if it is a <b>relative</b> redirect, it will attempt to extract the common part from
     *     <code>resourcePath</code> and <code>redirect</code> and it will update the <code>url</code> accordingly;</li>
     *     <li>if it is an <b>absolute</b> redirect, and it is able to extract a common part from
     *     <code>resourcePath</code> and <code>redirect</code>, it will store and update the {@link RedirectInfo}
     *     of the given URL (to allow for redirection caching).</li>
     * </ul>
     *
     * @param url          the url
     * @param redirect     the redirect
     * @param resourcePath the resource path
     * @return the data
     * @throws IOException in case of any errors (usually connection or not found)
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
                url = formatUrl(redirect.substring(0, index));
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

}
