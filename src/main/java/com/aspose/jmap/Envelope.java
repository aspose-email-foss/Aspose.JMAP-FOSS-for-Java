package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

/**
 * SMTP MAIL FROM / RCPT TO envelope for a submission, distinct from the message's own From/To headers.
 *
 * <p>Corresponds to the JMAP {@code Envelope} object.</p>
 */
public final class Envelope {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Address mailFrom;
    private final List<Address> rcptTo;

    /**
     * Package-visible all-args constructor.
     *
     * @param mailFrom the SMTP MAIL FROM address (required)
     * @param rcptTo   the list of SMTP RCPT TO addresses (required)
     */
    public Envelope(Address mailFrom, List<Address> rcptTo) {
        this.mailFrom = mailFrom;
        this.rcptTo = List.copyOf(rcptTo);
    }

    /**
     * @return the SMTP MAIL FROM address.
     */
    public Address getMailFrom() {
        return mailFrom;
    }

    /**
     * @return an immutable list of SMTP RCPT TO addresses.
     */
    public List<Address> getRcptTo() {
        return rcptTo;
    }

    /**
     * Parses an {@code Envelope} from its JSON representation.
     *
     * @param data the JSON node representing the envelope
     * @return a new {@code Envelope} instance
     * @throws JmapProtocolError if required properties are missing or have an unexpected type
     */
    public static Envelope fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidArguments", "Envelope JSON must be an object");
        }

        JsonNode mailFromNode = data.get("mailFrom");
        if (mailFromNode == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required property: mailFrom");
        }
        Address mailFrom = Address.fromJson(mailFromNode);

        JsonNode rcptToNode = data.get("rcptTo");
        if (rcptToNode == null || !rcptToNode.isArray()) {
            throw new JmapProtocolError("invalidArguments", "Missing required property or not an array: rcptTo");
        }
        List<Address> rcptTo = new ArrayList<>();
        for (JsonNode element : rcptToNode) {
            rcptTo.add(Address.fromJson(element));
        }

        return new Envelope(mailFrom, rcptTo);
    }

    /**
     * Serialises this {@code Envelope} to a JSON node.
     *
     * @return an {@link ObjectNode} representing this envelope
     */
    public JsonNode toJson() {
        ObjectNode node = MAPPER.createObjectNode();
        node.set("mailFrom", mailFrom.toJson());

        ArrayNode rcptArray = MAPPER.createArrayNode();
        for (Address addr : rcptTo) {
            rcptArray.add(addr.toJson());
        }
        node.set("rcptTo", rcptArray);

        return node;
    }
}
