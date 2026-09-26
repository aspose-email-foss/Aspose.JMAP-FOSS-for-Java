package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Immutable model class representing an {@code EmailBodyValue} as defined by the JMAP specification.
 *
 * <p>Properties:
 * <ul>
 *   <li>{@code value} – the body text (required).</li>
 *   <li>{@code isEncodingProblem} – true if the body could not be decoded correctly (optional).</li>
 *   <li>{@code isTruncated} – true if the body was truncated by the server (optional).</li>
 * </ul>
 *
 * <p>Instances are created via the package‑visible all‑args constructor or the {@link #fromJson(JsonNode)} factory.
 */
public final class EmailBodyValue {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String value;
    private final Boolean isEncodingProblem;
    private final Boolean isTruncated;

    /**
     * Package‑visible all‑args constructor.
     *
     * @param value               the body text, must not be {@code null}
     * @param isEncodingProblem   optional flag indicating an encoding problem
     * @param isTruncated         optional flag indicating truncation
     */
    public EmailBodyValue(String value, Boolean isEncodingProblem, Boolean isTruncated) {
        this.value = value;
        this.isEncodingProblem = isEncodingProblem;
        this.isTruncated = isTruncated;
    }

    /** @return the body text */
    public String getValue() {
        return value;
    }

    /** @return {@code true} if there was an encoding problem, {@code null} if unspecified */
    public Boolean getIsEncodingProblem() {
        return isEncodingProblem;
    }

    /** @return {@code true} if the body was truncated, {@code null} if unspecified */
    public Boolean getIsTruncated() {
        return isTruncated;
    }

    /**
     * Parses an {@code EmailBodyValue} from a JSON object.
     *
     * @param data the JSON node representing the object
     * @return a new {@code EmailBodyValue} instance
     * @throws JmapProtocolError if required fields are missing or have invalid types
     */
    public static EmailBodyValue fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidArguments", "EmailBodyValue must be a JSON object");
        }

        JsonNode valueNode = data.get("value");
        if (valueNode == null || !valueNode.isTextual()) {
            throw new JmapProtocolError("invalidArguments", "Missing required field 'value'");
        }
        String value = valueNode.asText();

        Boolean isEncodingProblem = null;
        JsonNode encNode = data.get("isEncodingProblem");
        if (encNode != null && !encNode.isNull()) {
            if (!encNode.isBoolean()) {
                throw new JmapProtocolError("invalidArguments", "Field 'isEncodingProblem' must be boolean");
            }
            isEncodingProblem = encNode.asBoolean();
        }

        Boolean isTruncated = null;
        JsonNode truncNode = data.get("isTruncated");
        if (truncNode != null && !truncNode.isNull()) {
            if (!truncNode.isBoolean()) {
                throw new JmapProtocolError("invalidArguments", "Field 'isTruncated' must be boolean");
            }
            isTruncated = truncNode.asBoolean();
        }

        return new EmailBodyValue(value, isEncodingProblem, isTruncated);
    }

    /**
     * Serialises this {@code EmailBodyValue} to a JSON object.
     *
     * @return a {@link JsonNode} representing this instance
     */
    public JsonNode toJson() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("value", value);
        if (isEncodingProblem != null) {
            node.put("isEncodingProblem", isEncodingProblem);
        }
        if (isTruncated != null) {
            node.put("isTruncated", isTruncated);
        }
        return node;
    }
}
