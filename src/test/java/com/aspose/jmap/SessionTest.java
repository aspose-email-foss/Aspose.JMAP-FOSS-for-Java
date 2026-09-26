package com.aspose.jmap;

// No import needed: this test file shares the exact same package as the production code under test
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Unit tests for {@link Session} and its nested {@link Session.CoreCapability}.
 */
class SessionTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Creates a minimal but complete Session JSON object containing all required fields.
     */
    private static ObjectNode createFullSessionJson() {
        ObjectNode root = MAPPER.createObjectNode();

        // capabilities – include a CoreCapability entry for realism
        ObjectNode capabilitiesNode = MAPPER.createObjectNode();
        ObjectNode coreCapNode = MAPPER.createObjectNode();
        coreCapNode.put("maxSizeUpload", 10L);
        coreCapNode.put("maxConcurrentUpload", 2L);
        coreCapNode.put("maxSizeRequest", 20L);
        coreCapNode.put("maxConcurrentRequests", 3L);
        coreCapNode.put("maxCallsInRequest", 4L);
        coreCapNode.put("maxObjectsInGet", 5L);
        coreCapNode.put("maxObjectsInSet", 6L);
        coreCapNode.set("collationAlgorithms", MAPPER.valueToTree(List.of("i;unicode-casemap")));
        capabilitiesNode.set("urn:ietf:params:jmap:core", coreCapNode);
        root.set("capabilities", capabilitiesNode);

        // accounts – one dummy account
        ObjectNode accountsNode = MAPPER.createObjectNode();
        ObjectNode accountNode = MAPPER.createObjectNode();
        accountNode.put("name", "test@example.com");
        accountNode.put("isPersonal", true);
        accountNode.put("isReadOnly", false);
        accountNode.set("accountCapabilities", MAPPER.createObjectNode());
        accountsNode.set("acc123", accountNode);
        root.set("accounts", accountsNode);

        // primaryAccounts – map capability URN to account id
        root.set("primaryAccounts", MAPPER.valueToTree(Map.of("urn:ietf:params:jmap:mail", "acc123")));

        // required scalar fields
        root.put("username", "user@example.com");
        root.put("apiUrl", "/jmap/");
        root.put("downloadUrl", "/download/{accountId}/{blobId}/{type}/{name}");
        root.put("uploadUrl", "/upload/{accountId}");
        root.put("eventSourceUrl", "/events/{accountId}");
        root.put("state", "state123");

        return root;
    }

    @Test
    void fromJson_and_toJson_roundTrip() {
        ObjectNode json = createFullSessionJson();

        Session session = Session.fromJson(json);
        assertNotNull(session);
        assertEquals("user@example.com", session.getUsername());
        assertEquals("/jmap/", session.getApiUrl());
        assertEquals("/download/{accountId}/{blobId}/{type}/{name}", session.getDownloadUrl());
        assertEquals("/upload/{accountId}", session.getUploadUrl());
        assertEquals("/events/{accountId}", session.getEventSourceUrl());
        assertEquals("state123", session.getState());

        // accounts map contains the dummy account
        assertTrue(session.getAccounts().containsKey("acc123"));
        Account acc = session.getAccounts().get("acc123");
        assertEquals("test@example.com", acc.getName());
        assertTrue(acc.isPersonal());
        assertFalse(acc.isReadOnly());

        // capabilities map contains the raw core capability data (Session.capabilities is
        // Map<String, Object>, generically converted - it is NOT parsed into a typed
        // CoreCapability per entry; use the standalone top-level CoreCapability.fromJson()
        // separately if typed access is needed, as CoreCapabilityTest does).
        Object coreCapObj = session.getCapabilities().get("urn:ietf:params:jmap:core");
        assertNotNull(coreCapObj);
        assertTrue(coreCapObj instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> coreCap = (Map<String, Object>) coreCapObj;
        assertEquals(10, ((Number) coreCap.get("maxSizeUpload")).longValue());
        assertEquals(2, ((Number) coreCap.get("maxConcurrentUpload")).longValue());

        // round‑trip back to JSON and compare structural equality (via Map, not JsonNode
        // directly - guaranteed order-independent per the JDK Map.equals contract)
        JsonNode roundTrip = session.toJson();
        assertEquals(
                MAPPER.convertValue(json, Map.class),
                MAPPER.convertValue(roundTrip, Map.class));
    }

    @Test
    void fromJson_missingRequiredField_throwsProtocolError() {
        ObjectNode json = createFullSessionJson();
        json.remove("username"); // required field

        JmapProtocolError ex = assertThrows(JmapProtocolError.class, () -> Session.fromJson(json));
        assertTrue(ex.getMessage().contains("username"));
    }

    @Test
    void coreCapability_fromJson_and_toJson_roundTrip() {
        // Build a CoreCapability JSON node directly
        ObjectNode node = MAPPER.createObjectNode();
        node.put("maxSizeUpload", 123L);
        node.put("maxConcurrentUpload", 4L);
        node.put("maxSizeRequest", 567L);
        node.put("maxConcurrentRequests", 8L);
        node.put("maxCallsInRequest", 2L);
        node.put("maxObjectsInGet", 10L);
        node.put("maxObjectsInSet", 20L);
        node.set("collationAlgorithms", MAPPER.valueToTree(List.of("i;unicode-casemap", "i;ascii-casemap")));

        // CoreCapability is its own top-level class/file (per subtype_own_file_rule), not
        // nested inside Session.
        CoreCapability cap = CoreCapability.fromJson(node);
        assertNotNull(cap);
        assertEquals(123L, cap.getMaxSizeUpload());
        assertEquals(4L, cap.getMaxConcurrentUpload());
        assertEquals(List.of("i;unicode-casemap", "i;ascii-casemap"), cap.getCollationAlgorithms());

        JsonNode back = cap.toJson();
        assertEquals(
                MAPPER.convertValue(node, Map.class),
                MAPPER.convertValue(back, Map.class));
    }
}
