package com.aspose.jmap;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Immutable model class representing an {@code Account} object inside a JMAP {@code Session}.
 *
 * <p>The JSON property names match the JMAP wire format exactly.
 */
public final class Account {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String name;
    private final Boolean isPersonal;
    private final Boolean isReadOnly;
    private final Map<String, Object> accountCapabilities;

    /**
     * Package‑visible all‑args constructor.
     *
     * @param name                the account name (required)
     * @param isPersonal          true if the account is personal, otherwise false (may be {@code null})
     * @param isReadOnly          true if the account is read‑only, otherwise false (may be {@code null})
     * @param accountCapabilities map of capability URN to capability‑specific information (may be {@code null})
     */
    public Account(String name, Boolean isPersonal, Boolean isReadOnly, Map<String, Object> accountCapabilities) {
        this.name = name;
        this.isPersonal = isPersonal;
        this.isReadOnly = isReadOnly;
        this.accountCapabilities = accountCapabilities != null
                ? Collections.unmodifiableMap(accountCapabilities)
                : Collections.emptyMap();
    }

    /** @return the account name */
    public String getName() {
        return name;
    }

    /** @return {@code true} if the account is personal, otherwise {@code false} (may be {@code null}) */
    public Boolean isPersonal() {
        return isPersonal;
    }

    /** @return {@code true} if the account is read‑only, otherwise {@code false} (may be {@code null}) */
    public Boolean isReadOnly() {
        return isReadOnly;
    }

    /** @return an unmodifiable map of capability URN to capability‑specific information */
    public Map<String, Object> getAccountCapabilities() {
        return accountCapabilities;
    }

    /**
     * Parses a JSON representation of an {@code Account}.
     *
     * @param data JSON node containing the account object
     * @return a new {@code Account} instance
     * @throws JmapProtocolError if required properties are missing or malformed
     */
    public static Account fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidArguments", "Invalid Account JSON: expected an object");
        }

        JsonNode nameNode = data.get("name");
        if (nameNode == null || nameNode.isNull()) {
            throw new JmapProtocolError("invalidArguments", "Missing required property 'name' in Account");
        }
        String name = nameNode.asText();

        JsonNode isPersonalNode = data.get("isPersonal");
        Boolean isPersonal = (isPersonalNode != null && !isPersonalNode.isNull())
                ? isPersonalNode.asBoolean()
                : null;

        JsonNode isReadOnlyNode = data.get("isReadOnly");
        Boolean isReadOnly = (isReadOnlyNode != null && !isReadOnlyNode.isNull())
                ? isReadOnlyNode.asBoolean()
                : null;

        JsonNode capsNode = data.get("accountCapabilities");
        Map<String, Object> caps = (capsNode != null && !capsNode.isNull())
                ? MAPPER.convertValue(capsNode, new TypeReference<Map<String, Object>>() {})
                : Collections.emptyMap();

        return new Account(name, isPersonal, isReadOnly, caps);
    }

    /**
     * Serialises this {@code Account} to a JSON node.
     *
     * @return an {@link ObjectNode} representing this account
     */
    public JsonNode toJson() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("name", name);
        if (isPersonal != null) {
            node.put("isPersonal", isPersonal);
        } else {
            node.putNull("isPersonal");
        }
        if (isReadOnly != null) {
            node.put("isReadOnly", isReadOnly);
        } else {
            node.putNull("isReadOnly");
        }
        node.set("accountCapabilities", MAPPER.valueToTree(accountCapabilities));
        return node;
    }
}
