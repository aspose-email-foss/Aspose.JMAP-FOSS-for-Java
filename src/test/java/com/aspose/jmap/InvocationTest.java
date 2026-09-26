package com.aspose.jmap;

// No import needed: this test file shares the exact same package as the production code under test
// (see single_package_rule) - every production class/interface is already visible here without importing it.

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class InvocationTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void fromJson_validArray_createsInvocation() {
        // Build a correct JSON array representation
        ArrayNode json = MAPPER.createArrayNode();
        json.add("Mailbox/get");
        ObjectNode args = MAPPER.createObjectNode();
        args.put("accountId", "a1");
        json.add(args);
        json.add("c1");

        Invocation inv = Invocation.fromJson(json);

        assertEquals("Mailbox/get", inv.getName());
        assertEquals("c1", inv.getMethodCallId());
        assertEquals(Map.of("accountId", "a1"), inv.getArguments());
    }

    @Test
    void toJson_producesCorrectArray() {
        // Create an Invocation instance via the package‑visible constructor using reflection
        // (the constructor is package‑visible, so we can call it directly here)
        Invocation inv = new Invocation("Email/get", Map.of("ids", "[]"), "call42");

        JsonNode json = inv.toJson();

        assertTrue(json.isArray(), "Serialized form must be a JSON array");
        assertEquals(3, json.size(), "Array must contain exactly three elements");
        assertEquals("Email/get", json.get(0).asText());
        assertEquals("call42", json.get(2).asText());

        // Verify arguments node round‑trips to the original map
        JsonNode argsNode = json.get(1);
        @SuppressWarnings("unchecked")
        Map<String, Object> argsMap = MAPPER.convertValue(argsNode, Map.class);
        assertEquals(Map.of("ids", "[]"), argsMap);
    }

    @Test
    void fromJson_invalidSize_throwsProtocolError() {
        ArrayNode json = MAPPER.createArrayNode();
        json.add("Foo/bar"); // only one element

        assertThrows(JmapProtocolError.class, () -> Invocation.fromJson(json));
    }

    @Test
    void fromJson_invalidElementTypes_throwsProtocolError() {
        ArrayNode json = MAPPER.createArrayNode();
        json.add(123); // name must be textual
        json.add("notAnObject"); // arguments must be an object
        json.add(true); // methodCallId must be textual

        assertThrows(JmapProtocolError.class, () -> Invocation.fromJson(json));
    }
}
