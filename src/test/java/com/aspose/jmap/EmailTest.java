package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

/**
 * Unit tests for {@link Email}.
 */
public class EmailTest {

    @org.junit.jupiter.api.Test
    void roundTripSerialization_shouldPreserveAllFields() {
        // Prepare data
        java.util.Map<String, Boolean> mailboxIds = new java.util.HashMap<>();
        mailboxIds.put("mailboxA", true);
        java.util.Map<String, Boolean> keywords = new java.util.HashMap<>();
        keywords.put("$seen", true);
        java.util.List<String> messageId = java.util.List.of("<msg-1@example.com>", "<msg-2@example.com>");
        java.util.List<String> inReplyTo = java.util.List.of("<ref-1@example.com>");
        java.util.List<String> references = java.util.List.of("<ref-1@example.com>", "<ref-2@example.com>");
        java.util.List<com.aspose.jmap.EmailAddress> from = java.util.List.of(
                new com.aspose.jmap.EmailAddress("Alice", "alice@example.com"));
        java.util.List<com.aspose.jmap.EmailAddress> to = java.util.List.of(
                new com.aspose.jmap.EmailAddress(null, "bob@example.com"));
        java.util.Map<String, com.aspose.jmap.EmailBodyValue> bodyValues = new java.util.HashMap<>();
        bodyValues.put("0", new com.aspose.jmap.EmailBodyValue("Hello", false, false));

        Email email = new Email(
                "id123",
                "blob456",
                "thread789",
                mailboxIds,
                keywords,
                1024L,
                "2023-01-01T12:00:00Z",
                messageId,
                inReplyTo,
                references,
                null, // sender
                from,
                to,
                null, // cc
                null, // bcc
                null, // replyTo
                "Test subject",
                "2023-01-01T11:00:00Z",
                null, // bodyStructure
                bodyValues,
                null, // textBody
                null, // htmlBody
                null, // attachments
                true,
                "preview text"
        );

        // Serialize
        com.fasterxml.jackson.databind.JsonNode json = email.toJson();

        // Deserialize
        Email parsed = Email.fromJson(json);

        // Assertions on scalar fields (safe via default equals())
        org.junit.jupiter.api.Assertions.assertEquals(email.getId(), parsed.getId());
        org.junit.jupiter.api.Assertions.assertEquals(email.getBlobId(), parsed.getBlobId());
        org.junit.jupiter.api.Assertions.assertEquals(email.getThreadId(), parsed.getThreadId());
        org.junit.jupiter.api.Assertions.assertEquals(email.getMailboxIds(), parsed.getMailboxIds());
        org.junit.jupiter.api.Assertions.assertEquals(email.getKeywords(), parsed.getKeywords());
        org.junit.jupiter.api.Assertions.assertEquals(email.getSize(), parsed.getSize());
        org.junit.jupiter.api.Assertions.assertEquals(email.getReceivedAt(), parsed.getReceivedAt());
        org.junit.jupiter.api.Assertions.assertEquals(email.getMessageId(), parsed.getMessageId());
        org.junit.jupiter.api.Assertions.assertEquals(email.getInReplyTo(), parsed.getInReplyTo());
        org.junit.jupiter.api.Assertions.assertEquals(email.getReferences(), parsed.getReferences());
        org.junit.jupiter.api.Assertions.assertEquals(email.getSubject(), parsed.getSubject());
        org.junit.jupiter.api.Assertions.assertEquals(email.getSentAt(), parsed.getSentAt());
        org.junit.jupiter.api.Assertions.assertEquals(email.getHasAttachment(), parsed.getHasAttachment());
        org.junit.jupiter.api.Assertions.assertEquals(email.getPreview(), parsed.getPreview());

        // from/to (List<EmailAddress>) and bodyValues (Map<String, EmailBodyValue>) hold model
        // classes with no equals()/hashCode() override (plain immutable data classes, per
        // model_conventions - none of them override Object equality), so List/Map.equals()
        // would compare by reference identity, not value, and always report unequal for two
        // separately-parsed instances. Compare their JSON representation instead.
        org.junit.jupiter.api.Assertions.assertEquals(
                new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(
                        email.getFrom().stream().map(EmailAddress::toJson).toList()),
                new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(
                        parsed.getFrom().stream().map(EmailAddress::toJson).toList()));
        org.junit.jupiter.api.Assertions.assertEquals(
                new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(
                        email.getTo().stream().map(EmailAddress::toJson).toList()),
                new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(
                        parsed.getTo().stream().map(EmailAddress::toJson).toList()));
        org.junit.jupiter.api.Assertions.assertEquals(email.getBodyValues().keySet(), parsed.getBodyValues().keySet());
        for (String key : email.getBodyValues().keySet()) {
            org.junit.jupiter.api.Assertions.assertEquals(
                    email.getBodyValues().get(key).toJson(),
                    parsed.getBodyValues().get(key).toJson());
        }
    }

    @org.junit.jupiter.api.Test
    void fromJson_missingRequiredMailboxIds_shouldThrowProtocolError() {
        // Build JSON without the required "mailboxIds" property
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.node.ObjectNode node = mapper.createObjectNode();
        node.put("id", "id123");
        // intentionally omit mailboxIds
        node.putObject("keywords"); // optional, empty

        // Expect JmapProtocolError
        org.junit.jupiter.api.Assertions.assertThrows(
                JmapProtocolError.class,
                () -> Email.fromJson(node)
        );
    }

    @org.junit.jupiter.api.Test
    void toJson_omitsNullOptionalFields() {
        java.util.Map<String, Boolean> mailboxIds = new java.util.HashMap<>();
        mailboxIds.put("mailboxX", true);
        Email email = new Email(
                null, // id
                null, // blobId
                null, // threadId
                mailboxIds,
                null, // keywords (null -> empty)
                null, // size
                null, // receivedAt
                null, // messageId
                null, // inReplyTo
                null, // references
                null, // sender
                null, // from
                null, // to
                null, // cc
                null, // bcc
                null, // replyTo
                null, // subject
                null, // sentAt
                null, // bodyStructure
                java.util.Collections.emptyMap(),
                null, // textBody
                null, // htmlBody
                null, // attachments
                null, // hasAttachment
                null  // preview
        );

        com.fasterxml.jackson.databind.JsonNode json = email.toJson();

        // Required fields must be present
        org.junit.jupiter.api.Assertions.assertTrue(json.has("mailboxIds"));
        // Optional fields that are null should be absent
        org.junit.jupiter.api.Assertions.assertFalse(json.has("id"));
        org.junit.jupiter.api.Assertions.assertFalse(json.has("blobId"));
        org.junit.jupiter.api.Assertions.assertFalse(json.has("threadId"));
        org.junit.jupiter.api.Assertions.assertFalse(json.has("size"));
        org.junit.jupiter.api.Assertions.assertFalse(json.has("receivedAt"));
        org.junit.jupiter.api.Assertions.assertFalse(json.has("subject"));
        org.junit.jupiter.api.Assertions.assertFalse(json.has("hasAttachment"));
        org.junit.jupiter.api.Assertions.assertFalse(json.has("preview"));
    }
}
