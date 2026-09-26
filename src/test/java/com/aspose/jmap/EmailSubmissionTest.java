package com.aspose.jmap;

// No import needed: this test file shares the exact same package as the production code under test
// (see single_package_rule) - every production class/interface is already visible here without importing it.

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link EmailSubmission}.
 */
public class EmailSubmissionTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void serializeAndDeserializeFullObject() {
        // required fields
        String id = "subm-123";
        String identityId = "ident-456";
        String emailId = "email-789";
        String threadId = "thread-001";

        // optional fields
        String sendAt = "2023-10-01T12:34:56Z";
        String undoStatus = "pending";

        DeliveryStatus ds = new DeliveryStatus("250 OK", "yes", "yes");
        Map<String, DeliveryStatus> deliveryStatus = new HashMap<>();
        deliveryStatus.put("user@example.com", ds);

        List<String> dsnBlobIds = List.of("blob-1", "blob-2");
        List<String> mdnBlobIds = List.of("blob-3");

        EmailSubmission original = new EmailSubmission(
                id,
                identityId,
                emailId,
                threadId,
                null,               // envelope omitted for this test
                sendAt,
                undoStatus,
                deliveryStatus,
                dsnBlobIds,
                mdnBlobIds
        );

        // Serialize to JSON
        ObjectNode json = (ObjectNode) original.toJson();

        // Deserialize back
        EmailSubmission parsed = EmailSubmission.fromJson(json);

        // Verify all fields round‑trip correctly
        assertEquals(id, parsed.getId());
        assertEquals(identityId, parsed.getIdentityId());
        assertEquals(emailId, parsed.getEmailId());
        assertEquals(threadId, parsed.getThreadId());
        assertNull(parsed.getEnvelope());
        assertEquals(sendAt, parsed.getSendAt());
        assertEquals(undoStatus, parsed.getUndoStatus());

        assertNotNull(parsed.getDeliveryStatus());
        assertEquals(1, parsed.getDeliveryStatus().size());
        DeliveryStatus parsedDs = parsed.getDeliveryStatus().get("user@example.com");
        assertNotNull(parsedDs);
        assertEquals("250 OK", parsedDs.getSmtpReply());
        assertEquals("yes", parsedDs.getDelivered());
        assertEquals("yes", parsedDs.getDisplayed());

        assertEquals(dsnBlobIds, parsed.getDsnBlobIds());
        assertEquals(mdnBlobIds, parsed.getMdnBlobIds());
    }

    @Test
    void serializeAndDeserializeWithOnlyRequiredFields() {
        // Only the required fields are supplied; everything else stays null
        EmailSubmission original = new EmailSubmission(
                null,               // id omitted on create
                "ident-abc",
                "email-def",
                null,               // threadId omitted
                null,               // envelope omitted
                null,               // sendAt omitted
                null,               // undoStatus omitted
                null,               // deliveryStatus omitted
                null,               // dsnBlobIds omitted
                null                // mdnBlobIds omitted
        );

        ObjectNode json = (ObjectNode) original.toJson();
        // The JSON should contain only the required properties
        assertTrue(json.has("identityId"));
        assertTrue(json.has("emailId"));
        assertFalse(json.has("id"));
        assertFalse(json.has("threadId"));
        assertFalse(json.has("envelope"));
        assertFalse(json.has("sendAt"));
        assertFalse(json.has("undoStatus"));
        assertFalse(json.has("deliveryStatus"));
        assertFalse(json.has("dsnBlobIds"));
        assertFalse(json.has("mdnBlobIds"));

        EmailSubmission parsed = EmailSubmission.fromJson(json);
        assertNull(parsed.getId());
        assertEquals("ident-abc", parsed.getIdentityId());
        assertEquals("email-def", parsed.getEmailId());
        assertNull(parsed.getThreadId());
        assertNull(parsed.getEnvelope());
        assertNull(parsed.getSendAt());
        assertNull(parsed.getUndoStatus());
        assertNull(parsed.getDeliveryStatus());
        assertNull(parsed.getDsnBlobIds());
        assertNull(parsed.getMdnBlobIds());
    }

    @Test
    void fromJsonThrowsWhenRequiredFieldMissing() {
        // Build a JSON object that lacks the required "identityId" field
        ObjectNode incomplete = MAPPER.createObjectNode();
        incomplete.put("emailId", "email-xyz"); // required but identityId missing

        JmapProtocolError ex = assertThrows(
                JmapProtocolError.class,
                () -> EmailSubmission.fromJson(incomplete)
        );
        assertEquals("invalidArguments", ex.getType());
        assertTrue(ex.getDescription().contains("identityId"));
    }
}
