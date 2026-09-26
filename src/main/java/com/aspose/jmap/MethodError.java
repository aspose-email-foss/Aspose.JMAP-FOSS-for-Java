package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents a top‑level JMAP method error returned as an {@code error} method response.
 *
 * <p>The {@code type} property is required and must be a non‑null string.
 * The {@code description} property is optional and may be {@code null}.
 *
 * @see JmapProtocolError
 */
public final class MethodError {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String type;
    private final String description; // nullable

    /**
     * Package‑visible all‑args constructor. Instances are immutable.
     *
     * @param type        the error type (required)
     * @param description the optional human‑readable description
     */
    public MethodError(String type, String description) {
        this.type = type;
        this.description = description;
    }

    /** Returns the error type. */
    public String getType() {
        return type;
    }

    /** Returns the optional description, or {@code null} if absent. */
    public String getDescription() {
        return description;
    }

    /**
     * Parses a {@link JsonNode} into a {@code MethodError}.
     *
     * @param data the JSON object representing the error
     * @return a {@code MethodError} instance
     * @throws JmapProtocolError if required fields are missing or have an unexpected type
     */
    public static MethodError fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidMethodError", "MethodError must be a JSON object");
        }

        JsonNode typeNode = data.get("type");
        if (typeNode == null || !typeNode.isTextual()) {
            throw new JmapProtocolError("invalidMethodError", "Missing required field 'type' in MethodError");
        }
        String type = typeNode.asText();

        JsonNode descNode = data.get("description");
        String description = null;
        if (descNode != null && !descNode.isNull()) {
            if (!descNode.isTextual()) {
                throw new JmapProtocolError("invalidMethodError", "Field 'description' must be a string or null");
            }
            description = descNode.asText();
        }

        return new MethodError(type, description);
    }

    /**
     * Serialises this {@code MethodError} to a {@link JsonNode}.
     *
     * @return an {@link ObjectNode} representing this error
     */
    public JsonNode toJson() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("type", type);
        if (description != null) {
            node.put("description", description);
        }
        return node;
    }
}
