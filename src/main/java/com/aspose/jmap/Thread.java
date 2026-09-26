package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a JMAP Thread object.
 * <p>
 * A Thread is an ordered list of Email ids that make up a conversation.
 * </p>
 */
public final class Thread {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String id;
    private final List<String> emailIds;

    /**
     * Package‑visible all‑args constructor.
     *
     * @param id       the server‑assigned thread identifier, may be {@code null} when constructing a create payload
     * @param emailIds the ordered list of email ids, may be {@code null} when constructing a create payload
     */
    public Thread(String id, List<String> emailIds) {
        this.id = id;
        this.emailIds = emailIds == null
                ? null
                : Collections.unmodifiableList(new ArrayList<>(emailIds));
    }

    /**
     * Returns the thread identifier.
     *
     * @return the thread id, or {@code null} if not set
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the ordered list of email ids belonging to this thread.
     *
     * @return an unmodifiable list of email ids, or {@code null} if not set
     */
    public List<String> getEmailIds() {
        return emailIds;
    }

    /**
     * Parses a {@link Thread} instance from a JSON node.
     *
     * @param data the JSON representation of a Thread
     * @return the parsed {@code Thread}
     * @throws JmapProtocolError if required properties are missing or have an unexpected type
     */
    public static Thread fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidType", "Thread JSON must be an object");
        }

        JsonNode idNode = data.get("id");
        if (idNode == null || !idNode.isTextual()) {
            throw new JmapProtocolError("missingProperty", "Thread missing required property 'id'");
        }
        String id = idNode.asText();

        JsonNode emailIdsNode = data.get("emailIds");
        if (emailIdsNode == null || !emailIdsNode.isArray()) {
            throw new JmapProtocolError("missingProperty", "Thread missing required property 'emailIds'");
        }
        List<String> emailIds = new ArrayList<>();
        for (JsonNode elem : emailIdsNode) {
            if (!elem.isTextual()) {
                throw new JmapProtocolError("invalidProperty", "Thread.emailIds must contain strings");
            }
            emailIds.add(elem.asText());
        }

        return new Thread(id, emailIds);
    }

    /**
     * Serialises this {@code Thread} to a JSON node.
     *
     * @return a JSON object representing this Thread
     */
    public JsonNode toJson() {
        ObjectNode node = MAPPER.createObjectNode();
        if (id != null) {
            node.put("id", id);
        }
        if (emailIds != null) {
            ArrayNode array = node.putArray("emailIds");
            for (String eid : emailIds) {
                array.add(eid);
            }
        }
        return node;
    }
}
