package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

public class SearchSnippetTest {

    @org.junit.jupiter.api.Test
    void testSerializationDeserializationFull() {
        // Build a SearchSnippet instance with all fields
        SearchSnippet original = new SearchSnippet("email-123", "Hello <mark>World</mark>", "Preview <mark>text</mark>");

        // Serialize to JSON
        com.fasterxml.jackson.databind.JsonNode json = original.toJson();

        // Verify JSON contains all fields
        org.junit.jupiter.api.Assertions.assertTrue(json.isObject());
        org.junit.jupiter.api.Assertions.assertEquals("email-123", json.get("emailId").asText());
        org.junit.jupiter.api.Assertions.assertEquals("Hello <mark>World</mark>", json.get("subject").asText());
        org.junit.jupiter.api.Assertions.assertEquals("Preview <mark>text</mark>", json.get("preview").asText());

        // Deserialize back
        SearchSnippet parsed = SearchSnippet.fromJson(json);

        // Verify round‑trip equality
        org.junit.jupiter.api.Assertions.assertEquals(original.getEmailId(), parsed.getEmailId());
        org.junit.jupiter.api.Assertions.assertEquals(original.getSubject(), parsed.getSubject());
        org.junit.jupiter.api.Assertions.assertEquals(original.getPreview(), parsed.getPreview());
    }

    @org.junit.jupiter.api.Test
    void testDeserializationMissingOptional() {
        // Create JSON with only the required field
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode obj = mapper.createObjectNode();
        obj.put("emailId", "email-456");
        // subject and preview are omitted (null)

        // Parse
        SearchSnippet snippet = SearchSnippet.fromJson(obj);

        // Verify required field is set and optional fields are null
        org.junit.jupiter.api.Assertions.assertEquals("email-456", snippet.getEmailId());
        org.junit.jupiter.api.Assertions.assertNull(snippet.getSubject());
        org.junit.jupiter.api.Assertions.assertNull(snippet.getPreview());

        // Serialize back; optional fields should be omitted
        com.fasterxml.jackson.databind.JsonNode serialized = snippet.toJson();
        org.junit.jupiter.api.Assertions.assertEquals("email-456", serialized.get("emailId").asText());
        org.junit.jupiter.api.Assertions.assertFalse(serialized.has("subject"));
        org.junit.jupiter.api.Assertions.assertFalse(serialized.has("preview"));
    }

    @org.junit.jupiter.api.Test
    void testFromJsonMissingRequiredThrows() {
        // JSON without emailId
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode obj = mapper.createObjectNode();
        obj.put("subject", "No emailId");

        // Expect JmapProtocolError
        org.junit.jupiter.api.Assertions.assertThrows(
                JmapProtocolError.class,
                () -> SearchSnippet.fromJson(obj)
        );
    }
}
