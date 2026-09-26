package com.aspose.jmap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents a low‑level HTTP response returned by a {@link JmapTransport}.
 * <p>
 * This class is immutable and contains the HTTP status code, response headers,
 * and the raw response body bytes (typically JSON text, but a blob download's body is
 * arbitrary binary data - see the note on {@link JmapHttpRequest}'s body). It does not
 * interpret the body as JSON; callers may parse it as needed.
 */
public final class JmapHttpResponse {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final byte[] body;

    /**
     * Constructs a {@code JmapHttpResponse}.
     *
     * @param statusCode the HTTP status code (e.g., 200, 404)
     * @param headers    the response headers; a defensive copy is stored
     * @param body       the response body; may be {@code null}
     */
    public JmapHttpResponse(int statusCode, Map<String, List<String>> headers, byte[] body) {
        this.statusCode = statusCode;
        // Defensive copy to guarantee immutability - Collections.unmodifiableMap alone only
        // wraps the caller's map in a read-only VIEW; it does not copy, so a mutation the
        // caller makes to their own map after construction would still show through here.
        this.headers = headers == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new java.util.HashMap<>(headers));
        this.body = body;
    }

    /**
     * Returns the HTTP status code of the response.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns an unmodifiable view of the response headers.
     *
     * @return the headers map, never {@code null}
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * Returns the raw response body bytes.
     *
     * @return the body, or {@code null} if none was received
     */
    public byte[] getBody() {
        return body;
    }
}
