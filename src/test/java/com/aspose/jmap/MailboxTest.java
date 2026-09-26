package com.aspose.jmap;

// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Unit tests for {@link Mailbox}.
 */
public class MailboxTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void roundTripSerialization_allFieldsPresent() {
        // Arrange – create a fully populated Mailbox (using package‑visible constructor)
        MailboxRights rights = new MailboxRights(
                true,  // mayReadItems
                false, // mayAddItems
                true,  // mayRemoveItems
                false, // maySetSeen
                true,  // maySetKeywords
                false, // mayCreateChild
                true,  // mayRename
                false, // mayDelete
                true   // maySubmit
        );

        Mailbox original = new Mailbox(
                "mbx123",
                "Inbox",
                "parent123",
                "inbox",
                42L,
                100L,
                5L,
                80L,
                3L,
                rights,
                true
        );

        // Act – serialize then deserialize
        JsonNode json = original.toJson();
        Mailbox parsed = Mailbox.fromJson(json);

        // Assert – all getters match
        assertEquals(original.getId(), parsed.getId());
        assertEquals(original.getName(), parsed.getName());
        assertEquals(original.getParentId(), parsed.getParentId());
        assertEquals(original.getRole(), parsed.getRole());
        assertEquals(original.getSortOrder(), parsed.getSortOrder());
        assertEquals(original.getTotalEmails(), parsed.getTotalEmails());
        assertEquals(original.getUnreadEmails(), parsed.getUnreadEmails());
        assertEquals(original.getTotalThreads(), parsed.getTotalThreads());
        assertEquals(original.getUnreadThreads(), parsed.getUnreadThreads());
        assertEquals(original.isSubscribed(), parsed.isSubscribed());

        // Rights object equality – compare each field
        MailboxRights origRights = original.getMyRights();
        MailboxRights parsedRights = parsed.getMyRights();
        assertNotNull(origRights);
        assertNotNull(parsedRights);
        assertEquals(origRights.getMayReadItems(), parsedRights.getMayReadItems());
        assertEquals(origRights.getMayAddItems(), parsedRights.getMayAddItems());
        assertEquals(origRights.getMayRemoveItems(), parsedRights.getMayRemoveItems());
        assertEquals(origRights.getMaySetSeen(), parsedRights.getMaySetSeen());
        assertEquals(origRights.getMaySetKeywords(), parsedRights.getMaySetKeywords());
        assertEquals(origRights.getMayCreateChild(), parsedRights.getMayCreateChild());
        assertEquals(origRights.getMayRename(), parsedRights.getMayRename());
        assertEquals(origRights.getMayDelete(), parsedRights.getMayDelete());
        assertEquals(origRights.getMaySubmit(), parsedRights.getMaySubmit());
    }

    @Test
    void serializationOmitsNullOptionalFields() {
        // Arrange – mailbox with only required fields and defaults
        Mailbox mailbox = new Mailbox(
                null,               // id omitted for create payload
                "Drafts",           // name (required)
                null,               // parentId
                null,               // role
                0L,                 // sortOrder default
                null,               // totalEmails
                null,               // unreadEmails
                null,               // totalThreads
                null,               // unreadThreads
                null,               // myRights
                false               // isSubscribed default
        );

        // Act
        JsonNode json = mailbox.toJson();

        // Assert – required fields present, optional omitted
        assertTrue(json.isObject());
        assertFalse(json.has("id"));
        assertEquals("Drafts", json.get("name").asText());
        assertFalse(json.has("parentId"));
        assertFalse(json.has("role"));
        assertEquals(0L, json.get("sortOrder").asLong());
        assertFalse(json.has("totalEmails"));
        assertFalse(json.has("unreadEmails"));
        assertFalse(json.has("totalThreads"));
        assertFalse(json.has("unreadThreads"));
        assertFalse(json.has("myRights"));
        assertEquals(false, json.get("isSubscribed").asBoolean());
    }

    @Test
    void fromJson_missingRequiredName_throwsProtocolError() {
        // Arrange – JSON object without the required "name" property
        ObjectNode node = MAPPER.createObjectNode();
        node.put("id", "mbx999");
        // deliberately omit "name"

        // Act & Assert
        JmapProtocolError ex = assertThrows(
                JmapProtocolError.class,
                () -> Mailbox.fromJson(node)
        );
        assertEquals("invalidArguments", ex.getType());
        assertTrue(ex.getDescription().contains("name"));
    }

    // -------------------------------------------------------------------------
    // Helper methods (none needed for this test class)
    // -------------------------------------------------------------------------
}
