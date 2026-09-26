package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents an {@code EmailHeader} JMAP data object.
 *
 * <p>Both {@code name} and {@code value} are required string properties.
 */
public final class EmailHeader {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String name;
    private final String value;

    /**
     * Package‑visible all‑args constructor.
     *
     * @param name  the header name
     * @param value the header value
     */
    public EmailHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Returns the header name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the header value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Creates an {@code EmailHeader} instance from a JSON object.
     *
     * @param data the JSON node representing the EmailHeader
     * @return a new {@code EmailHeader}
     * @throws JmapProtocolError if required properties are missing or of the wrong type
     */
    public static EmailHeader fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidArguments", "Invalid EmailHeader JSON: expected an object");
        }

        JsonNode nameNode = data.get("name");
        JsonNode valueNode = data.get("value");

        if (nameNode == null || !nameNode.isTextual()) {
            throw new JmapProtocolError("invalidArguments", "EmailHeader missing required property 'name'");
        }
        if (valueNode == null || !valueNode.isTextual()) {
            throw new JmapProtocolError("invalidArguments", "EmailHeader missing required property 'value'");
        }

        return new EmailHeader(nameNode.asText(), valueNode.asText());
    }

    /**
     * Serialises this {@code EmailHeader} to a JSON object.
     *
     * @return a {@link JsonNode} representing this EmailHeader
     */
    public JsonNode toJson() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("name", name);
        node.put("value", value);
        return node;
    }
}
