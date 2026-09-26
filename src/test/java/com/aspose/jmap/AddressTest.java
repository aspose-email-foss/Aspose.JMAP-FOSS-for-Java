package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Unit tests for {@link Address}.
 */
public class AddressTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testSerializationDeserializationWithParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("RET", "HDRS");
        params.put("X-NULL", null);

        Address original = new Address("user@example.com", params);
        // Serialize
        var json = original.toJson();

        // Verify JSON structure
        assertTrue(json.isObject(), "Serialized Address should be a JSON object");
        assertEquals("user@example.com", json.get("email").asText());
        assertTrue(json.has("parameters"), "Parameters field should be present");
        var paramsNode = json.get("parameters");
        assertEquals("HDRS", paramsNode.get("RET").asText());
        assertTrue(paramsNode.get("X-NULL").isNull());

        // Deserialize
        Address parsed = Address.fromJson(json);
        assertEquals(original.getEmail(), parsed.getEmail());
        assertNotNull(parsed.getParameters());
        assertEquals(2, parsed.getParameters().size());
        assertEquals("HDRS", parsed.getParameters().get("RET"));
        assertNull(parsed.getParameters().get("X-NULL"));
    }

    @Test
    void testSerializationDeserializationWithoutParameters() {
        Address original = new Address("no.params@example.com", null);
        var json = original.toJson();

        // Parameters field must be omitted
        assertFalse(json.has("parameters"), "Parameters field should be omitted when null");

        // Deserialize back
        Address parsed = Address.fromJson(json);
        assertEquals(original.getEmail(), parsed.getEmail());
        assertNull(parsed.getParameters(), "Deserialized parameters should be null");
    }

    @Test
    void testFromJsonMissingEmailThrows() {
        ObjectNode node = MAPPER.createObjectNode(); // empty object, no email
        var ex = assertThrows(JmapProtocolError.class, () -> Address.fromJson(node));
        assertEquals("invalidArguments", ex.getType());
        assertTrue(ex.getDescription().contains("email"));
    }

    @Test
    void testFromJsonInvalidEmailTypeThrows() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("email", 123); // numeric instead of string
        var ex = assertThrows(JmapProtocolError.class, () -> Address.fromJson(node));
        assertEquals("invalidArguments", ex.getType());
        assertTrue(ex.getDescription().contains("email"));
    }
}
