package com.aspose.jmap;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.nio.charset.StandardCharsets;

/**
 * Unit tests for {@link JmapClientCore}.
 */
class JmapClientCoreTest {

    private static final String SESSION_URL = "https://example.com/.well-known/jmap";
    private static final String USERNAME = "user@example.com";
    private static final String PASSWORD = "secret";

    /**
     * Helper transport that returns pre‑queued responses and records the requests it receives.
     */
    private static class FakeTransport implements JmapTransport {
        private final Queue<JmapHttpResponse> responses = new ArrayDeque<>();
        private final List<JmapHttpRequest> capturedRequests = new ArrayList<>();

        void queueResponse(JmapHttpResponse response) {
            responses.add(response);
        }

        JmapHttpRequest getLastRequest() {
            if (capturedRequests.isEmpty()) {
                return null;
            }
            return capturedRequests.get(capturedRequests.size() - 1);
        }

        @Override
        public JmapHttpResponse send(JmapHttpRequest request) {
            capturedRequests.add(request);
            JmapHttpResponse resp = responses.poll();
            if (resp == null) {
                throw new IllegalStateException("No queued response for request: " + request);
            }
            return resp;
        }
    }

    /**
     * Builds a minimal valid Session JSON object required by {@link Session#fromJson(JsonNode)}.
     */
    private static JmapHttpResponse makeSessionResponse() {
        String json = """
                {
                  "capabilities": {},
                  "accounts": {},
                  "primaryAccounts": {},
                  "username": "%s",
                  "apiUrl": "/api/",
                  "downloadUrl": "/download/{accountId}/{blobId}/{type}/{name}",
                  "uploadUrl": "/upload/{accountId}",
                  "eventSourceUrl": "/events/",
                  "state": "0"
                }
                """.formatted(USERNAME);
        return new JmapHttpResponse(200, Map.of(), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Builds a JMAP response envelope containing a single successful invocation.
     */
    private static JmapHttpResponse makeEchoSuccessResponse(Map<String, Object> echoed) {
        // Build JSON: {"methodResponses":[["Core/echo", {...}, "c1"]]}
        String argsJson;
        try {
            argsJson = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(echoed);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String json = """
                {
                  "methodResponses": [
                    ["Core/echo", %s, "c1"]
                  ],
                  "sessionState": "0"
                }
                """.formatted(argsJson);
        return new JmapHttpResponse(200, Map.of(), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Builds a JMAP response envelope containing a protocol error.
     */
    private static JmapHttpResponse makeErrorResponse(String type, String description) {
        String json = """
                {
                  "methodResponses": [
                    ["error", {"type":"%s","description":"%s"}, "c1"]
                  ],
                  "sessionState": "0"
                }
                """.formatted(type, description);
        return new JmapHttpResponse(200, Map.of(), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Builds a successful uploadBlob response.
     */
    private static JmapHttpResponse makeUploadResponse(String accountId, String blobId, String type, long size) {
        String json = """
                {
                  "accountId":"%s",
                  "blobId":"%s",
                  "type":"%s",
                  "size":%d
                }
                """.formatted(accountId, blobId, type, size);
        return new JmapHttpResponse(200, Map.of(), json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Builds a successful downloadBlob response with the given raw body bytes.
     */
    private static JmapHttpResponse makeDownloadResponse(byte[] data) {
        return new JmapHttpResponse(200, Map.of(), data);
    }

    @Test
    void connectAndEchoSuccess() {
        FakeTransport transport = new FakeTransport();
        transport.queueResponse(makeSessionResponse());

        JmapClientOptions options = new JmapClientOptions.Builder()
                .sessionUrl(SESSION_URL)
                .username(USERNAME)
                .password(PASSWORD)
                .transport(transport)
                .build();

        JmapClientCore client = new JmapClientCore(options);
        Session session = client.connect();
        assertNotNull(session);
        assertEquals(USERNAME, session.getUsername());

        // Prepare echo response
        Map<String, Object> args = Map.of("hello", true, "high", 5);
        transport.queueResponse(makeEchoSuccessResponse(args));

        Map<String, Object> echoed = client.echo(args);
        assertEquals(args, echoed);

        // Verify the echo request
        JmapHttpRequest echoReq = transport.getLastRequest();
        assertNotNull(echoReq);
        assertEquals("POST", echoReq.getMethod());
        assertEquals("https://example.com/api/", echoReq.getUrl());
        assertTrue(echoReq.getHeaders().containsKey("Authorization"));
        assertEquals(List.of("application/json"), echoReq.getHeaders().get("Content-Type"));
        // Body should contain the method call name and arguments
        String body = new String(echoReq.getBody(), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"Core/echo\""));
        assertTrue(body.contains("\"hello\":true"));
    }

    @Test
    void uploadBlobSuccess() {
        FakeTransport transport = new FakeTransport();
        transport.queueResponse(makeSessionResponse());

        JmapClientOptions options = new JmapClientOptions.Builder()
                .sessionUrl(SESSION_URL)
                .username(USERNAME)
                .password(PASSWORD)
                .transport(transport)
                .build();

        JmapClientCore client = new JmapClientCore(options);
        client.connect();

        // Queue upload response
        transport.queueResponse(makeUploadResponse("a1", "b1", "message/rfc822", 123));

        // Not valid UTF-8 (starts like a JPEG magic number) - a prior version lossily
        // encoded this via `new String(data, UTF_8)` before sending, corrupting real
        // attachment content.
        byte[] data = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0x00, 0x10, 0x4A, 0x46 };
        UploadBlobResponse resp = client.uploadBlob("a1", data, "message/rfc822");
        assertEquals("a1", resp.getAccountId());
        assertEquals("b1", resp.getBlobId());
        assertEquals("message/rfc822", resp.getType());
        assertEquals(123L, resp.getSize());

        // Verify upload request
        JmapHttpRequest uploadReq = transport.getLastRequest();
        assertNotNull(uploadReq);
        assertEquals("POST", uploadReq.getMethod());
        assertEquals("https://example.com/upload/a1", uploadReq.getUrl());
        assertEquals(List.of("message/rfc822"), uploadReq.getHeaders().get("Content-Type"));
        assertArrayEquals(data, uploadReq.getBody());
    }

    @Test
    void uploadBlobPercentEncodesAccountId() {
        // Regression test: accountId is spliced verbatim into a URL path segment. A value
        // containing "/", "?", or "#" must be percent-encoded, or it would rewrite the
        // request path or smuggle extra query parameters into the upload URL.
        FakeTransport transport = new FakeTransport();
        transport.queueResponse(makeSessionResponse());

        JmapClientOptions options = new JmapClientOptions.Builder()
                .sessionUrl(SESSION_URL)
                .username(USERNAME)
                .password(PASSWORD)
                .transport(transport)
                .build();

        JmapClientCore client = new JmapClientCore(options);
        client.connect();

        transport.queueResponse(makeUploadResponse("a/b", "b1", "text/plain", 1));
        client.uploadBlob("a/b", new byte[] { 1 }, "text/plain");

        JmapHttpRequest uploadReq = transport.getLastRequest();
        assertNotNull(uploadReq);
        assertEquals("https://example.com/upload/a%2Fb", uploadReq.getUrl());
    }

    @Test
    void downloadBlobSuccess() {
        FakeTransport transport = new FakeTransport();
        transport.queueResponse(makeSessionResponse());

        JmapClientOptions options = new JmapClientOptions.Builder()
                .sessionUrl(SESSION_URL)
                .username(USERNAME)
                .password(PASSWORD)
                .transport(transport)
                .build();

        JmapClientCore client = new JmapClientCore(options);
        client.connect();

        // Not valid UTF-8 (starts like a JPEG magic number) - a prior version decoded this
        // via `response.getBody().getBytes(UTF_8)` on an already-lossily-decoded String,
        // corrupting real (non-text) attachment content.
        byte[] payload = new byte[] { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0x00, 0x10, 0x4A, 0x46 };
        transport.queueResponse(makeDownloadResponse(payload));

        byte[] result = client.downloadBlob("a1", "b1", "text/plain", "file.txt");
        assertArrayEquals(payload, result);

        JmapHttpRequest dlReq = transport.getLastRequest();
        assertNotNull(dlReq);
        assertEquals("GET", dlReq.getMethod());
        assertEquals("https://example.com/download/a1/b1/text%2Fplain/file.txt", dlReq.getUrl());
        assertTrue(dlReq.getHeaders().containsKey("Authorization"));
    }

    @Test
    void connectUsesBearerTokenWhenProvided() {
        FakeTransport transport = new FakeTransport();
        transport.queueResponse(makeSessionResponse());

        JmapClientOptions options = new JmapClientOptions.Builder()
                .sessionUrl(SESSION_URL)
                .bearerToken("test-oauth-token")
                .transport(transport)
                .build();

        JmapClientCore client = new JmapClientCore(options);
        Session session = client.connect();
        assertNotNull(session);

        JmapHttpRequest connectReq = transport.getLastRequest();
        assertNotNull(connectReq);
        assertEquals(List.of("Bearer test-oauth-token"), connectReq.getHeaders().get("Authorization"));
    }

    @Test
    void connectUsesBasicAuthWhenNoBearerToken() {
        FakeTransport transport = new FakeTransport();
        transport.queueResponse(makeSessionResponse());

        JmapClientOptions options = new JmapClientOptions.Builder()
                .sessionUrl(SESSION_URL)
                .username(USERNAME)
                .password(PASSWORD)
                .transport(transport)
                .build();

        JmapClientCore client = new JmapClientCore(options);
        client.connect();

        String expected = "Basic " + Base64.getEncoder()
                .encodeToString((USERNAME + ":" + PASSWORD).getBytes(StandardCharsets.UTF_8));
        JmapHttpRequest connectReq = transport.getLastRequest();
        assertNotNull(connectReq);
        assertEquals(List.of(expected), connectReq.getHeaders().get("Authorization"));
    }

    @Test
    void echoProtocolErrorThrows() {
        FakeTransport transport = new FakeTransport();
        transport.queueResponse(makeSessionResponse());

        JmapClientOptions options = new JmapClientOptions.Builder()
                .sessionUrl(SESSION_URL)
                .username(USERNAME)
                .password(PASSWORD)
                .transport(transport)
                .build();

        JmapClientCore client = new JmapClientCore(options);
        client.connect();

        transport.queueResponse(makeErrorResponse("unknownMethod", "Method not found"));

        Map<String, Object> args = Map.of("foo", "bar");
        JmapProtocolError ex = assertThrows(JmapProtocolError.class, () -> client.echo(args));
        assertEquals("unknownMethod", ex.getType());
        assertEquals("Method not found", ex.getDescription());
    }
}
