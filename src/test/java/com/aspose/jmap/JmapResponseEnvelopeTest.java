package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JmapResponseEnvelopeTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static Invocation makeInvocation(String name, Map<String, Object> args, String callId) {
        return new Invocation(name, args, callId);
    }

    @Test
    void testFullSerializationRoundTrip() {
        // arrange
        Invocation inv1 = makeInvocation("Mail/get", Map.of("accountId", "a1"), "c1");
        Invocation inv2 = makeInvocation("Mailbox/get", Map.of("accountId", "a1", "ids", List.of("m1")), "c2");
        List<Invocation> methodResponses = List.of(inv1, inv2);
        Map<String, String> createdIds = Map.of("temp1", "real1", "temp2", "real2");
        String sessionState = "state123";

        JmapResponseEnvelope envelope = new JmapResponseEnvelope(methodResponses, createdIds, sessionState);

        // act
        JsonNode json = envelope.toJson();
        JmapResponseEnvelope parsed = JmapResponseEnvelope.fromJson(json);

        // assert
        assertEquals(sessionState, parsed.getSessionState());
        assertEquals(createdIds, parsed.getCreatedIds());

        List<Invocation> parsedResponses = parsed.getMethodResponses();
        assertEquals(2, parsedResponses.size());

        Invocation p1 = parsedResponses.get(0);
        assertEquals(inv1.getName(), p1.getName());
        assertEquals(inv1.getMethodCallId(), p1.getMethodCallId());
        assertEquals(inv1.getArguments(), p1.getArguments());

        Invocation p2 = parsedResponses.get(1);
        assertEquals(inv2.getName(), p2.getName());
        assertEquals(inv2.getMethodCallId(), p2.getMethodCallId());
        assertEquals(inv2.getArguments(), p2.getArguments());
    }

    @Test
    void testDeserializationWithoutCreatedIds() {
        // arrange JSON with only required fields
        ObjectNode root = MAPPER.createObjectNode();

        ArrayNode mrArray = MAPPER.createArrayNode();
        mrArray.add(makeInvocation("Identity/get", Collections.emptyMap(), "c3").toJson());
        root.set("methodResponses", mrArray);

        root.put("sessionState", "sess42");

        // act
        JmapResponseEnvelope envelope = JmapResponseEnvelope.fromJson(root);

        // assert
        assertEquals("sess42", envelope.getSessionState());
        assertNull(envelope.getCreatedIds());
        assertEquals(1, envelope.getMethodResponses().size());
        assertEquals("Identity/get", envelope.getMethodResponses().get(0).getName());
    }

    @Test
    void testFromJsonMissingMethodResponsesThrows() {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("sessionState", "s1");
        // createdIds optional, omitted

        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> JmapResponseEnvelope.fromJson(root));
        assertEquals("InvalidResponseEnvelope", ex.getType());
    }

    @Test
    void testFromJsonMethodResponsesNotArrayThrows() {
        ObjectNode root = MAPPER.createObjectNode();
        root.set("methodResponses", MAPPER.createObjectNode()); // wrong type
        root.put("sessionState", "s2");

        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> JmapResponseEnvelope.fromJson(root));
        assertEquals("InvalidResponseEnvelope", ex.getType());
    }

    @Test
    void testFromJsonCreatedIdsNotObjectThrows() {
        ObjectNode root = MAPPER.createObjectNode();

        ArrayNode mrArray = MAPPER.createArrayNode();
        mrArray.add(makeInvocation("Email/get", Collections.emptyMap(), "c4").toJson());
        root.set("methodResponses", mrArray);

        root.set("createdIds", MAPPER.createArrayNode()); // invalid type
        root.put("sessionState", "s3");

        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> JmapResponseEnvelope.fromJson(root));
        assertEquals("InvalidResponseEnvelope", ex.getType());
    }

    @Test
    void testFromJsonMissingSessionStateThrows() {
        ObjectNode root = MAPPER.createObjectNode();

        ArrayNode mrArray = MAPPER.createArrayNode();
        mrArray.add(makeInvocation("Email/get", Collections.emptyMap(), "c5").toJson());
        root.set("methodResponses", mrArray);
        // sessionState omitted

        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> JmapResponseEnvelope.fromJson(root));
        assertEquals("InvalidResponseEnvelope", ex.getType());
    }
}
