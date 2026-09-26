package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.HashMap;

/**
 * Unit tests for {@link Account}.
 */
public class AccountTest {

    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;

    @Test
    void fromJson_and_toJson_roundTrip_allFieldsPresent() {
        // Build JSON with all properties
        ObjectNode json = FACTORY.objectNode();
        json.put("name", "user@example.com");
        json.put("isPersonal", true);
        json.put("isReadOnly", false);
        ObjectNode caps = FACTORY.objectNode();
        caps.put("urn:ietf:params:jmap:mail", FACTORY.objectNode()); // empty capability object
        caps.put("urn:ietf:params:jmap:submission", FACTORY.objectNode());
        json.set("accountCapabilities", caps);

        // Deserialize
        Account account = Account.fromJson(json);
        assertEquals("user@example.com", account.getName());
        assertTrue(account.isPersonal());
        assertFalse(account.isReadOnly());
        assertEquals(2, account.getAccountCapabilities().size());
        assertTrue(account.getAccountCapabilities().containsKey("urn:ietf:params:jmap:mail"));
        assertTrue(account.getAccountCapabilities().containsKey("urn:ietf:params:jmap:submission"));

        // Serialize back
        JsonNode serialized = account.toJson();
        assertEquals(json, serialized);
    }

    @Test
    void fromJson_handlesMissingOptionalFields() {
        // JSON with only required field and empty capabilities
        ObjectNode json = FACTORY.objectNode();
        json.put("name", "account2");
        json.set("accountCapabilities", FACTORY.objectNode());

        // Deserialize
        Account account = Account.fromJson(json);
        assertEquals("account2", account.getName());
        assertNull(account.isPersonal(), "isPersonal should be null when missing");
        assertNull(account.isReadOnly(), "isReadOnly should be null when missing");
        assertNotNull(account.getAccountCapabilities());
        assertTrue(account.getAccountCapabilities().isEmpty());

        // Serialize back – optional fields should appear as null
        JsonNode serialized = account.toJson();
        assertTrue(serialized.has("isPersonal"));
        assertTrue(serialized.get("isPersonal").isNull());
        assertTrue(serialized.has("isReadOnly"));
        assertTrue(serialized.get("isReadOnly").isNull());
    }

    @Test
    void fromJson_throwsWhenRequiredNameMissing() {
        ObjectNode json = FACTORY.objectNode();
        json.put("isPersonal", true);
        json.set("accountCapabilities", FACTORY.objectNode());

        JmapProtocolError ex = assertThrows(JmapProtocolError.class, () -> Account.fromJson(json));
        assertTrue(ex.getMessage().contains("Missing required property 'name'"));
    }

    @Test
    void toJson_includesAllFieldsEvenWhenNull() {
        Map<String, Object> caps = new HashMap<>();
        caps.put("urn:ietf:params:jmap:mail", Map.of());

        Account account = new Account("test", null, null, caps);
        JsonNode json = account.toJson();

        assertEquals("test", json.get("name").asText());
        assertTrue(json.has("isPersonal"));
        assertTrue(json.get("isPersonal").isNull());
        assertTrue(json.has("isReadOnly"));
        assertTrue(json.get("isReadOnly").isNull());
        assertTrue(json.has("accountCapabilities"));
        assertEquals(1, json.get("accountCapabilities").size());
        assertTrue(json.get("accountCapabilities").has("urn:ietf:params:jmap:mail"));
    }
}
