package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an EmailSubmission object as defined by JMAP.
 * <p>
 * This model is immutable; use the {@code fromJson} factory to create instances from
 * server responses and {@code toJson} to serialize for create/update payloads.
 */
public final class EmailSubmission {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String id;                         // server-assigned, optional on create
    private final String identityId;                 // required
    private final String emailId;                    // required
    private final String threadId;                   // server-assigned, optional on create
    private final Envelope envelope;                 // optional, may be null
    private final String sendAt;                     // server-assigned, optional on create
    private final String undoStatus;                 // server-assigned, optional on create
    private final Map<String, DeliveryStatus> deliveryStatus; // optional, may be null
    private final List<String> dsnBlobIds;           // server-assigned, optional on create
    private final List<String> mdnBlobIds;           // server-assigned, optional on create

    /**
     * Package‑visible all‑args constructor. Instances are created via {@link #fromJson(JsonNode)}.
     */
    public EmailSubmission(
            String id,
            String identityId,
            String emailId,
            String threadId,
            Envelope envelope,
            String sendAt,
            String undoStatus,
            Map<String, DeliveryStatus> deliveryStatus,
            List<String> dsnBlobIds,
            List<String> mdnBlobIds) {
        this.id = id;
        this.identityId = identityId;
        this.emailId = emailId;
        this.threadId = threadId;
        this.envelope = envelope;
        this.sendAt = sendAt;
        this.undoStatus = undoStatus;
        this.deliveryStatus = deliveryStatus != null ? Collections.unmodifiableMap(deliveryStatus) : null;
        this.dsnBlobIds = dsnBlobIds != null ? Collections.unmodifiableList(dsnBlobIds) : null;
        this.mdnBlobIds = mdnBlobIds != null ? Collections.unmodifiableList(mdnBlobIds) : null;
    }

    /** @return the server‑assigned id, or {@code null} if not present */
    public String getId() {
        return id;
    }

    /** @return the identity id (required) */
    public String getIdentityId() {
        return identityId;
    }

    /** @return the email id (required) */
    public String getEmailId() {
        return emailId;
    }

    /** @return the thread id, or {@code null} if not present */
    public String getThreadId() {
        return threadId;
    }

    /** @return the envelope, or {@code null} if not supplied */
    public Envelope getEnvelope() {
        return envelope;
    }

    /** @return the sendAt timestamp, or {@code null} if not present */
    public String getSendAt() {
        return sendAt;
    }

    /** @return the undo status, or {@code null} if not present */
    public String getUndoStatus() {
        return undoStatus;
    }

    /** @return a map of delivery status per recipient, or {@code null} if not present */
    public Map<String, DeliveryStatus> getDeliveryStatus() {
        return deliveryStatus;
    }

    /** @return list of DSN blob ids, or {@code null} if not present */
    public List<String> getDsnBlobIds() {
        return dsnBlobIds;
    }

    /** @return list of MDN blob ids, or {@code null} if not present */
    public List<String> getMdnBlobIds() {
        return mdnBlobIds;
    }

    /**
     * Parses a {@link JsonNode} representing an EmailSubmission object.
     *
     * @param data JSON object node
     * @return an immutable {@code EmailSubmission}
     * @throws JmapProtocolError if required fields are missing or have invalid types
     */
    public static EmailSubmission fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidArguments", "EmailSubmission JSON must be an object");
        }

        String id = getOptionalText(data, "id");
        String identityId = getRequiredText(data, "identityId");
        String emailId = getRequiredText(data, "emailId");
        String threadId = getOptionalText(data, "threadId");

        Envelope envelope = null;
        JsonNode envelopeNode = data.get("envelope");
        if (envelopeNode != null && !envelopeNode.isNull()) {
            envelope = Envelope.fromJson(envelopeNode);
        }

        String sendAt = getOptionalText(data, "sendAt");
        String undoStatus = getOptionalText(data, "undoStatus");

        Map<String, DeliveryStatus> deliveryStatus = null;
        JsonNode dsNode = data.get("deliveryStatus");
        if (dsNode != null && !dsNode.isNull()) {
            if (!dsNode.isObject()) {
                throw new JmapProtocolError("invalidArguments", "deliveryStatus must be an object");
            }
            deliveryStatus = new HashMap<>();
            var dsFields = dsNode.fields();
            while (dsFields.hasNext()) {
                Map.Entry<String, JsonNode> entry = dsFields.next();
                deliveryStatus.put(entry.getKey(), DeliveryStatus.fromJson(entry.getValue()));
            }
        }

        List<String> dsnBlobIds = getOptionalStringList(data, "dsnBlobIds");
        List<String> mdnBlobIds = getOptionalStringList(data, "mdnBlobIds");

        return new EmailSubmission(
                id,
                identityId,
                emailId,
                threadId,
                envelope,
                sendAt,
                undoStatus,
                deliveryStatus,
                dsnBlobIds,
                mdnBlobIds);
    }

    /**
     * Serialises this EmailSubmission to a {@link JsonNode}.
     *
     * @return an {@code ObjectNode} containing the JSON representation
     */
    public JsonNode toJson() {
        ObjectNode node = MAPPER.createObjectNode();

        if (id != null) node.put("id", id);
        if (identityId != null) node.put("identityId", identityId);
        if (emailId != null) node.put("emailId", emailId);
        if (threadId != null) node.put("threadId", threadId);
        if (envelope != null) node.set("envelope", envelope.toJson());
        if (sendAt != null) node.put("sendAt", sendAt);
        if (undoStatus != null) node.put("undoStatus", undoStatus);

        if (deliveryStatus != null) {
            ObjectNode dsNode = MAPPER.createObjectNode();
            deliveryStatus.forEach((k, v) -> dsNode.set(k, v.toJson()));
            node.set("deliveryStatus", dsNode);
        }

        if (dsnBlobIds != null) {
            ArrayNode arr = MAPPER.createArrayNode();
            dsnBlobIds.forEach(arr::add);
            node.set("dsnBlobIds", arr);
        }

        if (mdnBlobIds != null) {
            ArrayNode arr = MAPPER.createArrayNode();
            mdnBlobIds.forEach(arr::add);
            node.set("mdnBlobIds", arr);
        }

        return node;
    }

    // -------------------------------------------------------------------------
    // Helper methods for JSON parsing
    // -------------------------------------------------------------------------

    private static String getRequiredText(JsonNode node, String fieldName) {
        JsonNode f = node.get(fieldName);
        if (f == null || f.isNull() || !f.isTextual()) {
            throw new JmapProtocolError("invalidArguments", "Missing required field: " + fieldName);
        }
        return f.asText();
    }

    private static String getOptionalText(JsonNode node, String fieldName) {
        JsonNode f = node.get(fieldName);
        return (f != null && !f.isNull() && f.isTextual()) ? f.asText() : null;
    }

    private static List<String> getOptionalStringList(JsonNode node, String fieldName) {
        JsonNode arrNode = node.get(fieldName);
        if (arrNode == null || arrNode.isNull()) {
            return null;
        }
        if (!arrNode.isArray()) {
            throw new JmapProtocolError("invalidArguments", fieldName + " must be an array");
        }
        List<String> list = new ArrayList<>();
        arrNode.forEach(item -> {
            if (!item.isTextual()) {
                throw new JmapProtocolError("invalidArguments", fieldName + " array must contain strings");
            }
            list.add(item.asText());
        });
        return list;
    }
}
