package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

public class EmailBodyValueTest {

    @org.junit.jupiter.api.Test
    void roundTripSerialization_allFieldsPresent() {
        // Arrange: create instance with all fields set
        EmailBodyValue original = new EmailBodyValue("Hello, world!", Boolean.TRUE, Boolean.FALSE);

        // Act: serialize to JSON and deserialize back
        com.fasterxml.jackson.databind.JsonNode json = original.toJson();
        EmailBodyValue parsed = EmailBodyValue.fromJson(json);

        // Assert: all fields match
        org.junit.jupiter.api.Assertions.assertEquals("Hello, world!", parsed.getValue());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, parsed.getIsEncodingProblem());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.FALSE, parsed.getIsTruncated());
    }

    @org.junit.jupiter.api.Test
    void serializationOmitsNullOptionalFields() {
        // Arrange: instance with optional fields null
        EmailBodyValue original = new EmailBodyValue("Only required", null, null);

        // Act: serialize
        com.fasterxml.jackson.databind.JsonNode json = original.toJson();

        // Assert: required field present, optional fields absent
        org.junit.jupiter.api.Assertions.assertTrue(json.isObject());
        org.junit.jupiter.api.Assertions.assertEquals("Only required", json.get("value").asText());
        org.junit.jupiter.api.Assertions.assertFalse(json.has("isEncodingProblem"));
        org.junit.jupiter.api.Assertions.assertFalse(json.has("isTruncated"));

        // Deserialize back and verify optional fields are null
        EmailBodyValue parsed = EmailBodyValue.fromJson(json);
        org.junit.jupiter.api.Assertions.assertEquals("Only required", parsed.getValue());
        org.junit.jupiter.api.Assertions.assertNull(parsed.getIsEncodingProblem());
        org.junit.jupiter.api.Assertions.assertNull(parsed.getIsTruncated());
    }

    @org.junit.jupiter.api.Test
    void fromJson_missingRequiredField_throwsProtocolError() {
        // Arrange: JSON object without the required "value" field
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode node = mapper.createObjectNode();
        node.put("isEncodingProblem", true); // optional field present

        // Act & Assert: expect JmapProtocolError
        org.junit.jupiter.api.Assertions.assertThrows(
                JmapProtocolError.class,
                () -> EmailBodyValue.fromJson(node)
        );
    }

    @org.junit.jupiter.api.Test
    void fromJson_invalidOptionalFieldType_throwsProtocolError() {
        // Arrange: JSON with wrong type for isTruncated (string instead of boolean)
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode node = mapper.createObjectNode();
        node.put("value", "Test body");
        node.put("isTruncated", "notABoolean");

        // Act & Assert: expect JmapProtocolError due to type mismatch
        org.junit.jupiter.api.Assertions.assertThrows(
                JmapProtocolError.class,
                () -> EmailBodyValue.fromJson(node)
        );
    }
}
