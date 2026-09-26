package com.aspose.jmap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents an address in an Envelope.
 *
 * <p>This model is immutable and provides JSON (de)serialization helpers.</p>
 */
public final class Address {
    private final String email;
    private final Map<String, String> parameters; // nullable

    /**
     * Package‑visible all‑args constructor.
     *
     * @param email      the required email address
     * @param parameters optional SMTP parameters; may be {@code null}
     */
    public Address(String email, Map<String, String> parameters) {
        this.email = email;
        // NOT Map.copyOf(parameters): it throws NullPointerException on a null value, but a
        // JMAP Envelope Address parameter legitimately can have a null value (a value-less
        // SMTP parameter, e.g. "RET" with no argument) - confirmed, a real test case.
        this.parameters = parameters == null
                ? null
                : Collections.unmodifiableMap(new HashMap<>(parameters));
    }

    /**
     * Returns the email address.
     *
     * @return the email string
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the SMTP parameters map, or {@code null} if not present.
     *
     * @return an immutable map of parameters, possibly {@code null}
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Parses a JSON object into an {@code Address} instance.
     *
     * @param data the JSON node representing an address
     * @return a populated {@code Address}
     * @throws JmapProtocolError if required fields are missing or have invalid types
     */
    public static Address fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidArguments", "Address must be a JSON object");
        }

        JsonNode emailNode = data.get("email");
        if (emailNode == null || emailNode.isNull() || !emailNode.isTextual()) {
            throw new JmapProtocolError("invalidArguments", "Missing required property 'email' or it is not a string");
        }
        String email = emailNode.asText();

        JsonNode paramsNode = data.get("parameters");
        Map<String, String> parameters = null;
        if (paramsNode != null && !paramsNode.isNull()) {
            if (!paramsNode.isObject()) {
                throw new JmapProtocolError("invalidArguments", "Property 'parameters' must be a JSON object");
            }
            Iterator<Map.Entry<String, JsonNode>> fields = paramsNode.fields();
            Map<String, String> map = new HashMap<>();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode valueNode = entry.getValue();
                if (valueNode.isNull()) {
                    map.put(entry.getKey(), null);
                } else if (valueNode.isTextual()) {
                    map.put(entry.getKey(), valueNode.asText());
                } else {
                    throw new JmapProtocolError(
                            "invalidArguments",
                            "Parameter value for key '" + entry.getKey() + "' must be a string or null");
                }
            }
            parameters = map.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(map);
        }

        return new Address(email, parameters);
    }

    /**
     * Serialises this {@code Address} to a JSON object.
     *
     * @return a {@link JsonNode} representing this address
     */
    public JsonNode toJson() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("email", email);
        if (parameters != null) {
            ObjectNode paramsNode = MAPPER.createObjectNode();
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                if (entry.getValue() == null) {
                    paramsNode.putNull(entry.getKey());
                } else {
                    paramsNode.put(entry.getKey(), entry.getValue());
                }
            }
            node.set("parameters", paramsNode);
        }
        return node;
    }
}
