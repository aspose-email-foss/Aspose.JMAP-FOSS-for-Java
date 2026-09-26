package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.Map;

/**
 * Represents a JMAP method invocation.
 * <p>
 * On the wire this is a JSON array of exactly three elements:
 * <pre>
 * ["methodName", {arguments}, "callId"]
 * </pre>
 * The class provides hand‑written {@code fromJson} and {@code toJson} methods that
 * respect this array shape.
 */
public final class Invocation {
    private final String name;
    private final Map<String, Object> arguments;
    private final String methodCallId;

    /** Shared Jackson mapper for (de)serialization. */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Package‑visible all‑args constructor.
     *
     * @param name          the JMAP method name
     * @param arguments     the method arguments map
     * @param methodCallId  the client‑chosen identifier for this call
     */
    public Invocation(String name, Map<String, Object> arguments, String methodCallId) {
        this.name = name;
        this.arguments = arguments;
        this.methodCallId = methodCallId;
    }

    /** @return the JMAP method name */
    public String getName() {
        return name;
    }

    /** @return the method arguments */
    public Map<String, Object> getArguments() {
        return arguments;
    }

    /** @return the call identifier */
    public String getMethodCallId() {
        return methodCallId;
    }

    /**
     * Parses an {@code Invocation} from a JSON node.
     *
     * @param data a JSON array node representing the invocation
     * @return a new {@code Invocation} instance
     * @throws JmapProtocolError if the JSON is malformed or missing required elements
     */
    public static Invocation fromJson(JsonNode data) {
        if (data == null || !data.isArray() || data.size() != 3) {
            throw new JmapProtocolError(
                "InvalidInvocation",
                "Invocation must be a JSON array of exactly three elements"
            );
        }

        JsonNode nameNode = data.get(0);
        JsonNode argsNode = data.get(1);
        JsonNode idNode = data.get(2);

        if (!nameNode.isTextual() || !argsNode.isObject() || !idNode.isTextual()) {
            throw new JmapProtocolError(
                "InvalidInvocation",
                "Invocation array elements must be: string, object, string"
            );
        }

        String name = nameNode.asText();
        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = MAPPER.convertValue(argsNode, Map.class);
        String methodCallId = idNode.asText();

        return new Invocation(name, arguments, methodCallId);
    }

    /**
     * Serialises this {@code Invocation} to a JSON array node.
     *
     * @return a JSON array representing the invocation
     */
    public JsonNode toJson() {
        ArrayNode array = MAPPER.createArrayNode();
        array.add(name);
        array.add(MAPPER.valueToTree(arguments));
        array.add(methodCallId);
        return array;
    }
}
