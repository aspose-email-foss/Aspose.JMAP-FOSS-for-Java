package com.aspose.jmap;

// No import needed: this test file shares the exact same package as the production code under test
// (see single_package_rule) - every production class/interface is already visible here without importing it.

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResultReferenceTest {

    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;

    @Test
    void serializeAndDeserializeRoundTrip() {
        ResultReference original = new ResultReference("call123", "mailboxId", "/list/0/id");
        ObjectNode json = (ObjectNode) original.toJson();

        assertEquals("call123", json.get("resultOf").asText());
        assertEquals("mailboxId", json.get("name").asText());
        assertEquals("/list/0/id", json.get("path").asText());

        ResultReference parsed = ResultReference.fromJson(json);
        assertEquals(original.getResultOf(), parsed.getResultOf());
        assertEquals(original.getName(), parsed.getName());
        assertEquals(original.getPath(), parsed.getPath());
    }

    @Test
    void fromJsonMissingRequiredFieldThrows() {
        ObjectNode incomplete = FACTORY.objectNode();
        incomplete.put("resultOf", "callA");
        incomplete.put("name", "id");
        // 'path' omitted intentionally

        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> ResultReference.fromJson(incomplete));
        assertEquals("invalidResultReference", ex.getType());
        assertTrue(ex.getDescription().contains("path"));
    }

    @Test
    void fromJsonNonStringFieldThrows() {
        ObjectNode badNode = FACTORY.objectNode();
        badNode.put("resultOf", 42); // should be string
        badNode.put("name", "id");
        badNode.put("path", "/some/path");

        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> ResultReference.fromJson(badNode));
        assertEquals("invalidResultReference", ex.getType());
        assertTrue(ex.getDescription().contains("resultOf"));
    }
}
