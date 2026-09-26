package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

public class SetErrorTest {

    @org.junit.jupiter.api.Test
    void testFullSerializationDeserialization() {
        java.util.List<String> props = java.util.List.of("name", "color");
        SetError original = new SetError("invalidProperties", "Invalid values", props);

        // Serialize to JSON
        com.fasterxml.jackson.databind.JsonNode json = original.toJson();

        // Verify JSON structure
        org.junit.jupiter.api.Assertions.assertTrue(json.isObject(), "Serialized JSON should be an object");
        org.junit.jupiter.api.Assertions.assertEquals("invalidProperties", json.get("type").asText());
        org.junit.jupiter.api.Assertions.assertEquals("Invalid values", json.get("description").asText());
        org.junit.jupiter.api.Assertions.assertTrue(json.get("properties").isArray(), "properties should be an array");
        org.junit.jupiter.api.Assertions.assertEquals(2, json.get("properties").size());
        org.junit.jupiter.api.Assertions.assertEquals("name", json.get("properties").get(0).asText());
        org.junit.jupiter.api.Assertions.assertEquals("color", json.get("properties").get(1).asText());

        // Deserialize back
        SetError parsed = SetError.fromJson(json);
        org.junit.jupiter.api.Assertions.assertEquals(original.getType(), parsed.getType());
        org.junit.jupiter.api.Assertions.assertEquals(original.getDescription(), parsed.getDescription());
        org.junit.jupiter.api.Assertions.assertEquals(original.getProperties(), parsed.getProperties());
    }

    @org.junit.jupiter.api.Test
    void testSerializationDeserializationWithNulls() {
        SetError original = new SetError("notFound", null, null);

        // Serialize
        com.fasterxml.jackson.databind.JsonNode json = original.toJson();

        // Should contain only the required field
        org.junit.jupiter.api.Assertions.assertTrue(json.isObject(), "Serialized JSON should be an object");
        org.junit.jupiter.api.Assertions.assertEquals("notFound", json.get("type").asText());
        org.junit.jupiter.api.Assertions.assertNull(json.get("description"), "description field should be absent");
        org.junit.jupiter.api.Assertions.assertNull(json.get("properties"), "properties field should be absent");

        // Deserialize
        SetError parsed = SetError.fromJson(json);
        org.junit.jupiter.api.Assertions.assertEquals("notFound", parsed.getType());
        org.junit.jupiter.api.Assertions.assertNull(parsed.getDescription());
        org.junit.jupiter.api.Assertions.assertNull(parsed.getProperties());
    }

    @org.junit.jupiter.api.Test
    void testFromJsonMissingRequiredTypeThrows() {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode node = mapper.createObjectNode();
        node.put("description", "Missing type field");

        org.junit.jupiter.api.Assertions.assertThrows(
                JmapProtocolError.class,
                () -> SetError.fromJson(node),
                "Missing required 'type' should cause JmapProtocolError"
        );
    }
}
