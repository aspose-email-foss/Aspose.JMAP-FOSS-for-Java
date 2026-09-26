package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

/**
 * Unit tests for {@link EmailAddressGroup}.
 */
public class EmailAddressGroupTest {

    @org.junit.jupiter.api.Test
    void testFromJsonWithNameAndAddresses() {
        // Build JSON: {"name":"Team","addresses":[{"email":"a@x"},{"email":"b@x"}]}
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode root = mapper.createObjectNode();
        root.put("name", "Team");
        com.fasterxml.jackson.databind.node.ArrayNode addrArray = root.putArray("addresses");

        com.fasterxml.jackson.databind.node.ObjectNode a = mapper.createObjectNode();
        a.put("email", "a@x");
        addrArray.add(a);

        com.fasterxml.jackson.databind.node.ObjectNode b = mapper.createObjectNode();
        b.put("email", "b@x");
        addrArray.add(b);

        EmailAddressGroup group = EmailAddressGroup.fromJson(root);

        org.junit.jupiter.api.Assertions.assertEquals("Team", group.getName());
        org.junit.jupiter.api.Assertions.assertEquals(2, group.getAddresses().size());
        org.junit.jupiter.api.Assertions.assertEquals("a@x", group.getAddresses().get(0).getEmail());
        org.junit.jupiter.api.Assertions.assertEquals("b@x", group.getAddresses().get(1).getEmail());
    }

    @org.junit.jupiter.api.Test
    void testFromJsonWithoutName() {
        // Build JSON without the optional "name": {"addresses":[{"email":"c@x"}]}
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode root = mapper.createObjectNode();
        com.fasterxml.jackson.databind.node.ArrayNode addrArray = root.putArray("addresses");
        com.fasterxml.jackson.databind.node.ObjectNode c = mapper.createObjectNode();
        c.put("email", "c@x");
        addrArray.add(c);

        EmailAddressGroup group = EmailAddressGroup.fromJson(root);

        org.junit.jupiter.api.Assertions.assertNull(group.getName());
        org.junit.jupiter.api.Assertions.assertEquals(1, group.getAddresses().size());
        org.junit.jupiter.api.Assertions.assertEquals("c@x", group.getAddresses().get(0).getEmail());
    }

    @org.junit.jupiter.api.Test
    void testToJsonIncludesName() {
        // Construct group with name and two addresses
        EmailAddress addr1 = new EmailAddress(null, "a@x");
        EmailAddress addr2 = new EmailAddress(null, "b@x");
        java.util.List<EmailAddress> list = java.util.List.of(addr1, addr2);
        EmailAddressGroup group = new EmailAddressGroup("Team", list);

        com.fasterxml.jackson.databind.JsonNode json = group.toJson();

        org.junit.jupiter.api.Assertions.assertTrue(json.isObject());
        org.junit.jupiter.api.Assertions.assertEquals("Team", json.get("name").asText());

        com.fasterxml.jackson.databind.JsonNode addrsNode = json.get("addresses");
        org.junit.jupiter.api.Assertions.assertTrue(addrsNode.isArray());
        org.junit.jupiter.api.Assertions.assertEquals(2, addrsNode.size());
        org.junit.jupiter.api.Assertions.assertEquals("a@x", addrsNode.get(0).get("email").asText());
        org.junit.jupiter.api.Assertions.assertEquals("b@x", addrsNode.get(1).get("email").asText());
    }

    @org.junit.jupiter.api.Test
    void testToJsonOmitsNameWhenNull() {
        // Construct group with null name
        EmailAddress addr = new EmailAddress(null, "solo@x");
        java.util.List<EmailAddress> list = java.util.List.of(addr);
        EmailAddressGroup group = new EmailAddressGroup(null, list);

        com.fasterxml.jackson.databind.JsonNode json = group.toJson();

        org.junit.jupiter.api.Assertions.assertTrue(json.isObject());
        org.junit.jupiter.api.Assertions.assertTrue(json.get("name") == null || json.get("name").isNull());

        com.fasterxml.jackson.databind.JsonNode addrsNode = json.get("addresses");
        org.junit.jupiter.api.Assertions.assertTrue(addrsNode.isArray());
        org.junit.jupiter.api.Assertions.assertEquals(1, addrsNode.size());
        org.junit.jupiter.api.Assertions.assertEquals("solo@x", addrsNode.get(0).get("email").asText());
    }

    @org.junit.jupiter.api.Test
    void testFromJsonMissingAddressesThrows() {
        // JSON with name but missing addresses array
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode root = mapper.createObjectNode();
        root.put("name", "NoAddrs");

        org.junit.jupiter.api.Assertions.assertThrows(JmapProtocolError.class, () -> {
            EmailAddressGroup.fromJson(root);
        });
    }
}
