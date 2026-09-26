package com.aspose.jmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents a JMAP request envelope sent to the server's API URL.
 *
 * <p>The JSON body has the form:
 *
 * <pre>
 * {
 *   "using": ["urn:ietf:params:jmap:core", ...],
 *   "methodCalls": [ [name, arguments, methodCallId], ... ],
 *   "createdIds": { "clientId": "serverId", ... } // optional, may be null
 * }
 * </pre>
 *
 * This class is immutable and provides {@code fromJson} / {@code toJson}
 * methods for explicit (de)serialization.
 */
public final class JmapRequestEnvelope {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final List<String> using;
    private final List<Invocation> methodCalls;
    private final Map<String, String> createdIds; // nullable

    /**
     * Package‑visible all‑args constructor.
     *
     * @param using       required list of capability URNs
     * @param methodCalls required list of method invocations
     * @param createdIds  optional map of client‑generated IDs to server IDs, may be {@code null}
     */
    public JmapRequestEnvelope(List<String> using, List<Invocation> methodCalls, Map<String, String> createdIds) {
        this.using = Collections.unmodifiableList(new ArrayList<>(using));
        this.methodCalls = Collections.unmodifiableList(new ArrayList<>(methodCalls));
        this.createdIds = createdIds != null
                ? Collections.unmodifiableMap(new HashMap<>(createdIds))
                : null;
    }

    /** @return the list of capability URNs required by this request */
    public List<String> getUsing() {
        return using;
    }

    /** @return the list of method invocations */
    public List<Invocation> getMethodCalls() {
        return methodCalls;
    }

    /** @return the map of client‑generated IDs to server IDs, or {@code null} if not supplied */
    public Map<String, String> getCreatedIds() {
        return createdIds;
    }

    /**
     * Parses a {@link JsonNode} representing a JMAP request envelope.
     *
     * @param data JSON object node to parse
     * @return a {@code JmapRequestEnvelope} instance
     * @throws JmapProtocolError if required properties are missing or have an unexpected type
     */
    public static JmapRequestEnvelope fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidRequest", "Invalid request envelope: expected JSON object");
        }

        // using (required)
        JsonNode usingNode = data.get("using");
        if (usingNode == null || !usingNode.isArray()) {
            throw new JmapProtocolError("invalidRequest", "Missing required property 'using' or it is not an array");
        }
        List<String> using = new ArrayList<>();
        for (JsonNode elem : usingNode) {
            if (!elem.isTextual()) {
                throw new JmapProtocolError("invalidRequest", "Invalid value in 'using' array: expected string");
            }
            using.add(elem.asText());
        }

        // methodCalls (required)
        JsonNode callsNode = data.get("methodCalls");
        if (callsNode == null || !callsNode.isArray()) {
            throw new JmapProtocolError("invalidRequest", "Missing required property 'methodCalls' or it is not an array");
        }
        List<Invocation> methodCalls = new ArrayList<>();
        for (JsonNode elem : callsNode) {
            methodCalls.add(Invocation.fromJson(elem));
        }

        // createdIds (optional)
        JsonNode createdIdsNode = data.get("createdIds");
        Map<String, String> createdIds = null;
        if (createdIdsNode != null && !createdIdsNode.isNull()) {
            if (!createdIdsNode.isObject()) {
                throw new JmapProtocolError("invalidRequest", "'createdIds' must be an object or null");
            }
            createdIds = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> fields = createdIdsNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode val = entry.getValue();
                if (!val.isTextual()) {
                    throw new JmapProtocolError("invalidRequest",
                            "Invalid value for createdId '" + entry.getKey() + "': expected string");
                }
                createdIds.put(entry.getKey(), val.asText());
            }
        }

        return new JmapRequestEnvelope(using, methodCalls, createdIds);
    }

    /**
     * Serialises this envelope to a {@link JsonNode}.
     *
     * @return JSON representation suitable for sending in the HTTP request body
     */
    public JsonNode toJson() {
        ObjectNode root = MAPPER.createObjectNode();

        // using
        ArrayNode usingArray = MAPPER.createArrayNode();
        for (String urn : using) {
            usingArray.add(urn);
        }
        root.set("using", usingArray);

        // methodCalls
        ArrayNode callsArray = MAPPER.createArrayNode();
        for (Invocation inv : methodCalls) {
            callsArray.add(inv.toJson());
        }
        root.set("methodCalls", callsArray);

        // createdIds (optional)
        if (createdIds != null) {
            ObjectNode idsNode = MAPPER.createObjectNode();
            for (Map.Entry<String, String> entry : createdIds.entrySet()) {
                idsNode.put(entry.getKey(), entry.getValue());
            }
            root.set("createdIds", idsNode);
        } else {
            root.putNull("createdIds");
        }

        return root;
    }
}
