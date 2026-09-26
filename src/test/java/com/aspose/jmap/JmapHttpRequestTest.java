package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class JmapHttpRequestTest {

    @Test
    void createsRequestWithHeadersAndBody() {
        String method = "POST";
        String url = "https://example.com/jmap";
        List<String> values = new ArrayList<>(List.of("value1", "value2"));
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("X-Custom", values);
        byte[] body = "{\"foo\":\"bar\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8);

        JmapHttpRequest request = new JmapHttpRequest(method, url, headers, body);

        assertEquals(method, request.getMethod());
        assertEquals(url, request.getUrl());
        assertArrayEquals(body, request.getBody());

        Map<String, List<String>> reqHeaders = request.getHeaders();
        assertEquals(1, reqHeaders.size());
        assertTrue(reqHeaders.containsKey("X-Custom"));
        assertEquals(List.of("value1", "value2"), reqHeaders.get("X-Custom"));

        // original mutable map modifications must not affect the request
        values.add("value3");
        headers.put("Another", List.of("x"));
        assertEquals(List.of("value1", "value2"), request.getHeaders().get("X-Custom"));
        assertFalse(request.getHeaders().containsKey("Another"));

        // request headers map must be unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> reqHeaders.put("New", List.of("y")));
        assertThrows(UnsupportedOperationException.class, () -> reqHeaders.get("X-Custom").add("z"));
    }

    @Test
    void handlesNullHeadersAndBody() {
        JmapHttpRequest request = new JmapHttpRequest("GET", "https://example.com/", null, null);

        assertEquals("GET", request.getMethod());
        assertEquals("https://example.com/", request.getUrl());
        assertNull(request.getBody());

        Map<String, List<String>> headers = request.getHeaders();
        assertNotNull(headers);
        assertTrue(headers.isEmpty());

        // ensure the returned map is unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> headers.put("X", List.of("Y")));
    }

    @Test
    void rejectsNullMethodOrUrl() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> new JmapHttpRequest(null, "https://example.com/", null, null));
        assertEquals("method must not be null", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> new JmapHttpRequest("GET", null, null, null));
        assertEquals("url must not be null", ex2.getMessage());
    }
}
