package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

public class MailboxRightsTest {

    @org.junit.jupiter.api.Test
    void testSerializationAllFields() {
        MailboxRights rights = new MailboxRights(
                Boolean.TRUE,
                Boolean.FALSE,
                Boolean.TRUE,
                Boolean.FALSE,
                Boolean.TRUE,
                Boolean.FALSE,
                Boolean.TRUE,
                Boolean.FALSE,
                Boolean.TRUE);

        com.fasterxml.jackson.databind.JsonNode json = rights.toJson();

        org.junit.jupiter.api.Assertions.assertTrue(json.isObject());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, json.get("mayReadItems").booleanValue());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.FALSE, json.get("mayAddItems").booleanValue());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, json.get("mayRemoveItems").booleanValue());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.FALSE, json.get("maySetSeen").booleanValue());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, json.get("maySetKeywords").booleanValue());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.FALSE, json.get("mayCreateChild").booleanValue());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, json.get("mayRename").booleanValue());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.FALSE, json.get("mayDelete").booleanValue());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, json.get("maySubmit").booleanValue());
    }

    @org.junit.jupiter.api.Test
    void testDeserializationAllFields() throws Exception {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        String jsonStr = """
                {
                  "mayReadItems": true,
                  "mayAddItems": false,
                  "mayRemoveItems": true,
                  "maySetSeen": false,
                  "maySetKeywords": true,
                  "mayCreateChild": false,
                  "mayRename": true,
                  "mayDelete": false,
                  "maySubmit": true
                }
                """;
        com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(jsonStr);
        MailboxRights rights = MailboxRights.fromJson(node);

        org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, rights.getMayReadItems());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.FALSE, rights.getMayAddItems());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, rights.getMayRemoveItems());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.FALSE, rights.getMaySetSeen());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, rights.getMaySetKeywords());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.FALSE, rights.getMayCreateChild());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, rights.getMayRename());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.FALSE, rights.getMayDelete());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, rights.getMaySubmit());
    }

    @org.junit.jupiter.api.Test
    void testDeserializationWithMissingFields() throws Exception {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        String jsonStr = """
                {
                  "mayReadItems": true,
                  "maySetSeen": false
                }
                """;
        com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(jsonStr);
        MailboxRights rights = MailboxRights.fromJson(node);

        org.junit.jupiter.api.Assertions.assertEquals(Boolean.TRUE, rights.getMayReadItems());
        org.junit.jupiter.api.Assertions.assertNull(rights.getMayAddItems());
        org.junit.jupiter.api.Assertions.assertNull(rights.getMayRemoveItems());
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.FALSE, rights.getMaySetSeen());
        org.junit.jupiter.api.Assertions.assertNull(rights.getMaySetKeywords());
        org.junit.jupiter.api.Assertions.assertNull(rights.getMayCreateChild());
        org.junit.jupiter.api.Assertions.assertNull(rights.getMayRename());
        org.junit.jupiter.api.Assertions.assertNull(rights.getMayDelete());
        org.junit.jupiter.api.Assertions.assertNull(rights.getMaySubmit());
    }

    @org.junit.jupiter.api.Test
    void testDeserializationInvalidBooleanType() throws Exception {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        String jsonStr = """
                {
                  "mayReadItems": "yes"
                }
                """;
        com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(jsonStr);

        org.junit.jupiter.api.Assertions.assertThrows(
                JmapProtocolError.class,
                () -> MailboxRights.fromJson(node)
        );
    }
}
