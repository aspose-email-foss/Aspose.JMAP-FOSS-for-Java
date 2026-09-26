package com.aspose.jmap;

// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Unit tests for {@link DeliveryStatus}.
 */
public class DeliveryStatusTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void roundTripSerialization_ShouldPreserveAllFields() {
        // Arrange: build a JSON object with all required properties
        ObjectNode json = MAPPER.createObjectNode();
        json.put("smtpReply", "250 OK");
        json.put("delivered", "yes");
        json.put("displayed", "unknown");

        // Act: deserialize, then serialize back
        DeliveryStatus status = DeliveryStatus.fromJson(json);
        JsonNode serialized = status.toJson();

        // Assert: getters return expected values
        assertEquals("250 OK", status.getSmtpReply());
        assertEquals("yes", status.getDelivered());
        assertEquals("unknown", status.getDisplayed());

        // Assert: serialized JSON contains exactly the same fields and values
        assertTrue(serialized.isObject(), "Serialized node should be an object");
        assertEquals("250 OK", serialized.get("smtpReply").asText());
        assertEquals("yes", serialized.get("delivered").asText());
        assertEquals("unknown", serialized.get("displayed").asText());
        assertEquals(3, serialized.size(), "Serialized object should contain exactly three fields");
    }

    @Test
    void fromJson_MissingOptionalProperty_LeavesItNull() {
        // Per submission.yaml, none of DeliveryStatus's properties are marked required - a
        // server may omit any of them (e.g. before final delivery status is known).
        ObjectNode json = MAPPER.createObjectNode();
        json.put("smtpReply", "550 Mailbox unavailable");
        // delivered omitted
        json.put("displayed", "yes");

        DeliveryStatus status = DeliveryStatus.fromJson(json);
        assertEquals("550 Mailbox unavailable", status.getSmtpReply());
        assertNull(status.getDelivered(), "delivered is optional and was omitted");
        assertEquals("yes", status.getDisplayed());
    }

    @Test
    void fromJson_NullNode_ShouldThrowJmapProtocolError() {
        // Act & Assert: null input leads to protocol error
        JmapProtocolError ex = assertThrows(JmapProtocolError.class,
                () -> DeliveryStatus.fromJson(null));
        assertTrue(ex.getMessage().contains("must be an object"));
    }

    @Test
    void toJson_ShouldProduceObjectNodeWithAllFields() {
        // Arrange: create a DeliveryStatus instance via constructor (package‑visible)
        DeliveryStatus status = new DeliveryStatus("421 Service not available", "queued", "yes");

        // Act: serialize to JSON
        JsonNode json = status.toJson();

        // Assert: verify structure and values
        assertTrue(json.isObject(), "Result should be a JSON object");
        assertEquals("421 Service not available", json.get("smtpReply").asText());
        assertEquals("queued", json.get("delivered").asText());
        assertEquals("yes", json.get("displayed").asText());
        assertEquals(3, json.size(), "Exactly three fields should be present");
    }
}
