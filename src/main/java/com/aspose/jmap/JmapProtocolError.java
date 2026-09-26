package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * Represents a JMAP protocol error returned as an {@code error} method response.
 * <p>
 * The error contains a required {@code type} string and an optional {@code description}
 * string. Instances are immutable and can be created from a {@link JsonNode} or
 * converted back to JSON via {@link #toJson()}.
 *
 * @see JmapError
 */
public final class JmapProtocolError extends JmapError {
    private static final long serialVersionUID = 1L;
    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;

    private final String type;
    private final String description;

    /**
     * Constructs a new {@code JmapProtocolError} with only a type.
     *
     * @param type the error type (required, non‑null)
     */
    public JmapProtocolError(String type) {
        this(type, null);
    }

    /**
     * Constructs a new {@code JmapProtocolError}.
     *
     * @param type        the error type (required, non‑null)
     * @param description the optional error description; may be {@code null}
     */
    public JmapProtocolError(String type, String description) {
        super(buildMessage(type, description));
        if (type == null) {
            throw new IllegalArgumentException("Error type must not be null");
        }
        this.type = type;
        this.description = description;
    }

    /**
     * Constructs a new {@code JmapProtocolError} with a cause.
     *
     * @param type        the error type (required, non‑null)
     * @param description the optional error description; may be {@code null}
     * @param cause       the underlying cause; may be {@code null}
     */
    public JmapProtocolError(String type, String description, Throwable cause) {
        super(buildMessage(type, description), cause);
        if (type == null) {
            throw new IllegalArgumentException("Error type must not be null");
        }
        this.type = type;
        this.description = description;
    }

    private static String buildMessage(String type, String description) {
        return description == null ? type : type + ": " + description;
    }

    /**
     * Returns the JMAP error type.
     *
     * @return the error type string
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the optional error description.
     *
     * @return the description string, or {@code null} if not provided
     */
    public String getDescription() {
        return description;
    }

    /**
     * Parses a {@link JsonNode} representing a JMAP protocol error into a
     * {@code JmapProtocolError} instance.
     *
     * @param data the JSON object node containing {@code type} and optional {@code description}
     * @return a new {@code JmapProtocolError}
     * @throws JmapProtocolError if the required {@code type} field is missing or not a text node
     */
    public static JmapProtocolError fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidError", "Error object must be a JSON object");
        }
        JsonNode typeNode = data.get("type");
        if (typeNode == null || !typeNode.isTextual()) {
            throw new JmapProtocolError("invalidError", "Missing required field 'type' in error object");
        }
        String type = typeNode.asText();
        JsonNode descNode = data.get("description");
        String description = (descNode != null && descNode.isTextual()) ? descNode.asText() : null;
        return new JmapProtocolError(type, description);
    }

    /**
     * Serialises this error to a JSON object node.
     *
     * @return an {@link ObjectNode} containing {@code type} and, if present, {@code description}
     */
    public JsonNode toJson() {
        ObjectNode node = FACTORY.objectNode();
        node.put("type", type);
        if (description != null) {
            node.put("description", description);
        }
        return node;
    }
}
