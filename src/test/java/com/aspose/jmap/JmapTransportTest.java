package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

public class JmapTransportTest {

    @org.junit.jupiter.api.Test
    void testSendReturnsProvidedResponseAndCapturesRequest() {
        // Prepare request with headers and no body
        java.util.Map<String, java.util.List<String>> headers = java.util.Map.of(
                "Accept", java.util.List.of("application/json")
        );
        JmapHttpRequest request = new JmapHttpRequest(
                "GET",
                "https://example.com/api",
                headers,
                null
        );

        // Prepare a canned response
        java.util.Map<String, java.util.List<String>> respHeaders = java.util.Map.of(
                "Content-Type", java.util.List.of("text/plain")
        );
        JmapHttpResponse cannedResponse = new JmapHttpResponse(
                200,
                respHeaders,
                "OK".getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        // Use the fake transport
        FakeTransport transport = new FakeTransport(cannedResponse);
        JmapHttpResponse result = transport.send(request);

        // Verify the response is exactly the canned one
        org.junit.jupiter.api.Assertions.assertEquals(200, result.getStatusCode());
        org.junit.jupiter.api.Assertions.assertArrayEquals("OK".getBytes(java.nio.charset.StandardCharsets.UTF_8), result.getBody());
        org.junit.jupiter.api.Assertions.assertEquals(respHeaders, result.getHeaders());

        // Verify the request was captured correctly
        JmapHttpRequest captured = transport.getCapturedRequest();
        org.junit.jupiter.api.Assertions.assertEquals(request, captured);
        org.junit.jupiter.api.Assertions.assertEquals("GET", captured.getMethod());
        org.junit.jupiter.api.Assertions.assertEquals("https://example.com/api", captured.getUrl());
        org.junit.jupiter.api.Assertions.assertEquals(headers, captured.getHeaders());
        org.junit.jupiter.api.Assertions.assertNull(captured.getBody());
    }

    @org.junit.jupiter.api.Test
    void testSendHandlesNullHeadersAndBody() {
        // Request with null headers and null body
        JmapHttpRequest request = new JmapHttpRequest(
                "POST",
                "https://example.com/empty",
                null,
                null
        );

        // Canned response with no headers and null body
        JmapHttpResponse cannedResponse = new JmapHttpResponse(
                204,
                null,
                null
        );

        FakeTransport transport = new FakeTransport(cannedResponse);
        JmapHttpResponse result = transport.send(request);

        // Verify status code and null body
        org.junit.jupiter.api.Assertions.assertEquals(204, result.getStatusCode());
        org.junit.jupiter.api.Assertions.assertNull(result.getBody());

        // Verify headers map is empty (not null) per JmapHttpResponse contract
        org.junit.jupiter.api.Assertions.assertTrue(result.getHeaders().isEmpty());

        // Verify captured request reflects null headers/body handling
        JmapHttpRequest captured = transport.getCapturedRequest();
        org.junit.jupiter.api.Assertions.assertTrue(captured.getHeaders().isEmpty());
        org.junit.jupiter.api.Assertions.assertNull(captured.getBody());
    }

    /**
     * Simple fake transport that records the request it receives and returns a
     * pre‑configured response. Used to verify that {@link JmapTransport#send}
     * is called with the expected {@link JmapHttpRequest} and that the response
     * is propagated unchanged.
     */
    private static class FakeTransport implements JmapTransport {
        private final JmapHttpResponse responseToReturn;
        private JmapHttpRequest capturedRequest;

        FakeTransport(JmapHttpResponse responseToReturn) {
            this.responseToReturn = responseToReturn;
        }

        @Override
        public JmapHttpResponse send(JmapHttpRequest request) {
            this.capturedRequest = request;
            return responseToReturn;
        }

        JmapHttpRequest getCapturedRequest() {
            return capturedRequest;
        }
    }
}
