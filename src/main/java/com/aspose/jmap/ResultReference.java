package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * Represents a JMAP {@code ResultReference}, a back‑reference to a value produced by an earlier
 * method call in the same request.
 *
 * <p>Wire format:
 *
 * <pre>
 * {
 *   "resultOf": "callId",
 *   "name": "propertyName",
 *   "path": "/some/json/pointer"
 * }
 * </pre>
 *
 * All three properties are required and are strings.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc8620.html#section-5.4">JMAP ResultReference</a>
 */
public final class ResultReference {
    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;

    private final String resultOf;
    private final String name;
    private final String path;

    /**
     * Package‑visible all‑args constructor. Instances are immutable.
     *
     * @param resultOf the {@code resultOf} identifier of the earlier call
     * @param name     the name of the property within that call's result
     * @param path     a JSON Pointer into the referenced result
     */
    public ResultReference(String resultOf, String name, String path) {
        this.resultOf = resultOf;
        this.name = name;
        this.path = path;
    }

    /** @return the {@code resultOf} identifier */
    public String getResultOf() {
        return resultOf;
    }

    /** @return the property name within the referenced result */
    public String getName() {
        return name;
    }

    /** @return the JSON Pointer path into the referenced result */
    public String getPath() {
        return path;
    }

    /**
     * Parses a {@code ResultReference} from a {@link JsonNode}.
     *
     * @param data the JSON object node representing a ResultReference
     * @return a new {@code ResultReference} instance
     * @throws JmapProtocolError if any required field is missing or not a text node
     */
    public static ResultReference fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidResultReference", "ResultReference must be a JSON object");
        }

        JsonNode resultOfNode = data.get("resultOf");
        JsonNode nameNode = data.get("name");
        JsonNode pathNode = data.get("path");

        if (resultOfNode == null || !resultOfNode.isTextual()) {
            throw new JmapProtocolError("invalidResultReference", "Missing or non‑string 'resultOf' field");
        }
        if (nameNode == null || !nameNode.isTextual()) {
            throw new JmapProtocolError("invalidResultReference", "Missing or non‑string 'name' field");
        }
        if (pathNode == null || !pathNode.isTextual()) {
            throw new JmapProtocolError("invalidResultReference", "Missing or non‑string 'path' field");
        }

        return new ResultReference(
                resultOfNode.asText(),
                nameNode.asText(),
                pathNode.asText()
        );
    }

    /**
     * Serialises this {@code ResultReference} to a {@link JsonNode}.
     *
     * @return an {@link ObjectNode} containing {@code resultOf}, {@code name} and {@code path}
     */
    public JsonNode toJson() {
        ObjectNode node = FACTORY.objectNode();
        node.put("resultOf", resultOf);
        node.put("name", name);
        node.put("path", path);
        return node;
    }
}
