package com.aspose.jmap;

// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JmapRequestEnvelopeTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static Invocation sampleInvocation() {
        Map<String, Object> args = new HashMap<>();
        args.put("accountId", "user@example.test");
        args.put("ids", List.of("msg1"));
        return new Invocation("Email/get", args, "c1");
    }

    @Test
    void roundTripSerializationWithCreatedIds() {
        List<String> using = List.of("urn:ietf:params:jmap:core", "urn:ietf:params:jmap:mail");
        List<Invocation> calls = List.of(sampleInvocation());
        Map<String, String> createdIds = Map.of("clientId1", "serverId1", "clientId2", "serverId2");

        JmapRequestEnvelope envelope = new JmapRequestEnvelope(using, calls, createdIds);

        JsonNode json = envelope.toJson();
        // verify JSON structure
        assertTrue(json.isObject());
        assertEquals(using.size(), json.get("using").size());
        assertEquals(calls.size(), json.get("methodCalls").size());
        assertTrue(json.hasNonNull("createdIds"));
        assertEquals(createdIds.size(), json.get("createdIds").size());

        JmapRequestEnvelope parsed = JmapRequestEnvelope.fromJson(json);
        assertEquals(using, parsed.getUsing());
        assertEquals(calls.size(), parsed.getMethodCalls().size());
        assertEquals(calls.get(0).getName(), parsed.getMethodCalls().get(0).getName());
        assertEquals(calls.get(0).getMethodCallId(), parsed.getMethodCalls().get(0).getMethodCallId());
        assertEquals(createdIds, parsed.getCreatedIds());
    }

    @Test
    void serializationWhenCreatedIdsIsNull() {
        List<String> using = List.of("urn:ietf:params:jmap:core");
        List<Invocation> calls = List.of(sampleInvocation());

        JmapRequestEnvelope envelope = new JmapRequestEnvelope(using, calls, null);
        JsonNode json = envelope.toJson();

        assertTrue(json.has("createdIds"));
        assertTrue(json.get("createdIds").isNull());

        JmapRequestEnvelope parsed = JmapRequestEnvelope.fromJson(json);
        assertNull(parsed.getCreatedIds());
        assertEquals(using, parsed.getUsing());
        assertEquals(calls.size(), parsed.getMethodCalls().size());
    }

    @Test
    void fromJsonThrowsWhenUsingMissing() {
        ObjectNode root = MAPPER.createObjectNode();
        // omit "using"
        root.set("methodCalls", MAPPER.createArrayNode());
        root.putNull("createdIds");

        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> JmapRequestEnvelope.fromJson(root));
        assertTrue(ex.getMessage().contains("Missing required property 'using'"));
    }

    @Test
    void fromJsonThrowsWhenUsingContainsNonString() {
        ObjectNode root = MAPPER.createObjectNode();
        root.set("using", MAPPER.createArrayNode().add(123)); // non‑textual element
        root.set("methodCalls", MAPPER.createArrayNode());
        root.putNull("createdIds");

        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> JmapRequestEnvelope.fromJson(root));
        assertTrue(ex.getMessage().contains("Invalid value in 'using' array"));
    }

    @Test
    void fromJsonThrowsWhenMethodCallsMissing() {
        ObjectNode root = MAPPER.createObjectNode();
        root.set("using", MAPPER.createArrayNode().add("urn:ietf:params:jmap:core"));
        // omit methodCalls
        root.putNull("createdIds");

        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> JmapRequestEnvelope.fromJson(root));
        assertTrue(ex.getMessage().contains("Missing required property 'methodCalls'"));
    }

    @Test
    void fromJsonHandlesNullCreatedIdsGracefully() {
        ObjectNode root = MAPPER.createObjectNode();
        root.set("using", MAPPER.createArrayNode().add("urn:ietf:params:jmap:core"));
        root.set("methodCalls", MAPPER.createArrayNode()
                .add(sampleInvocation().toJson()));
        root.putNull("createdIds");

        JmapRequestEnvelope envelope = JmapRequestEnvelope.fromJson(root);
        assertNull(envelope.getCreatedIds());
        assertEquals(List.of("urn:ietf:params:jmap:core"), envelope.getUsing());
        assertEquals(1, envelope.getMethodCalls().size());
        assertEquals("Email/get", envelope.getMethodCalls().get(0).getName());
    }
}
