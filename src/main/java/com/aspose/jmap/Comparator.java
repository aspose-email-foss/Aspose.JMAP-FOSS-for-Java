package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * One entry of an Email/query {@code sort} argument.
 *
 * <p>Corresponds to the JMAP {@code Comparator} data type.</p>
 */
public final class Comparator {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String property;
    private final boolean isAscending; // defaults to true when omitted
    private final String collation;    // may be null

    /**
     * Package‑visible all‑args constructor.
     *
     * @param property    the property to sort by (required)
     * @param isAscending true for ascending order, false for descending
     * @param collation   optional collation identifier, may be {@code null}
     */
    public Comparator(String property, boolean isAscending, String collation) {
        this.property = property;
        this.isAscending = isAscending;
        this.collation = collation;
    }

    /** @return the property name to sort by. */
    public String getProperty() {
        return property;
    }

    /** @return {@code true} if sorting is ascending, {@code false} otherwise. */
    public boolean isAscending() {
        return isAscending;
    }

    /** @return the collation identifier, or {@code null} if not set. */
    public String getCollation() {
        return collation;
    }

    /**
     * Parses a {@link Comparator} from its JSON representation.
     *
     * @param data the JSON node representing a Comparator object
     * @return a {@code Comparator} instance
     * @throws JmapProtocolError if required fields are missing or have invalid types
     */
    public static Comparator fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidArguments",
                    "Comparator JSON must be an object");
        }

        JsonNode propertyNode = data.get("property");
        if (propertyNode == null || !propertyNode.isTextual()) {
            throw new JmapProtocolError("invalidArguments",
                    "Missing required field 'property' or it is not a string");
        }
        String property = propertyNode.asText();

        JsonNode isAscendingNode = data.get("isAscending");
        boolean isAscending = isAscendingNode == null || !isAscendingNode.isBoolean()
                ? true
                : isAscendingNode.asBoolean();

        JsonNode collationNode = data.get("collation");
        String collation = null;
        if (collationNode != null && !collationNode.isNull()) {
            if (!collationNode.isTextual()) {
                throw new JmapProtocolError("invalidArguments",
                        "Field 'collation' must be a string or null");
            }
            collation = collationNode.asText();
        }

        return new Comparator(property, isAscending, collation);
    }

    /**
     * Serialises this {@code Comparator} to a JSON object.
     *
     * @return a {@link JsonNode} representing this Comparator
     */
    public JsonNode toJson() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("property", property);
        node.put("isAscending", isAscending);
        if (collation != null) {
            node.put("collation", collation);
        } else {
            node.putNull("collation");
        }
        return node;
    }
}
