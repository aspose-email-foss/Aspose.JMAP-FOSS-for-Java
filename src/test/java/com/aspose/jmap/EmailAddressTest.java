package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

public class EmailAddressTest {

    @org.junit.jupiter.api.Test
    void classIsFinalLikeEveryOtherModel() {
        // Regression test: every other model class in this codebase is `public final class`;
        // EmailAddress was the sole exception, with no subclass anywhere - an unintentional
        // inconsistency rather than a deliberate extension point.
        org.junit.jupiter.api.Assertions.assertTrue(
                java.lang.reflect.Modifier.isFinal(EmailAddress.class.getModifiers()),
                "EmailAddress should be declared final, like every other model class"
        );
    }

    @org.junit.jupiter.api.Test
    void testSerializationWithAllFields() {
        EmailAddress address = new EmailAddress("John Doe", "john@example.com");
        com.fasterxml.jackson.databind.JsonNode json = address.toJson();

        org.junit.jupiter.api.Assertions.assertTrue(json.has("name"));
        org.junit.jupiter.api.Assertions.assertEquals("John Doe", json.get("name").asText());
        org.junit.jupiter.api.Assertions.assertTrue(json.has("email"));
        org.junit.jupiter.api.Assertions.assertEquals("john@example.com", json.get("email").asText());
    }

    @org.junit.jupiter.api.Test
    void testDeserializationWithAllFields() {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode node = mapper.createObjectNode();
        node.put("name", "Alice");
        node.put("email", "alice@example.com");

        EmailAddress address = EmailAddress.fromJson(node);
        org.junit.jupiter.api.Assertions.assertEquals("Alice", address.getName());
        org.junit.jupiter.api.Assertions.assertEquals("alice@example.com", address.getEmail());
    }

    @org.junit.jupiter.api.Test
    void testSerializationWithoutOptionalName() {
        EmailAddress address = new EmailAddress(null, "bob@example.com");
        com.fasterxml.jackson.databind.JsonNode json = address.toJson();

        org.junit.jupiter.api.Assertions.assertFalse(json.has("name"));
        org.junit.jupiter.api.Assertions.assertTrue(json.has("email"));
        org.junit.jupiter.api.Assertions.assertEquals("bob@example.com", json.get("email").asText());
    }

    @org.junit.jupiter.api.Test
    void testDeserializationWithoutOptionalName() {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode node = mapper.createObjectNode();
        node.put("email", "carol@example.com");

        EmailAddress address = EmailAddress.fromJson(node);
        org.junit.jupiter.api.Assertions.assertNull(address.getName());
        org.junit.jupiter.api.Assertions.assertEquals("carol@example.com", address.getEmail());
    }

    @org.junit.jupiter.api.Test
    void testDeserializationMissingRequiredEmailThrows() {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode node = mapper.createObjectNode();
        node.put("name", "Dave");

        org.junit.jupiter.api.Assertions.assertThrows(
                JmapProtocolError.class,
                () -> EmailAddress.fromJson(node)
        );
    }
}
