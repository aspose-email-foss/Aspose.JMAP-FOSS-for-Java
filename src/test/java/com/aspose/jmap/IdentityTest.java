package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

public class IdentityTest {

    @org.junit.jupiter.api.Test
    void testFromJsonFullAndToJson() {
        // Build a full JSON representation of an Identity
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode json = mapper.createObjectNode();
        json.put("id", "id123");
        json.put("name", "John Doe");
        json.put("email", "john@example.com");

        // replyTo array with one address
        com.fasterxml.jackson.databind.node.ArrayNode replyToArr = json.putArray("replyTo");
        replyToArr.add(new EmailAddress("Reply Name", "reply@example.com").toJson());

        // bcc array with two addresses
        com.fasterxml.jackson.databind.node.ArrayNode bccArr = json.putArray("bcc");
        bccArr.add(new EmailAddress(null, "bcc1@example.com").toJson());
        bccArr.add(new EmailAddress("Bcc Two", "bcc2@example.com").toJson());

        json.put("textSignature", "Best regards");
        json.put("htmlSignature", "<p>Best regards</p>");
        json.put("mayDelete", true);

        // Deserialize
        Identity identity = Identity.fromJson(json);

        // Verify getters
        org.junit.jupiter.api.Assertions.assertEquals("id123", identity.getId());
        org.junit.jupiter.api.Assertions.assertEquals("John Doe", identity.getName());
        org.junit.jupiter.api.Assertions.assertEquals("john@example.com", identity.getEmail());
        org.junit.jupiter.api.Assertions.assertNotNull(identity.getReplyTo());
        org.junit.jupiter.api.Assertions.assertEquals(1, identity.getReplyTo().size());
        org.junit.jupiter.api.Assertions.assertEquals("Reply Name", identity.getReplyTo().get(0).getName());
        org.junit.jupiter.api.Assertions.assertEquals("reply@example.com", identity.getReplyTo().get(0).getEmail());

        org.junit.jupiter.api.Assertions.assertNotNull(identity.getBcc());
        org.junit.jupiter.api.Assertions.assertEquals(2, identity.getBcc().size());
        org.junit.jupiter.api.Assertions.assertNull(identity.getBcc().get(0).getName());
        org.junit.jupiter.api.Assertions.assertEquals("bcc1@example.com", identity.getBcc().get(0).getEmail());
        org.junit.jupiter.api.Assertions.assertEquals("Bcc Two", identity.getBcc().get(1).getName());
        org.junit.jupiter.api.Assertions.assertEquals("bcc2@example.com", identity.getBcc().get(1).getEmail());

        org.junit.jupiter.api.Assertions.assertEquals("Best regards", identity.getTextSignature());
        org.junit.jupiter.api.Assertions.assertEquals("<p>Best regards</p>", identity.getHtmlSignature());
        org.junit.jupiter.api.Assertions.assertTrue(identity.getMayDelete());

        // Serialize back to JSON and verify round‑trip consistency
        com.fasterxml.jackson.databind.JsonNode serialized = identity.toJson();
        org.junit.jupiter.api.Assertions.assertEquals("id123", serialized.get("id").asText());
        org.junit.jupiter.api.Assertions.assertEquals("John Doe", serialized.get("name").asText());
        org.junit.jupiter.api.Assertions.assertEquals("john@example.com", serialized.get("email").asText());

        org.junit.jupiter.api.Assertions.assertTrue(serialized.get("replyTo").isArray());
        org.junit.jupiter.api.Assertions.assertEquals(1, serialized.get("replyTo").size());
        org.junit.jupiter.api.Assertions.assertEquals("Reply Name", serialized.get("replyTo").get(0).get("name").asText());
        org.junit.jupiter.api.Assertions.assertEquals("reply@example.com", serialized.get("replyTo").get(0).get("email").asText());

        org.junit.jupiter.api.Assertions.assertTrue(serialized.get("bcc").isArray());
        org.junit.jupiter.api.Assertions.assertEquals(2, serialized.get("bcc").size());

        org.junit.jupiter.api.Assertions.assertEquals("Best regards", serialized.get("textSignature").asText());
        org.junit.jupiter.api.Assertions.assertEquals("<p>Best regards</p>", serialized.get("htmlSignature").asText());
        org.junit.jupiter.api.Assertions.assertTrue(serialized.get("mayDelete").asBoolean());
    }

    @org.junit.jupiter.api.Test
    void testFromJsonWithMissingOptionalFields() {
        // JSON with only required fields (email) and a name omitted (should default to empty string)
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode json = mapper.createObjectNode();
        json.put("email", "alice@example.com");

        Identity identity = Identity.fromJson(json);

        org.junit.jupiter.api.Assertions.assertNull(identity.getId());
        org.junit.jupiter.api.Assertions.assertEquals("", identity.getName());
        org.junit.jupiter.api.Assertions.assertEquals("alice@example.com", identity.getEmail());
        org.junit.jupiter.api.Assertions.assertNull(identity.getReplyTo());
        org.junit.jupiter.api.Assertions.assertNull(identity.getBcc());
        org.junit.jupiter.api.Assertions.assertEquals("", identity.getTextSignature());
        org.junit.jupiter.api.Assertions.assertEquals("", identity.getHtmlSignature());
        org.junit.jupiter.api.Assertions.assertNull(identity.getMayDelete());

        // Serialize should not include null optional fields
        com.fasterxml.jackson.databind.JsonNode serialized = identity.toJson();
        org.junit.jupiter.api.Assertions.assertFalse(serialized.has("id"));
        org.junit.jupiter.api.Assertions.assertEquals("", serialized.get("name").asText());
        org.junit.jupiter.api.Assertions.assertEquals("alice@example.com", serialized.get("email").asText());
        org.junit.jupiter.api.Assertions.assertFalse(serialized.has("replyTo"));
        org.junit.jupiter.api.Assertions.assertFalse(serialized.has("bcc"));
        org.junit.jupiter.api.Assertions.assertEquals("", serialized.get("textSignature").asText());
        org.junit.jupiter.api.Assertions.assertEquals("", serialized.get("htmlSignature").asText());
        org.junit.jupiter.api.Assertions.assertFalse(serialized.has("mayDelete"));
    }

    @org.junit.jupiter.api.Test
    void testFromJsonMissingRequiredEmailThrows() {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode json = mapper.createObjectNode();
        json.put("name", "No Email");

        org.junit.jupiter.api.Assertions.assertThrows(JmapProtocolError.class, () -> {
            Identity.fromJson(json);
        });
    }
}
