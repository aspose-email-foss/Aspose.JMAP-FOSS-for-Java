package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MethodErrorTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void serializeAndDeserializeFull() {
        MethodError original = new MethodError("unknownMethod", "Something went wrong");
        JsonNode json = original.toJson();

        assertTrue(json.isObject());
        assertEquals("unknownMethod", json.get("type").asText());
        assertEquals("Something went wrong", json.get("description").asText());

        MethodError parsed = MethodError.fromJson(json);
        assertEquals(original.getType(), parsed.getType());
        assertEquals(original.getDescription(), parsed.getDescription());
    }

    @Test
    void deserializeWithoutDescription() {
        JsonNode json = MAPPER.createObjectNode()
                .put("type", "invalidArguments");
        MethodError error = MethodError.fromJson(json);
        assertEquals("invalidArguments", error.getType());
        assertNull(error.getDescription());

        JsonNode serialized = error.toJson();
        assertTrue(serialized.isObject());
        assertEquals("invalidArguments", serialized.get("type").asText());
        assertFalse(serialized.has("description"));
    }

    @Test
    void deserializeExplicitNullDescription() {
        JsonNode json = MAPPER.createObjectNode()
                .put("type", "accountNotFound")
                .putNull("description");
        MethodError error = MethodError.fromJson(json);
        assertEquals("accountNotFound", error.getType());
        assertNull(error.getDescription());
    }

    @Test
    void fromJsonMissingTypeThrows() {
        JsonNode json = MAPPER.createObjectNode()
                .put("description", "Missing type field");
        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> MethodError.fromJson(json));
        assertEquals("invalidMethodError", ex.getType());
        assertTrue(ex.getMessage().contains("Missing required field 'type'"));
    }

    @Test
    void fromJsonNonObjectThrows() {
        JsonNode json = MAPPER.createArrayNode();
        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> MethodError.fromJson(json));
        assertEquals("invalidMethodError", ex.getType());
        assertTrue(ex.getMessage().contains("MethodError must be a JSON object"));
    }
}
