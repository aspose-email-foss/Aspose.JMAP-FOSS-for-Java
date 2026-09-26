package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JmapHttpResponseTest {

    @Test
    void constructorStoresValuesAndDefensiveCopiesHeaders() {
        Map<String, List<String>> mutableHeaders = new HashMap<>();
        mutableHeaders.put("Content-Type", List.of("application/json"));
        mutableHeaders.put("X-Custom", List.of("a", "b"));

        byte[] body = "{\"ok\":true}".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        JmapHttpResponse response = new JmapHttpResponse(200, mutableHeaders, body);

        // Verify stored values
        assertEquals(200, response.getStatusCode());
        assertArrayEquals(body, response.getBody());

        // Verify headers are present and equal
        Map<String, List<String>> respHeaders = response.getHeaders();
        assertEquals(2, respHeaders.size());
        assertEquals(List.of("application/json"), respHeaders.get("Content-Type"));
        assertEquals(List.of("a", "b"), respHeaders.get("X-Custom"));

        // Modify original map after construction; response should be unaffected
        mutableHeaders.put("New-Header", List.of("new"));
        assertFalse(response.getHeaders().containsKey("New-Header"));
    }

    @Test
    void getHeadersReturnsUnmodifiableMap() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Accept", List.of("*/*"));
        JmapHttpResponse response = new JmapHttpResponse(204, headers, null);

        Map<String, List<String>> respHeaders = response.getHeaders();
        assertThrows(UnsupportedOperationException.class, () -> respHeaders.put("Another", List.of("value")));
        assertThrows(UnsupportedOperationException.class, () -> respHeaders.get("Accept").add("text/plain"));
    }

    @Test
    void nullHeadersAndBodyHandledGracefully() {
        JmapHttpResponse response = new JmapHttpResponse(404, null, null);

        assertEquals(404, response.getStatusCode());
        assertNull(response.getBody());

        // Headers map should be non‑null and empty
        assertNotNull(response.getHeaders());
        assertTrue(response.getHeaders().isEmpty());
    }
}
