package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

public class ThreadTest {

    @org.junit.jupiter.api.Test
    public void testSerializationAndDeserialization() {
        // arrange
        java.util.List<String> ids = java.util.List.of("email1", "email2", "email3");
        Thread thread = new Thread("thread123", ids);

        // act: serialize
        com.fasterxml.jackson.databind.JsonNode json = thread.toJson();

        // assert: JSON contains expected fields
        org.junit.jupiter.api.Assertions.assertTrue(json.isObject(), "Result should be an object");
        org.junit.jupiter.api.Assertions.assertEquals("thread123", json.get("id").asText(), "id field mismatch");
        org.junit.jupiter.api.Assertions.assertTrue(json.get("emailIds").isArray(), "emailIds should be an array");
        org.junit.jupiter.api.Assertions.assertEquals(3, json.get("emailIds").size(), "emailIds array size mismatch");
        org.junit.jupiter.api.Assertions.assertEquals("email1", json.get("emailIds").get(0).asText());
        org.junit.jupiter.api.Assertions.assertEquals("email2", json.get("emailIds").get(1).asText());
        org.junit.jupiter.api.Assertions.assertEquals("email3", json.get("emailIds").get(2).asText());

        // act: deserialize
        Thread parsed = Thread.fromJson(json);

        // assert: fields round‑trip correctly
        org.junit.jupiter.api.Assertions.assertEquals(thread.getId(), parsed.getId(), "Round‑trip id mismatch");
        org.junit.jupiter.api.Assertions.assertEquals(thread.getEmailIds(), parsed.getEmailIds(), "Round‑trip emailIds mismatch");
    }

    @org.junit.jupiter.api.Test
    public void testNullOptionalFieldsSerialization() {
        // arrange: both fields are null (create payload scenario)
        Thread thread = new Thread(null, null);

        // act
        com.fasterxml.jackson.databind.JsonNode json = thread.toJson();

        // assert: resulting JSON object is empty (no id, no emailIds)
        org.junit.jupiter.api.Assertions.assertTrue(json.isObject(), "Result should be an object");
        org.junit.jupiter.api.Assertions.assertFalse(json.has("id"), "id field should be omitted");
        org.junit.jupiter.api.Assertions.assertFalse(json.has("emailIds"), "emailIds field should be omitted");
    }

    @org.junit.jupiter.api.Test
    public void testFromJsonMissingRequiredPropertiesThrows() {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

        // missing id
        com.fasterxml.jackson.databind.node.ObjectNode missingId = mapper.createObjectNode();
        missingId.putArray("emailIds").add("e1");
        org.junit.jupiter.api.Assertions.assertThrows(JmapProtocolError.class, () -> Thread.fromJson(missingId));

        // missing emailIds
        com.fasterxml.jackson.databind.node.ObjectNode missingEmailIds = mapper.createObjectNode();
        missingEmailIds.put("id", "t1");
        org.junit.jupiter.api.Assertions.assertThrows(JmapProtocolError.class, () -> Thread.fromJson(missingEmailIds));

        // emailIds not an array
        com.fasterxml.jackson.databind.node.ObjectNode badEmailIds = mapper.createObjectNode();
        badEmailIds.put("id", "t2");
        badEmailIds.put("emailIds", "not-an-array");
        org.junit.jupiter.api.Assertions.assertThrows(JmapProtocolError.class, () -> Thread.fromJson(badEmailIds));

        // emailIds array contains non‑string
        com.fasterxml.jackson.databind.node.ObjectNode nonStringElem = mapper.createObjectNode();
        nonStringElem.put("id", "t3");
        com.fasterxml.jackson.databind.node.ArrayNode arr = nonStringElem.putArray("emailIds");
        arr.add(123); // integer instead of string
        org.junit.jupiter.api.Assertions.assertThrows(JmapProtocolError.class, () -> Thread.fromJson(nonStringElem));
    }
}
