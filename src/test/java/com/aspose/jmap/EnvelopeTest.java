package com.aspose.jmap;

// No import needed: this test file shares the exact same package as the production code under test
// (see single_package_rule) - every production class/interface is already visible here without importing it.

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EnvelopeTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testSerializationAndDeserialization() {
        // Build a realistic Envelope instance
        Address mailFrom = new Address("sender@example.com", null);
        Address rcpt1 = new Address("rcpt1@example.com", Map.of("RET", "HDRS"));
        Address rcpt2 = new Address("rcpt2@example.com", Collections.emptyMap());
        List<Address> rcptTo = List.of(rcpt1, rcpt2);

        Envelope envelope = new Envelope(mailFrom, rcptTo);

        // Serialize to JSON
        JsonNode json = envelope.toJson();
        assertTrue(json.isObject(), "Envelope JSON should be an object");
        assertEquals("sender@example.com", json.get("mailFrom").get("email").asText());

        // Deserialize back
        Envelope parsed = Envelope.fromJson(json);
        assertEquals(mailFrom.getEmail(), parsed.getMailFrom().getEmail());
        assertEquals(rcptTo.size(), parsed.getRcptTo().size());

        // Verify each recipient
        for (int i = 0; i < rcptTo.size(); i++) {
            Address original = rcptTo.get(i);
            Address roundTrip = parsed.getRcptTo().get(i);
            assertEquals(original.getEmail(), roundTrip.getEmail());
            // parameters may be null or empty; compare safely
            Map<String, String> origParams = original.getParameters();
            Map<String, String> roundParams = roundTrip.getParameters();
            if (origParams == null) {
                assertNull(roundParams);
            } else {
                assertEquals(origParams, roundParams);
            }
        }
    }

    @Test
    void testEmptyRcptToList() {
        Address mailFrom = new Address("sender@example.com", null);
        List<Address> emptyRcpt = Collections.emptyList();

        Envelope envelope = new Envelope(mailFrom, emptyRcpt);
        JsonNode json = envelope.toJson();

        // rcptTo should be an empty array
        JsonNode rcptNode = json.get("rcptTo");
        assertTrue(rcptNode.isArray(), "rcptTo must be an array");
        assertEquals(0, rcptNode.size());

        // Deserialization must preserve the empty list
        Envelope parsed = Envelope.fromJson(json);
        assertEquals(0, parsed.getRcptTo().size());
    }

    @Test
    void testMissingMailFromThrowsProtocolError() {
        // Build JSON missing the required "mailFrom" property
        ObjectNode node = MAPPER.createObjectNode();

        // Add a valid rcptTo array with one address
        ArrayNode rcptArray = MAPPER.createArrayNode();
        ObjectNode addrNode = MAPPER.createObjectNode();
        addrNode.put("email", "rcpt@example.com");
        rcptArray.add(addrNode);
        node.set("rcptTo", rcptArray);

        // Expect a JmapProtocolError due to missing mailFrom
        assertThrows(JmapProtocolError.class, () -> Envelope.fromJson(node));
    }

    @Test
    void testMissingRcptToThrowsProtocolError() {
        // Build JSON missing the required "rcptTo" property
        ObjectNode node = MAPPER.createObjectNode();

        // Add a valid mailFrom object
        ObjectNode mailFromNode = MAPPER.createObjectNode();
        mailFromNode.put("email", "sender@example.com");
        node.set("mailFrom", mailFromNode);

        // Expect a JmapProtocolError due to missing rcptTo
        assertThrows(JmapProtocolError.class, () -> Envelope.fromJson(node));
    }
}
