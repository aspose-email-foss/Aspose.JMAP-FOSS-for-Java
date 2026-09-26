package com.aspose.jmap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents a low‑level HTTP request used by {@link JmapTransport}.
 * <p>
 * Instances are immutable. The {@code method} is the HTTP verb (e.g. "POST").
 * The {@code url} is the absolute request URL.
 * The {@code headers} map is unmodifiable; each header may have multiple values.
 * The {@code body} may be {@code null} for methods without a payload. It is raw bytes,
 * not a {@code String}: a {@code String} body would force every caller - including blob
 * uploads, which are not text - through a lossy UTF-8 round trip. JMAP method-call bodies
 * are UTF-8-encoded JSON text; blob uploads are the blob's raw bytes verbatim.
 * </p>
 */
public final class JmapHttpRequest {
    private final String method;
    private final String url;
    private final Map<String, List<String>> headers;
    private final byte[] body;

    /**
     * Constructs a new {@code JmapHttpRequest}.
     *
     * @param method  HTTP method, e.g. "POST"
     * @param url     absolute request URL
     * @param headers request headers; the map and its lists are defensively copied and made unmodifiable
     * @param body    request body, may be {@code null}
     */
    JmapHttpRequest(String method, String url, Map<String, List<String>> headers, byte[] body) {
        if (method == null) {
            throw new IllegalArgumentException("method must not be null");
        }
        if (url == null) {
            throw new IllegalArgumentException("url must not be null");
        }
        this.method = method;
        this.url = url;

        if (headers != null) {
            Map<String, List<String>> copy = new java.util.HashMap<>();
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                copy.put(entry.getKey(), List.copyOf(entry.getValue()));
            }
            this.headers = Collections.unmodifiableMap(copy);
        } else {
            this.headers = Collections.emptyMap();
        }

        this.body = body;
    }

    /** Returns the HTTP method. */
    public String getMethod() {
        return method;
    }

    /** Returns the request URL. */
    public String getUrl() {
        return url;
    }

    /** Returns an unmodifiable view of the request headers. */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /** Returns the request body, or {@code null} if none. */
    public byte[] getBody() {
        return body;
    }
}
