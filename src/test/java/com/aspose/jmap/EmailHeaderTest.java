package com.aspose.jmap;

// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Unit tests for {@link EmailHeader}.
 */
public class EmailHeaderTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testSerializationRoundTrip() {
        EmailHeader original = new EmailHeader("Subject", "Hello World");
        // Serialize to JSON
        ObjectNode json = (ObjectNode) original.toJson();
        assertEquals("Subject", json.get("name").asText());
        assertEquals("Hello World", json.get("value").asText());

        // Deserialize back
        EmailHeader parsed = EmailHeader.fromJson(json);
        assertEquals(original.getName(), parsed.getName());
        assertEquals(original.getValue(), parsed.getValue());
    }

    @Test
    void testFromJsonMissingNameThrows() {
        ObjectNode json = MAPPER.createObjectNode();
        json.put("value", "Missing name field");
        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> EmailHeader.fromJson(json));
        assertTrue(ex.getMessage().contains("name"));
    }

    @Test
    void testFromJsonMissingValueThrows() {
        ObjectNode json = MAPPER.createObjectNode();
        json.put("name", "MissingValue");
        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> EmailHeader.fromJson(json));
        assertTrue(ex.getMessage().contains("value"));
    }

    @Test
    void testFromJsonNullNodeThrows() {
        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> EmailHeader.fromJson(null));
        assertTrue(ex.getMessage().contains("Invalid EmailHeader JSON"));
    }

    @Test
    void testFromJsonNonObjectThrows() {
        // Pass a JSON array instead of an object
        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> EmailHeader.fromJson(MAPPER.createArrayNode()));
        assertTrue(ex.getMessage().contains("expected an object"));
    }
}
