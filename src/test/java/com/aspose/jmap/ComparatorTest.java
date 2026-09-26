package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

public class ComparatorTest {

    @org.junit.jupiter.api.Test
    void testSerializationRoundTrip() {
        // Arrange: create a Comparator with all fields set
        Comparator original = new Comparator("subject", Boolean.FALSE, "en-US");

        // Act: serialize to JSON and then deserialize back
        com.fasterxml.jackson.databind.JsonNode json = original.toJson();
        Comparator parsed = Comparator.fromJson(json);

        // Assert: fields match
        org.junit.jupiter.api.Assertions.assertEquals("subject", parsed.getProperty());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.FALSE, parsed.isAscending());
        org.junit.jupiter.api.Assertions.assertEquals("en-US", parsed.getCollation());

        // Also verify JSON structure directly
        org.junit.jupiter.api.Assertions.assertTrue(json.isObject());
        org.junit.jupiter.api.Assertions.assertEquals("subject", json.get("property").asText());
        org.junit.jupiter.api.Assertions.assertEquals(false, json.get("isAscending").asBoolean());
        org.junit.jupiter.api.Assertions.assertEquals("en-US", json.get("collation").asText());
    }

    @org.junit.jupiter.api.Test
    void testOptionalCollationNull() {
        // Arrange: Comparator with null collation (and default ascending)
        Comparator comparator = new Comparator("receivedAt", Boolean.TRUE, null);

        // Act: serialize
        com.fasterxml.jackson.databind.JsonNode json = comparator.toJson();

        // Assert: collation field is present and null
        org.junit.jupiter.api.Assertions.assertTrue(json.has("collation"));
        org.junit.jupiter.api.Assertions.assertTrue(json.get("collation").isNull());

        // Deserialize back and ensure collation is null
        Comparator parsed = Comparator.fromJson(json);
        org.junit.jupiter.api.Assertions.assertNull(parsed.getCollation());
        org.junit.jupiter.api.Assertions.assertEquals("receivedAt", parsed.getProperty());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, parsed.isAscending());
    }

    @org.junit.jupiter.api.Test
    void testFromJsonMissingRequiredPropertyThrows() {
        // Arrange: JSON object missing the required "property" field
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode json = mapper.createObjectNode();
        json.put("isAscending", true); // optional field present
        json.putNull("collation");

        // Act & Assert: expect JmapProtocolError
        org.junit.jupiter.api.Assertions.assertThrows(
                JmapProtocolError.class,
                () -> Comparator.fromJson(json)
        );
    }
}
