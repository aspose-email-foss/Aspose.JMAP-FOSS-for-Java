package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents the JSON response envelope returned from a JMAP API endpoint.
 *
 * <p>The envelope contains the list of method responses, optional created IDs map,
 * and the session state string.</p>
 *
 * <p>Instances are immutable and can be created from a {@link JsonNode} via
 * {@link #fromJson(JsonNode)} or converted back to JSON via {@link #toJson()}.</p>
 */
public final class JmapResponseEnvelope {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final List<Invocation> methodResponses;
    private final Map<String, String> createdIds; // may be null
    private final String sessionState;

    /**
     * Package‑visible all‑args constructor.
     *
     * @param methodResponses list of method responses (required)
     * @param createdIds map of temporary IDs to real IDs (optional, may be null)
     * @param sessionState current session state (required)
     */
    public JmapResponseEnvelope(List<Invocation> methodResponses,
                         Map<String, String> createdIds,
                         String sessionState) {
        this.methodResponses = Collections.unmodifiableList(new ArrayList<>(methodResponses));
        this.createdIds = createdIds != null ? Collections.unmodifiableMap(new HashMap<>(createdIds)) : null;
        this.sessionState = sessionState;
    }

    /** @return the list of method responses */
    public List<Invocation> getMethodResponses() {
        return methodResponses;
    }

    /** @return the map of created IDs, or {@code null} if not present */
    public Map<String, String> getCreatedIds() {
        return createdIds;
    }

    /** @return the session state string */
    public String getSessionState() {
        return sessionState;
    }

    /**
     * Parses a {@link JsonNode} representing a JMAP response envelope.
     *
     * @param data JSON object node
     * @return a {@code JmapResponseEnvelope} instance
     * @throws JmapProtocolError if required fields are missing or malformed
     */
    public static JmapResponseEnvelope fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("InvalidResponseEnvelope", "Response envelope must be a JSON object");
        }

        // methodResponses (required)
        JsonNode mrNode = data.get("methodResponses");
        if (mrNode == null || !mrNode.isArray()) {
            throw new JmapProtocolError("InvalidResponseEnvelope", "Missing or invalid 'methodResponses' property");
        }
        List<Invocation> methodResponses = new ArrayList<>();
        for (JsonNode elem : mrNode) {
            methodResponses.add(Invocation.fromJson(elem));
        }

        // createdIds (optional)
        Map<String, String> createdIds = null;
        JsonNode ciNode = data.get("createdIds");
        if (ciNode != null && !ciNode.isNull()) {
            if (!ciNode.isObject()) {
                throw new JmapProtocolError("InvalidResponseEnvelope", "'createdIds' must be an object");
            }
            createdIds = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> fields = ciNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode val = entry.getValue();
                if (!val.isTextual()) {
                    throw new JmapProtocolError("InvalidResponseEnvelope", "Created ID values must be strings");
                }
                createdIds.put(entry.getKey(), val.asText());
            }
        }

        // sessionState (required)
        JsonNode ssNode = data.get("sessionState");
        if (ssNode == null || !ssNode.isTextual()) {
            throw new JmapProtocolError("InvalidResponseEnvelope", "Missing or invalid 'sessionState' property");
        }
        String sessionState = ssNode.asText();

        return new JmapResponseEnvelope(methodResponses, createdIds, sessionState);
    }

    /**
     * Serialises this envelope to a {@link JsonNode}.
     *
     * @return JSON representation of the envelope
     */
    public JsonNode toJson() {
        ObjectNode root = MAPPER.createObjectNode();

        // methodResponses
        ArrayNode mrArray = MAPPER.createArrayNode();
        for (Invocation inv : methodResponses) {
            mrArray.add(inv.toJson());
        }
        root.set("methodResponses", mrArray);

        // createdIds (optional)
        if (createdIds != null) {
            ObjectNode ciObject = MAPPER.createObjectNode();
            for (Map.Entry<String, String> entry : createdIds.entrySet()) {
                ciObject.put(entry.getKey(), entry.getValue());
            }
            root.set("createdIds", ciObject);
        }

        // sessionState
        root.put("sessionState", sessionState);

        return root;
    }
}
