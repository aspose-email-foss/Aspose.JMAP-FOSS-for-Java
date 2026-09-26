package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents per‑recipient delivery outcome information for an {@link EmailSubmission}.
 *
 * <p>All properties correspond directly to the JMAP wire format. They are optional
 * because a server may omit any of them.</p>
 */
public final class DeliveryStatus {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String smtpReply;
    private final String delivered;
    private final String displayed;

    /**
     * Package‑visible all‑args constructor.
     *
     * @param smtpReply the raw SMTP reply string, may be {@code null}
     * @param delivered delivery status – one of {@code queued}, {@code yes},
     *                  {@code no} or {@code unknown}, may be {@code null}
     * @param displayed display status – one of {@code unknown} or {@code yes},
     *                  may be {@code null}
     */
    public DeliveryStatus(String smtpReply, String delivered, String displayed) {
        this.smtpReply = smtpReply;
        this.delivered = delivered;
        this.displayed = displayed;
    }

    /** @return the raw SMTP reply string, or {@code null} if absent */
    public String getSmtpReply() {
        return smtpReply;
    }

    /** @return the delivery status – {@code queued}, {@code yes}, {@code no},
     *         {@code unknown}, or {@code null} if absent */
    public String getDelivered() {
        return delivered;
    }

    /** @return the display status – {@code unknown}, {@code yes},
     *         or {@code null} if absent */
    public String getDisplayed() {
        return displayed;
    }

    /**
     * Parses a {@link DeliveryStatus} from a JSON object node.
     *
     * @param data the JSON representation; must be a JSON object
     * @return a {@code DeliveryStatus} instance (fields may be {@code null})
     * @throws JmapProtocolError if the supplied node is not a JSON object
     */
    public static DeliveryStatus fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidArguments",
                    "DeliveryStatus JSON must be an object");
        }

        JsonNode smtpReplyNode = data.get("smtpReply");
        JsonNode deliveredNode = data.get("delivered");
        JsonNode displayedNode = data.get("displayed");

        String smtpReply = smtpReplyNode != null ? smtpReplyNode.asText() : null;
        String delivered = deliveredNode != null ? deliveredNode.asText() : null;
        String displayed = displayedNode != null ? displayedNode.asText() : null;

        return new DeliveryStatus(smtpReply, delivered, displayed);
    }

    /**
     * Serialises this {@code DeliveryStatus} to a JSON object node.
     *
     * @return a {@link JsonNode} representing this instance
     */
    public JsonNode toJson() {
        ObjectNode obj = MAPPER.createObjectNode();
        if (smtpReply != null) {
            obj.put("smtpReply", smtpReply);
        }
        if (delivered != null) {
            obj.put("delivered", delivered);
        }
        if (displayed != null) {
            obj.put("displayed", displayed);
        }
        return obj;
    }
}
