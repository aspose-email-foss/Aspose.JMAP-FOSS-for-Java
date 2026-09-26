package com.aspose.jmap;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents a per‑item error returned by JMAP Set methods (e.g. {@code notCreated},
 * {@code notUpdated}, {@code notDestroyed}).
 *
 * <p>The JSON shape is an object with a required {@code type} field and optional
 * {@code description} and {@code properties} fields.</p>
 *
 * @see JmapProtocolError for parsing‑validation failures.
 */
public final class SetError {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String type;
    private final String description;          // nullable
    private final List<String> properties;     // nullable, immutable copy

    /**
     * Package‑visible all‑args constructor.
     *
     * @param type        the error type (required)
     * @param description optional human‑readable description, may be {@code null}
     * @param properties  optional list of property names that caused the error, may be {@code null}
     */
    public SetError(String type, String description, List<String> properties) {
        this.type = type;
        this.description = description;
        this.properties = properties == null ? null : List.copyOf(properties);
    }

    /** Returns the error type (e.g. {@code invalidProperties}, {@code notFound}). */
    public String getType() {
        return type;
    }

    /** Returns the optional description, or {@code null} if absent. */
    public String getDescription() {
        return description;
    }

    /**
     * Returns an immutable list of property names that caused the error,
     * or {@code null} if absent.
     */
    public List<String> getProperties() {
        return properties;
    }

    /**
     * Parses a {@link JsonNode} into a {@code SetError} instance.
     *
     * @param data the JSON object node representing a SetError
     * @return a {@code SetError} instance
     * @throws JmapProtocolError if required fields are missing or have invalid types
     */
    public static SetError fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidArguments",
                    "SetError must be a JSON object");
        }

        JsonNode typeNode = data.get("type");
        if (typeNode == null || !typeNode.isTextual()) {
            throw new JmapProtocolError("invalidArguments",
                    "SetError missing required field 'type' or it is not a string");
        }
        String type = typeNode.asText();

        JsonNode descNode = data.get("description");
        String description = null;
        if (descNode != null && !descNode.isNull()) {
            if (!descNode.isTextual()) {
                throw new JmapProtocolError("invalidArguments",
                        "SetError field 'description' must be a string or null");
            }
            description = descNode.asText();
        }

        JsonNode propsNode = data.get("properties");
        List<String> properties = null;
        if (propsNode != null && !propsNode.isNull()) {
            if (!propsNode.isArray()) {
                throw new JmapProtocolError("invalidArguments",
                        "SetError field 'properties' must be an array of strings or null");
            }
            properties = new ArrayList<>();
            for (JsonNode el : propsNode) {
                if (!el.isTextual()) {
                    throw new JmapProtocolError("invalidArguments",
                            "SetError 'properties' array elements must be strings");
                }
                properties.add(el.asText());
            }
        }

        return new SetError(type, description, properties);
    }

    /**
     * Serialises this {@code SetError} to a {@link JsonNode}.
     *
     * @return an {@link ObjectNode} representing this error
     */
    public JsonNode toJson() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("type", type);
        if (description != null) {
            node.put("description", description);
        }
        if (properties != null) {
            ArrayNode arr = node.putArray("properties");
            for (String p : properties) {
                arr.add(p);
            }
        }
        return node;
    }
}
