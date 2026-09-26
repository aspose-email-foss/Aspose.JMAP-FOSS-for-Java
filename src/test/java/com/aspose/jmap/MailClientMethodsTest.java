package com.aspose.jmap;

// No import needed: this test file shares the exact same package as the production code under test
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MailClientMethodsTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Builds a real {@link JmapHttpResponse} (a final class - not fakeable via a custom
     * implementation) from a status code and a JSON body.
     */
    private static JmapHttpResponse fakeResponse(int statusCode, JsonNode bodyJson) {
        return new JmapHttpResponse(statusCode, Map.of(), bodyJson.toString().getBytes(StandardCharsets.UTF_8));
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
     * Stub transport that returns a predefined sequence of responses and records the last request.
     */
    private static class FakeTransport implements JmapTransport {
        private final Queue<JmapHttpResponse> responses = new ArrayDeque<>();
        JmapHttpRequest lastRequest;

        FakeTransport(List<JmapHttpResponse> responses) {
            this.responses.addAll(responses);
        }

        @Override
        public JmapHttpResponse send(JmapHttpRequest request) {
            this.lastRequest = request;
            return responses.poll();
        }
    }

    /**
     * Builds a minimal {@code Session} JSON object required for {@link JmapClientCore#connect()}.
     */
    private static JsonNode buildSessionJson() {
        ObjectNode node = MAPPER.createObjectNode();
        node.set("capabilities", MAPPER.createObjectNode());
        ObjectNode account = MAPPER.createObjectNode();
        account.put("name", "user@example.com");
        account.put("isPersonal", true);
        account.put("isReadOnly", false);
        account.set("accountCapabilities", MAPPER.createObjectNode());
        node.set("accounts", MAPPER.createObjectNode().set("u1", account));
        node.set("primaryAccounts", MAPPER.createObjectNode().set("mail", MAPPER.convertValue("u1", JsonNode.class)));
        node.put("username", "user");
        node.put("apiUrl", "https://example.com/jmap");
        node.put("downloadUrl", "https://example.com/download/{accountId}/{blobId}/{type}/{name}");
        node.put("uploadUrl", "https://example.com/upload/{accountId}");
        node.put("eventSourceUrl", "https://example.com/event");
        node.put("state", "0");
        return node;
    }

    /**
     * Wraps a single method invocation into a JMAP response envelope.
     */
    private static JsonNode buildResponseEnvelope(String methodName, JsonNode arguments) {
        ObjectNode envelope = MAPPER.createObjectNode();
        ArrayNode methodResponses = MAPPER.createArrayNode();
        ArrayNode invocation = MAPPER.createArrayNode();
        invocation.add(methodName);
        invocation.add(arguments);
        invocation.add("c1");
        methodResponses.add(invocation);
        envelope.set("methodResponses", methodResponses);
        envelope.put("sessionState", "0");
        return envelope;
    }

    @Test
    void listMailboxes_success() {
        // ----- prepare fake responses -----
        // 1. Session GET response
        JsonNode sessionJson = buildSessionJson();

        // 2. Mailbox/get response
        ObjectNode mailbox = MAPPER.createObjectNode();
        mailbox.put("id", "mb1");
        mailbox.put("name", "Inbox");
        ObjectNode args = MAPPER.createObjectNode();
        args.put("accountId", "u1");
        args.set("list", MAPPER.createArrayNode().add(mailbox));
        JsonNode mailboxResp = buildResponseEnvelope("Mailbox/get", args);

        FakeTransport transport = new FakeTransport(List.of(
                fakeResponse(200, sessionJson),
                fakeResponse(200, mailboxResp)
        ));

        // ----- build client -----
        JmapClientOptions options = new JmapClientOptions.Builder()
                .sessionUrl("https://example.com/.well-known/jmap")
                .username("user")
                .password("pass")
                .transport(transport)
                .build();
        JmapClient client = new JmapClient(options);
        client.connect();

        // ----- invoke method under test -----
        List<Mailbox> mailboxes = client.listMailboxes("u1");

        // ----- assertions on result -----
        assertEquals(1, mailboxes.size());
        Mailbox mb = mailboxes.get(0);
        assertEquals("mb1", mb.getId());
        assertEquals("Inbox", mb.getName());

        // ----- assertions on the HTTP request sent -----
        JmapHttpRequest req = transport.lastRequest;
        assertNotNull(req);
        assertEquals("POST", req.getMethod());
        assertEquals("https://example.com/jmap", req.getUrl());

        JsonNode body = parseBody(req);
        assertNotNull(body);
        assertTrue(body.has("methodCalls"));
        ArrayNode methodCalls = (ArrayNode) body.get("methodCalls");
        assertEquals(1, methodCalls.size());
        ArrayNode call = (ArrayNode) methodCalls.get(0);
        assertEquals("Mailbox/get", call.get(0).asText());
        JsonNode callArgs = call.get(1);
        assertEquals("u1", callArgs.get("accountId").asText());
    }

    @Test
    void fetchMessage_notFound_throwsProtocolError() {
        // ----- prepare fake responses -----
        JsonNode sessionJson = buildSessionJson();

        // Email/get response with empty list
        ObjectNode args = MAPPER.createObjectNode();
        args.put("accountId", "u1");
        args.set("list", MAPPER.createArrayNode()); // empty list
        JsonNode emailResp = buildResponseEnvelope("Email/get", args);

        FakeTransport transport = new FakeTransport(List.of(
                fakeResponse(200, sessionJson),
                fakeResponse(200, emailResp)
        ));

        JmapClientOptions options = new JmapClientOptions.Builder()
                .sessionUrl("https://example.com/.well-known/jmap")
                .username("user")
                .password("pass")
                .transport(transport)
                .build();
        JmapClient client = new JmapClient(options);
        client.connect();

        // ----- invoke and expect exception -----
        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> client.fetchMessage("u1", "e123", null));
        assertEquals("notFound", ex.getType());
        assertEquals("Email not found", ex.getDescription());
    }

    @Test
    void createMailbox_mergesServerFields() {
        // ----- prepare fake responses -----
        JsonNode sessionJson = buildSessionJson();

        // Server response for Mailbox/set (create)
        ObjectNode serverCreated = MAPPER.createObjectNode();
        serverCreated.put("id", "mb123");
        serverCreated.put("name", "NewBox");
        ObjectNode createdMap = MAPPER.createObjectNode();
        createdMap.set("new", serverCreated);
        ObjectNode args = MAPPER.createObjectNode();
        args.put("accountId", "u1");
        args.set("created", createdMap);
        JsonNode setResp = buildResponseEnvelope("Mailbox/set", args);

        FakeTransport transport = new FakeTransport(List.of(
                fakeResponse(200, sessionJson),
                fakeResponse(200, setResp)
        ));

        JmapClientOptions options = new JmapClientOptions.Builder()
                .sessionUrl("https://example.com/.well-known/jmap")
                .username("user")
                .password("pass")
                .transport(transport)
                .build();
        JmapClient client = new JmapClient(options);
        client.connect();

        // ----- client-side mailbox (no id) -----
        Mailbox toCreate = new Mailbox(
                null,               // id
                "NewBox",           // name
                null,               // parentId
                null,               // role
                0L,                 // sortOrder
                null, null, null, null,
                null,               // myRights
                false               // isSubscribed
        );

        // ----- invoke createMailbox -----
        Mailbox created = client.createMailbox("u1", toCreate);

        // ----- assertions -----
        assertNotNull(created);
        assertEquals("mb123", created.getId());
        assertEquals("NewBox", created.getName());

        // Verify request payload contains the client‑side mailbox JSON under "create"
        JmapHttpRequest req = transport.lastRequest;
        assertNotNull(req);
        JsonNode body = parseBody(req);
        ArrayNode methodCalls = (ArrayNode) body.get("methodCalls");
        ArrayNode call = (ArrayNode) methodCalls.get(0);
        assertEquals("Mailbox/set", call.get(0).asText());
        JsonNode callArgs = call.get(1);
        assertTrue(callArgs.has("create"));
        JsonNode createMap = callArgs.get("create");
        assertTrue(createMap.has("new"));
        JsonNode clientMailboxJson = createMap.get("new");
        assertEquals("NewBox", clientMailboxJson.get("name").asText());
    }

    @Test
    void listMessages_success() {
        // ----- prepare fake responses -----
        JsonNode sessionJson = buildSessionJson();

        // Email/query response with all 7 fields populated
        ObjectNode args = MAPPER.createObjectNode();
        args.put("accountId", "u1");
        args.put("queryState", "qs1");
        args.put("canCalculateChanges", true);
        args.put("position", 0);
        args.set("ids", MAPPER.createArrayNode().add("e1").add("e2"));
        args.put("total", 42);
        args.put("limit", 10);
        JsonNode queryResp = buildResponseEnvelope("Email/query", args);

        FakeTransport transport = new FakeTransport(List.of(
                fakeResponse(200, sessionJson),
                fakeResponse(200, queryResp)
        ));

        // ----- build client -----
        JmapClientOptions options = new JmapClientOptions.Builder()
                .sessionUrl("https://example.com/.well-known/jmap")
                .username("user")
                .password("pass")
                .transport(transport)
                .build();
        JmapClient client = new JmapClient(options);
        client.connect();

        // ----- invoke method under test -----
        EmailQueryResponse result = client.listMessages("u1");

        // ----- assertions on result -----
        assertEquals("u1", result.getAccountId());
        assertEquals("qs1", result.getQueryState());
        assertTrue(result.isCanCalculateChanges());
        assertEquals(0, result.getPosition());
        assertEquals(List.of("e1", "e2"), result.getIds());
        assertEquals(42L, result.getTotal());
        assertEquals(10, result.getLimit());

        // ----- assertions on the HTTP request sent -----
        JmapHttpRequest req = transport.lastRequest;
        assertNotNull(req);
        JsonNode body = parseBody(req);
        ArrayNode methodCalls = (ArrayNode) body.get("methodCalls");
        ArrayNode call = (ArrayNode) methodCalls.get(0);
        assertEquals("Email/query", call.get(0).asText());
        JsonNode callArgs = call.get(1);
        assertEquals("u1", callArgs.get("accountId").asText());
        assertEquals(0, callArgs.get("position").asInt());
        assertFalse(callArgs.has("filter"));
        assertFalse(callArgs.has("sort"));
        assertFalse(callArgs.has("limit"));
    }

    @Test
    void listMessages_withFilterSortLimitPosition_success() {
        // ----- prepare fake responses -----
        JsonNode sessionJson = buildSessionJson();

        ObjectNode args = MAPPER.createObjectNode();
        args.put("accountId", "u1");
        args.put("queryState", "qs2");
        args.put("canCalculateChanges", false);
        args.put("position", 5);
        args.set("ids", MAPPER.createArrayNode().add("e3"));
        // total and limit intentionally omitted (optional fields, absent from response)
        JsonNode queryResp = buildResponseEnvelope("Email/query", args);

        FakeTransport transport = new FakeTransport(List.of(
                fakeResponse(200, sessionJson),
                fakeResponse(200, queryResp)
        ));

        JmapClientOptions options = new JmapClientOptions.Builder()
                .sessionUrl("https://example.com/.well-known/jmap")
                .username("user")
                .password("pass")
                .transport(transport)
                .build();
        JmapClient client = new JmapClient(options);
        client.connect();

        // ----- invoke method under test with all optional args populated -----
        Map<String, Object> filter = Map.of("inMailbox", "mb1");
        List<Comparator> sort = List.of(new Comparator("receivedAt", false, null));
        EmailQueryResponse result = client.listMessages("u1", filter, sort, 25, 5);

        // ----- assertions on result -----
        assertEquals("u1", result.getAccountId());
        assertEquals("qs2", result.getQueryState());
        assertFalse(result.isCanCalculateChanges());
        assertEquals(5, result.getPosition());
        assertEquals(List.of("e3"), result.getIds());
        assertNull(result.getTotal());
        assertNull(result.getLimit());

        // ----- assertions on the HTTP request sent -----
        JmapHttpRequest req = transport.lastRequest;
        JsonNode body = parseBody(req);
        ArrayNode methodCalls = (ArrayNode) body.get("methodCalls");
        ArrayNode call = (ArrayNode) methodCalls.get(0);
        JsonNode callArgs = call.get(1);
        assertEquals("mb1", callArgs.get("filter").get("inMailbox").asText());
        assertEquals(1, callArgs.get("sort").size());
        assertEquals("receivedAt", callArgs.get("sort").get(0).get("property").asText());
        assertFalse(callArgs.get("sort").get(0).get("isAscending").asBoolean());
        assertEquals(25, callArgs.get("limit").asInt());
        assertEquals(5, callArgs.get("position").asInt());
    }

    @Test
    void listIdentities_success() {
        // ----- prepare fake responses -----
        JsonNode sessionJson = buildSessionJson();

        // Identity/get response with one identity
        ObjectNode identity = MAPPER.createObjectNode();
        identity.put("id", "id1");
        identity.put("email", "me@example.com");
        identity.put("name", "Me");
        ObjectNode args = MAPPER.createObjectNode();
        args.put("accountId", "u1");
        args.set("list", MAPPER.createArrayNode().add(identity));
        JsonNode identityResp = buildResponseEnvelope("Identity/get", args);

        FakeTransport transport = new FakeTransport(List.of(
                fakeResponse(200, sessionJson),
                fakeResponse(200, identityResp)
        ));

        JmapClientOptions options = new JmapClientOptions.Builder()
                .sessionUrl("https://example.com/.well-known/jmap")
                .username("user")
                .password("pass")
                .transport(transport)
                .build();
        JmapClient client = new JmapClient(options);
        client.connect();

        // ----- invoke method under test -----
        List<Identity> ids = client.listIdentities("u1");

        // ----- assertions -----
        assertEquals(1, ids.size());
        Identity id = ids.get(0);
        assertEquals("id1", id.getId());
        assertEquals("me@example.com", id.getEmail());
        assertEquals("Me", id.getName());

        // Verify request method name
        JmapHttpRequest req = transport.lastRequest;
        JsonNode body = parseBody(req);
        ArrayNode methodCalls = (ArrayNode) body.get("methodCalls");
        ArrayNode call = (ArrayNode) methodCalls.get(0);
        assertEquals("Identity/get", call.get(0).asText());
    }
}
