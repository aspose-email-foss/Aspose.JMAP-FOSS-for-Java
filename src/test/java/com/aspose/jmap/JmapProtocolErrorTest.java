package com.aspose.jmap;

// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Unit tests for {@link JmapProtocolError}.
 */
public class JmapProtocolErrorTest {

    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;

    @Test
    void testSerializationWithDescription() {
        JmapProtocolError error = new JmapProtocolError("unknownMethod", "Method not found");
        assertEquals("unknownMethod", error.getType());
        assertEquals("Method not found", error.getDescription());

        ObjectNode json = (ObjectNode) error.toJson();
        assertEquals("unknownMethod", json.get("type").asText());
        assertTrue(json.has("description"));
        assertEquals("Method not found", json.get("description").asText());

        // round‑trip
        JmapProtocolError parsed = JmapProtocolError.fromJson(json);
        assertEquals(error.getType(), parsed.getType());
        assertEquals(error.getDescription(), parsed.getDescription());
    }

    @Test
    void testSerializationWithoutDescription() {
        JmapProtocolError error = new JmapProtocolError("invalidArguments", null);
        assertEquals("invalidArguments", error.getType());
        assertNull(error.getDescription());

        ObjectNode json = (ObjectNode) error.toJson();
        assertEquals("invalidArguments", json.get("type").asText());
        assertFalse(json.has("description"));

        // round‑trip with missing description field
        JmapProtocolError parsed = JmapProtocolError.fromJson(json);
        assertEquals(error.getType(), parsed.getType());
        assertNull(parsed.getDescription());
    }

    @Test
    void testFromJsonMissingTypeThrows() {
        ObjectNode invalid = FACTORY.objectNode(); // empty object, no "type"
        JmapProtocolError thrown = assertThrows(
                JmapProtocolError.class,
                () -> JmapProtocolError.fromJson(invalid)
        );
        assertEquals("invalidError", thrown.getType());
        assertTrue(thrown.getMessage().contains("Missing required field 'type'"));
    }

    @Test
    void testFromJsonNonObjectThrows() {
        // Passing a non‑object node (e.g., a textual node) should also result in a protocol error.
        assertThrows(
                JmapProtocolError.class,
                () -> JmapProtocolError.fromJson(FACTORY.textNode("not an object"))
        );
    }
}
