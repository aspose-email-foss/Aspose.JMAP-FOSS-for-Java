package com.aspose.jmap;

// No import needed: this test file shares the exact same package as the production code under test

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Unit tests for {@link SubmissionClientMethods} via the fully composed {@link JmapClient}.
 */
class SubmissionClientMethodsTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Fake transport that returns pre‑queued responses and records the last request sent.
     */
    private static class FakeTransport implements JmapTransport {
        private final Queue<JmapHttpResponse> queue = new ArrayDeque<>();
        private JmapHttpRequest lastRequest;

        void enqueue(JmapHttpResponse resp) {
            queue.add(resp);
        }

        JmapHttpRequest getLastRequest() {
            return lastRequest;
        }

        @Override
        public JmapHttpResponse send(JmapHttpRequest request) {
            this.lastRequest = request;
            JmapHttpResponse resp = queue.poll();
            if (resp == null) {
                throw new IllegalStateException("No response queued for request");
            }
            return resp;
        }
    }

    /**
     * Helper to build a minimal successful {@link JmapHttpResponse}.
     */
    private static JmapHttpResponse okResponse(JsonNode body) {
        // The concrete JmapHttpResponse class has a public constructor
        // (int statusCode, Map<String, List<String>> headers, byte[] body)
        return new JmapHttpResponse(200, Collections.emptyMap(), body.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Parses a captured {@link JmapHttpRequest}'s body (JSON bytes) back into a
     * {@link JsonNode} for assertions.
     */
    private static JsonNode parseBody(JmapHttpRequest req) {
        try {
            return MAPPER.readTree(req.getBody());
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Builds a full Session JSON object required for {@link JmapClient#connect()}.
     */
    private static JsonNode buildSessionJson() {
        ObjectNode node = MAPPER.createObjectNode();
        node.set("capabilities", MAPPER.createObjectNode());
        node.set("accounts", MAPPER.createObjectNode());
        node.set("primaryAccounts", MAPPER.createObjectNode());
        node.put("username", "user@example.test");
        node.put("apiUrl", "https://example.com/api");
        node.put("downloadUrl", "https://example.com/download/{accountId}/{blobId}/{type}/{name}");
        node.put("uploadUrl", "https://example.com/upload/{accountId}");
        node.put("eventSourceUrl", "https://example.com/eventsource");
        node.put("state", "sessionState");
        return node;
    }

    @Test
    void send_successful() {
        FakeTransport transport = new FakeTransport();

        // 1. session response
        transport.enqueue(okResponse(buildSessionJson()));

        // 2. EmailSubmission/set response
        ObjectNode created = MAPPER.createObjectNode();
        created.putObject("c1")
                .put("id", "s123")
                .put("sendAt", "2026-08-18T10:00:00Z")
                .put("undoStatus", "final");

        ArrayNode methodResp = MAPPER.createArrayNode();
        methodResp.add("EmailSubmission/set");
        ObjectNode args = MAPPER.createObjectNode();
        args.put("accountId", "a1");
        args.put("newState", "1");
        args.set("created", created);
        methodResp.add(args);
        methodResp.add("c1");

        ObjectNode envelope = MAPPER.createObjectNode();
        envelope.set("methodResponses", MAPPER.createArrayNode().add(methodResp));
        envelope.put("sessionState", "sessionState");

        transport.enqueue(okResponse(envelope));

        JmapClientOptions options = new JmapClientOptions.Builder()
                .sessionUrl("https://example.com/.well-known/jmap")
                .username("user@example.test")
                .password("secret")
                .transport(transport)
                .build();

        JmapClient client = new JmapClient(options);
        client.connect();

        // construct a minimal EmailSubmission (no id)
        EmailSubmission submission = new EmailSubmission(
                null,
                "id1",
                "emailId",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        EmailSubmission result = client.send("a1", submission, null);
        assertNotNull(result);
        assertEquals("s123", result.getId());

        // verify the request sent for the send() call
        JmapHttpRequest req = transport.getLastRequest();
        assertEquals("POST", req.getMethod());
        assertEquals("https://example.com/api", req.getUrl());

        JsonNode body = parseBody(req);
        assertTrue(body.isObject());
        JsonNode methodCalls = body.get("methodCalls");
        assertTrue(methodCalls.isArray());
        JsonNode inv = methodCalls.get(0);
        assertEquals("EmailSubmission/set", inv.get(0).asText());

        JsonNode invArgs = inv.get(1);
        assertEquals("a1", invArgs.get("accountId").asText());
        assertTrue(invArgs.has("create"));
        JsonNode createMap = invArgs.get("create");
        assertTrue(createMap.has("c1"));
    }

    @Test
    void cancelSend_successful() {
        FakeTransport transport = new FakeTransport();
        transport.enqueue(okResponse(buildSessionJson()));

        // response envelope for cancel (no created/updated needed)
        ArrayNode methodResp = MAPPER.createArrayNode()
                .add("EmailSubmission/set")
                .add(MAPPER.createObjectNode()
                        .put("accountId", "a1")
                        .put("newState", "2"))
                .add("c1");
        ObjectNode envelope = MAPPER.createObjectNode();
        envelope.set("methodResponses", MAPPER.createArrayNode().add(methodResp));
        envelope.put("sessionState", "sessionState");
        transport.enqueue(okResponse(envelope));

        JmapClientOptions opts = new JmapClientOptions.Builder()
                .sessionUrl("https://example.com/.well-known/jmap")
                .username("u")
                .password("p")
                .transport(transport)
                .build();

        JmapClient client = new JmapClient(opts);
        client.connect();

        client.cancelSend("a1", "s123");

        JmapHttpRequest req = transport.getLastRequest();
        assertEquals("POST", req.getMethod());
        JsonNode args = parseBody(req).get("methodCalls").get(0).get(1);
        assertEquals("a1", args.get("accountId").asText());
        assertTrue(args.has("update"));
        JsonNode update = args.get("update");
        assertTrue(update.has("s123"));
        JsonNode patch = update.get("s123");
        // Regression test: a PatchObject key is a JSON Pointer (RFC 6901) relative to the
        // patched object - a bare top-level property name has no leading slash. A prior
        // version sent "/undoStatus" (pointing at a differently-named property instead),
        // which a real JMAP server rejects/ignores, silently breaking cancel-send.
        assertEquals("canceled", patch.get("undoStatus").asText());
        assertTrue(!patch.has("/undoStatus"));
    }

    @Test
    void listSubmissions_empty() {
        FakeTransport transport = new FakeTransport();
        transport.enqueue(okResponse(buildSessionJson()));

        // empty list response
        ObjectNode resultObj = MAPPER.createObjectNode();
        resultObj.put("accountId", "a1");
        resultObj.put("state", "st");
        resultObj.set("list", MAPPER.createArrayNode());

        ArrayNode methodResp = MAPPER.createArrayNode()
                .add("EmailSubmission/get")
                .add(resultObj)
                .add("c1");

        ObjectNode envelope = MAPPER.createObjectNode();
        envelope.set("methodResponses", MAPPER.createArrayNode().add(methodResp));
        envelope.put("sessionState", "sessionState");
        transport.enqueue(okResponse(envelope));

        JmapClientOptions opts = new JmapClientOptions.Builder()
                .sessionUrl("https://example.com/.well-known/jmap")
                .username("u")
                .password("p")
                .transport(transport)
                .build();

        JmapClient client = new JmapClient(opts);
        client.connect();

        List<EmailSubmission> list = client.listSubmissions("a1");
        assertNotNull(list);
        assertTrue(list.isEmpty());

        JmapHttpRequest req = transport.getLastRequest();
        JsonNode args = parseBody(req).get("methodCalls").get(0).get(1);
        assertEquals("a1", args.get("accountId").asText());
    }

    @Test
    void listSubmissions_protocolError() {
        FakeTransport transport = new FakeTransport();
        transport.enqueue(okResponse(buildSessionJson()));

        // error invocation
        ObjectNode errArgs = MAPPER.createObjectNode()
                .put("type", "invalidArguments")
                .put("description", "bad request");
        ArrayNode methodResp = MAPPER.createArrayNode()
                .add("error")
                .add(errArgs)
                .add("c1");

        ObjectNode envelope = MAPPER.createObjectNode();
        envelope.set("methodResponses", MAPPER.createArrayNode().add(methodResp));
        envelope.put("sessionState", "sessionState");
        transport.enqueue(okResponse(envelope));

        JmapClientOptions opts = new JmapClientOptions.Builder()
                .sessionUrl("https://example.com/.well-known/jmap")
                .username("u")
                .password("p")
                .transport(transport)
                .build();

        JmapClient client = new JmapClient(opts);
        client.connect();

        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> client.listSubmissions("a1"));
        assertEquals("invalidArguments", ex.getType());
        assertEquals("bad request", ex.getDescription());
    }
}
