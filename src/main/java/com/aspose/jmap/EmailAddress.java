package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents a single email address used in JMAP message headers such as {@code From},
 * {@code To} or {@code Cc}.
 *
 * <p>The JSON representation follows the JMAP wire format:
 * <pre>
 * {
 *   "name": "John Doe",   // optional, may be omitted or null
 *   "email": "john@example.com" // required
 * }
 * </pre>
 *
 * Instances are immutable; use {@link #fromJson(JsonNode)} to deserialize and
 * {@link #toJson()} to serialize.
 */
public final class EmailAddress {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String name;   // optional, may be null
    private final String email;  // required, never null

    /**
     * Package‑visible all‑args constructor.
     *
     * @param name  optional display name, may be {@code null}
     * @param email required email address, must not be {@code null}
     */
    public EmailAddress(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * Returns the optional display name.
     *
     * @return the name, or {@code null} if not present
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the required email address.
     *
     * @return the email address string
     */
    public String getEmail() {
        return email;
    }

    /**
     * Parses a {@link JsonNode} representing an {@code EmailAddress} object.
     *
     * @param data the JSON node to parse; must be an object node
     * @return a new {@code EmailAddress} instance
     * @throws JmapProtocolError if required fields are missing or have invalid types
     */
    public static EmailAddress fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidArguments",
                    "Invalid EmailAddress JSON: expected an object");
        }

        JsonNode emailNode = data.get("email");
        if (emailNode == null || emailNode.isNull() || !emailNode.isTextual()) {
            throw new JmapProtocolError("invalidArguments",
                    "Missing required property 'email' in EmailAddress");
        }
        String email = emailNode.asText();

        JsonNode nameNode = data.get("name");
        String name = null;
        if (nameNode != null && !nameNode.isNull()) {
            if (!nameNode.isTextual()) {
                throw new JmapProtocolError("invalidArguments",
                        "Property 'name' must be a string in EmailAddress");
            }
            name = nameNode.asText();
        }

        return new EmailAddress(name, email);
    }

    /**
     * Serialises this {@code EmailAddress} to a {@link JsonNode} following the JMAP
     * wire format.
     *
     * @return an {@link ObjectNode} representing this address
     */
    public JsonNode toJson() {
        ObjectNode node = MAPPER.createObjectNode();
        if (name != null) {
            node.put("name", name);
        }
        node.put("email", email);
        return node;
    }
}
