package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

/**
 * Represents a group of email addresses as used in From/To headers, e.g.
 * {@code "Team: a@x, b@x;"}.
 *
 * <p>The JSON representation follows the JMAP wire format:
 * <pre>
 * {
 *   "name": "Team",               // optional, may be omitted or null
 *   "addresses": [ ... ]          // required, array of {@link EmailAddress}
 * }
 * </pre>
 */
public final class EmailAddressGroup {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String name;                     // may be null
    private final List<EmailAddress> addresses;    // never null, immutable

    /**
     * Package‑visible all‑args constructor.
     *
     * @param name      the group name, may be {@code null}
     * @param addresses the list of {@link EmailAddress} objects, must not be {@code null}
     */
    public EmailAddressGroup(String name, List<EmailAddress> addresses) {
        this.name = name;
        this.addresses = List.copyOf(addresses);
    }

    /**
     * Returns the group name, or {@code null} if none.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an immutable list of addresses belonging to this group.
     *
     * @return the addresses
     */
    public List<EmailAddress> getAddresses() {
        return addresses;
    }

    /**
     * Parses a JSON object into an {@code EmailAddressGroup}.
     *
     * @param data the JSON node representing the group
     * @return a new {@code EmailAddressGroup} instance
     * @throws JmapProtocolError if required fields are missing or have an unexpected type
     */
    public static EmailAddressGroup fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidArguments",
                    "Invalid EmailAddressGroup JSON: expected object");
        }

        JsonNode nameNode = data.get("name");
        String name = null;
        if (nameNode != null && !nameNode.isNull()) {
            if (!nameNode.isTextual()) {
                throw new JmapProtocolError("invalidArguments",
                        "Invalid EmailAddressGroup JSON: 'name' must be a string");
            }
            name = nameNode.asText();
        }

        JsonNode addressesNode = data.get("addresses");
        if (addressesNode == null || !addressesNode.isArray()) {
            throw new JmapProtocolError("invalidArguments",
                    "Invalid EmailAddressGroup JSON: missing or non‑array 'addresses'");
        }

        List<EmailAddress> parsed = new java.util.ArrayList<>(addressesNode.size());
        for (JsonNode addrNode : addressesNode) {
            parsed.add(EmailAddress.fromJson(addrNode));
        }

        return new EmailAddressGroup(name, parsed);
    }

    /**
     * Serialises this {@code EmailAddressGroup} to a JSON object.
     *
     * @return a {@link JsonNode} representing this group
     */
    public JsonNode toJson() {
        ObjectNode obj = MAPPER.createObjectNode();
        if (name != null) {
            obj.put("name", name);
        }
        ArrayNode arr = obj.putArray("addresses");
        for (EmailAddress addr : addresses) {
            arr.add(addr.toJson());
        }
        return obj;
    }
}
