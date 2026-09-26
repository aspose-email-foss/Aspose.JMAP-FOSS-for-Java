package com.aspose.jmap;

/**
 * Transport seam for JMAP HTTP communication.
 *
 * <p>Implementations convert a {@link JmapHttpRequest} into a low‑level HTTP request,
 * execute it, and return a {@link JmapHttpResponse}. Implementations must not declare
 * any checked exceptions; any network‑level failure must be wrapped in a
 * {@link JmapNetworkError} (unchecked).</p>
 */
public interface JmapTransport {
    /**
     * Sends the given JMAP HTTP request and returns the response.
     *
     * @param request the JMAP request to send; must not be {@code null}
     * @return the HTTP response received from the server
     * @throws JmapNetworkError if a network or I/O error occurs
     */
    JmapHttpResponse send(JmapHttpRequest request);
}
