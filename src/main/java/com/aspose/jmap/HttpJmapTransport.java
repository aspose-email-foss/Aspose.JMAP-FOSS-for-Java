package com.aspose.jmap;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default {@link JmapTransport} implementation that uses {@link java.net.http.HttpClient}
 * to perform HTTP requests against a JMAP server.
 *
 * <p>The transport follows redirects using {@link Redirect#NORMAL}. If a redirect response
 * is received, it manually follows the {@code Location} header, preserving all original
 * request headers (including {@code Authorization}) on a same-origin hop but dropping the
 * credential headers ({@code Authorization} / {@code Cookie} / {@code Proxy-Authorization})
 * once a redirect crosses to a different origin.
 *
 * <p>Any {@link IOException} or {@link InterruptedException} thrown by the underlying
 * {@code HttpClient} is wrapped in a {@link JmapError}.
 */
public final class HttpJmapTransport implements JmapTransport {

    private static final int MAX_REDIRECTS = 5;

    private final HttpClient client;

    /**
     * Creates a new {@code HttpJmapTransport} with a default {@link HttpClient}
     * configured to follow redirects using {@link Redirect#NORMAL}.
     */
    public HttpJmapTransport() {
        this(HttpClient.newBuilder()
                .followRedirects(Redirect.NORMAL)
                .build());
    }

    /**
     * Creates a new {@code HttpJmapTransport} with the supplied {@link HttpClient}.
     *
     * @param client the {@code HttpClient} to use for sending requests
     */
    public HttpJmapTransport(HttpClient client) {
        this.client = client;
    }

    @Override
    public JmapHttpResponse send(JmapHttpRequest request) {
        try {
            HttpRequest httpRequest = toHttpRequest(request);
            HttpResponse<byte[]> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());

            final String originalOrigin = originOf(URI.create(request.getUrl()));
            // Once a redirect crosses to a different origin, the caller's credentials
            // (Authorization / Cookie / Proxy-Authorization) must not be forwarded to it.
            boolean stripCredentials = false;
            int redirects = 0;
            while (isRedirect(httpResponse.statusCode()) && redirects < MAX_REDIRECTS) {
                String location = firstHeaderValue(httpResponse.headers().map(), "Location");
                if (location == null) {
                    break;
                }
                URI next = URI.create(request.getUrl()).resolve(location);
                if (!originOf(next).equals(originalOrigin)) {
                    stripCredentials = true;
                }
                HttpRequest redirectRequest = HttpRequest.newBuilder(next)
                        .method(request.getMethod(), bodyPublisher(request.getBody()))
                        .headers(flattenHeaders(request.getHeaders(), stripCredentials))
                        .build();

                httpResponse = client.send(redirectRequest, HttpResponse.BodyHandlers.ofByteArray());
                redirects++;
            }

            return new JmapHttpResponse(
                    httpResponse.statusCode(),
                    httpResponse.headers().map(),
                    httpResponse.body()
            );
        } catch (IOException | InterruptedException e) {
            java.lang.Thread.currentThread().interrupt();
            throw new JmapNetworkError("Failed to send JMAP HTTP request", e);
        }
    }

    private static HttpRequest toHttpRequest(JmapHttpRequest request) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(request.getUrl()))
                .method(request.getMethod(), bodyPublisher(request.getBody()));

        // HttpRequest.Builder.headers(String...) throws IllegalArgumentException("wrong
        // number, 0, of parameters") if called with a zero-length array - guard against an
        // empty (not just null) headers map, since JmapHttpRequest's own constructor always
        // normalizes a null map to an empty one, so the null check alone never actually skips
        // this call in practice.
        if (request.getHeaders() != null && !request.getHeaders().isEmpty()) {
            builder.headers(flattenHeaders(request.getHeaders()));
        }

        return builder.build();
    }

    private static HttpRequest.BodyPublisher bodyPublisher(byte[] body) {
        return (body == null || body.length == 0)
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofByteArray(body);
    }

    private static boolean isRedirect(int status) {
        return status == 301 || status == 302 || status == 303 || status == 307 || status == 308;
    }

    private static String firstHeaderValue(Map<String, List<String>> headers, String name) {
        List<String> values = headers.get(name);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    /**
     * Flattens a {@code Map<String, List<String>>} of headers into a {@code String[]}
     * suitable for {@link HttpRequest.Builder#headers(String...)}.
     */
    private static String[] flattenHeaders(Map<String, List<String>> headers) {
        return flattenHeaders(headers, false);
    }

    /**
     * As {@link #flattenHeaders(Map)}, but when {@code stripCredentials} is true the
     * {@code Authorization} / {@code Cookie} / {@code Proxy-Authorization} headers are
     * omitted - used when following a redirect that crosses to a different origin, so the
     * caller's credentials are not sent to another host.
     */
    private static String[] flattenHeaders(Map<String, List<String>> headers, boolean stripCredentials) {
        if (headers == null || headers.isEmpty()) {
            return new String[0];
        }
        List<String> flat = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            if (stripCredentials && isCredentialHeader(key)) {
                continue;
            }
            for (String value : entry.getValue()) {
                flat.add(key);
                flat.add(value);
            }
        }
        return flat.toArray(new String[0]);
    }

    private static boolean isCredentialHeader(String name) {
        return name.equalsIgnoreCase("Authorization")
                || name.equalsIgnoreCase("Cookie")
                || name.equalsIgnoreCase("Proxy-Authorization");
    }

    /** {@code scheme://host[:port]} of a URI, for same-origin comparison. */
    private static String originOf(URI uri) {
        return uri.getScheme() + "://" + uri.getHost() + (uri.getPort() == -1 ? "" : ":" + uri.getPort());
    }
}
