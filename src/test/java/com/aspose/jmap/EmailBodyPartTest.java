package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

public class EmailBodyPartTest {

    @org.junit.jupiter.api.Test
    void fromJsonAndToJson_fullFields() {
        // Build JSON with all fields present, including a nested subPart
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode root = mapper.createObjectNode();

        root.put("partId", "part-1");
        root.put("blobId", "blob-1");
        root.put("size", 12345L);
        root.put("type", "text/plain");
        root.put("name", "example.txt");
        root.put("charset", "utf-8");
        root.put("disposition", "attachment");
        root.put("cid", "cid-123");
        root.put("location", "loc-1");

        // headers array (empty for simplicity)
        root.putArray("headers");

        // language array
        com.fasterxml.jackson.databind.node.ArrayNode langArray = root.putArray("language");
        langArray.add("en");
        langArray.add("fr");

        // subParts array with one nested part
        com.fasterxml.jackson.databind.node.ArrayNode subPartsArray = root.putArray("subParts");
        com.fasterxml.jackson.databind.node.ObjectNode nested = mapper.createObjectNode();
        nested.put("size", 100L);
        nested.put("type", "image/png");
        nested.putArray("headers"); // empty headers
        subPartsArray.add(nested);

        // Parse
        EmailBodyPart part = EmailBodyPart.fromJson(root);

        // Verify getters
        org.junit.jupiter.api.Assertions.assertEquals("part-1", part.getPartId());
        org.junit.jupiter.api.Assertions.assertEquals("blob-1", part.getBlobId());
        org.junit.jupiter.api.Assertions.assertEquals(12345L, part.getSize());
        org.junit.jupiter.api.Assertions.assertEquals("example.txt", part.getName());
        org.junit.jupiter.api.Assertions.assertEquals("text/plain", part.getType());
        org.junit.jupiter.api.Assertions.assertEquals("utf-8", part.getCharset());
        org.junit.jupiter.api.Assertions.assertEquals("attachment", part.getDisposition());
        org.junit.jupiter.api.Assertions.assertEquals("cid-123", part.getCid());
        org.junit.jupiter.api.Assertions.assertEquals(java.util.List.of("en", "fr"), part.getLanguage());
        org.junit.jupiter.api.Assertions.assertEquals("loc-1", part.getLocation());
        org.junit.jupiter.api.Assertions.assertEquals(1, part.getSubParts().size());

        EmailBodyPart nestedPart = part.getSubParts().get(0);
        org.junit.jupiter.api.Assertions.assertEquals(100L, nestedPart.getSize());
        org.junit.jupiter.api.Assertions.assertEquals("image/png", nestedPart.getType());
        org.junit.jupiter.api.Assertions.assertTrue(nestedPart.getHeaders().isEmpty());

        // Serialise back to JSON and compare structural equality
        com.fasterxml.jackson.databind.JsonNode serialized = part.toJson();
        // Compare structurally via Map (guaranteed order-independent per the JDK Map.equals
        // contract) rather than JsonNode.equals directly, to avoid any ambiguity about
        // whether field insertion order affects ObjectNode equality in this Jackson version.
        org.junit.jupiter.api.Assertions.assertEquals(
                mapper.convertValue(root, java.util.Map.class),
                mapper.convertValue(serialized, java.util.Map.class));
    }

    @org.junit.jupiter.api.Test
    void fromJson_minimalRequiredFields() {
        // JSON with only required fields (size, type, headers)
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode root = mapper.createObjectNode();
        root.put("size", 42L);
        root.put("type", "application/json");
        root.putArray("headers"); // empty required array

        EmailBodyPart part = EmailBodyPart.fromJson(root);

        // Optional fields should be null or empty as appropriate
        org.junit.jupiter.api.Assertions.assertNull(part.getPartId());
        org.junit.jupiter.api.Assertions.assertNull(part.getBlobId());
        org.junit.jupiter.api.Assertions.assertNull(part.getName());
        org.junit.jupiter.api.Assertions.assertNull(part.getCharset());
        org.junit.jupiter.api.Assertions.assertNull(part.getDisposition());
        org.junit.jupiter.api.Assertions.assertNull(part.getCid());
        org.junit.jupiter.api.Assertions.assertNull(part.getLanguage());
        org.junit.jupiter.api.Assertions.assertNull(part.getLocation());
        org.junit.jupiter.api.Assertions.assertNull(part.getSubParts());

        org.junit.jupiter.api.Assertions.assertEquals(42L, part.getSize());
        org.junit.jupiter.api.Assertions.assertEquals("application/json", part.getType());
        org.junit.jupiter.api.Assertions.assertTrue(part.getHeaders().isEmpty());

        // Serialise back; should match the minimal JSON (no optional fields)
        com.fasterxml.jackson.databind.JsonNode serialized = part.toJson();
        // Compare structurally via Map (guaranteed order-independent per the JDK Map.equals
        // contract) rather than JsonNode.equals directly, to avoid any ambiguity about
        // whether field insertion order affects ObjectNode equality in this Jackson version.
        org.junit.jupiter.api.Assertions.assertEquals(
                mapper.convertValue(root, java.util.Map.class),
                mapper.convertValue(serialized, java.util.Map.class));
    }

    @org.junit.jupiter.api.Test
    void fromJson_missingRequiredField_throwsProtocolError() {
        // JSON missing the required 'size' field
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode root = mapper.createObjectNode();
        root.put("type", "text/plain");
        root.putArray("headers");

        org.junit.jupiter.api.Assertions.assertThrows(JmapProtocolError.class, () -> {
            EmailBodyPart.fromJson(root);
        });
    }
}
