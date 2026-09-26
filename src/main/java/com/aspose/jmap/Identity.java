package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a sending identity (name/email/replyTo used as the From when submitting mail).
 * <p>
 * This class is immutable; all fields are final and set via the package‑visible all‑args constructor.
 * Use {@link #fromJson(JsonNode)} to deserialize from a JMAP response and {@link #toJson()} to
 * serialize for a JMAP request.
 */
public final class Identity {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String id;                     // server‑assigned, may be null on create
    private final String name;                   // defaults to empty string
    private final String email;                  // required
    private final List<EmailAddress> replyTo;    // nullable
    private final List<EmailAddress> bcc;        // nullable
    private final String textSignature;          // defaults to empty string
    private final String htmlSignature;          // defaults to empty string
    private final Boolean mayDelete;             // server‑assigned, may be null on create

    /**
     * Package‑visible all‑args constructor. Parameters are ordered exactly as defined in the
     * JMAP specification.
     */
    public Identity(String id,
             String name,
             String email,
             List<EmailAddress> replyTo,
             List<EmailAddress> bcc,
             String textSignature,
             String htmlSignature,
             Boolean mayDelete) {
        this.id = id;
        this.name = name != null ? name : "";
        this.email = email;
        this.replyTo = replyTo != null ? Collections.unmodifiableList(replyTo) : null;
        this.bcc = bcc != null ? Collections.unmodifiableList(bcc) : null;
        this.textSignature = textSignature != null ? textSignature : "";
        this.htmlSignature = htmlSignature != null ? htmlSignature : "";
        this.mayDelete = mayDelete;
    }

    /** @return server‑assigned identity id, or {@code null} if not present */
    public String getId() {
        return id;
    }

    /** @return display name (empty string if not set) */
    public String getName() {
        return name;
    }

    /** @return primary email address (required) */
    public String getEmail() {
        return email;
    }

    /** @return list of reply‑to addresses, or {@code null} if not set */
    public List<EmailAddress> getReplyTo() {
        return replyTo;
    }

    /** @return list of BCC addresses, or {@code null} if not set */
    public List<EmailAddress> getBcc() {
        return bcc;
    }

    /** @return plain‑text signature (empty string if not set) */
    public String getTextSignature() {
        return textSignature;
    }

    /** @return HTML signature (empty string if not set) */
    public String getHtmlSignature() {
        return htmlSignature;
    }

    /** @return {@code true} if the identity may be deleted, {@code false} if not, or {@code null} if unknown */
    public Boolean getMayDelete() {
        return mayDelete;
    }

    /**
     * Deserialises an {@code Identity} from a {@link JsonNode}.
     *
     * @param data JSON object representing an Identity
     * @return a new {@code Identity} instance
     * @throws JmapProtocolError if required fields are missing or have an unexpected type
     */
    public static Identity fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidJson", "Identity JSON must be an object");
        }

        String id = data.has("id") && !data.get("id").isNull() ? data.get("id").asText() : null;
        String name = data.has("name") && !data.get("name").isNull() ? data.get("name").asText() : "";

        JsonNode emailNode = data.get("email");
        if (emailNode == null || emailNode.isNull() || !emailNode.isTextual()) {
            throw new JmapProtocolError("missingRequiredProperty", "email is required");
        }
        String email = emailNode.asText();

        List<EmailAddress> replyTo = null;
        if (data.has("replyTo") && !data.get("replyTo").isNull()) {
            JsonNode node = data.get("replyTo");
            if (!node.isArray()) {
                throw new JmapProtocolError("invalidProperty", "replyTo must be an array");
            }
            replyTo = new ArrayList<>();
            for (JsonNode el : node) {
                replyTo.add(EmailAddress.fromJson(el));
            }
        }

        List<EmailAddress> bcc = null;
        if (data.has("bcc") && !data.get("bcc").isNull()) {
            JsonNode node = data.get("bcc");
            if (!node.isArray()) {
                throw new JmapProtocolError("invalidProperty", "bcc must be an array");
            }
            bcc = new ArrayList<>();
            for (JsonNode el : node) {
                bcc.add(EmailAddress.fromJson(el));
            }
        }

        String textSignature = data.has("textSignature") && !data.get("textSignature").isNull()
                ? data.get("textSignature").asText()
                : "";
        String htmlSignature = data.has("htmlSignature") && !data.get("htmlSignature").isNull()
                ? data.get("htmlSignature").asText()
                : "";

        Boolean mayDelete = null;
        if (data.has("mayDelete") && !data.get("mayDelete").isNull()) {
            mayDelete = data.get("mayDelete").asBoolean();
        }

        return new Identity(id, name, email, replyTo, bcc, textSignature, htmlSignature, mayDelete);
    }

    /**
     * Serialises this {@code Identity} to a {@link JsonNode}.
     *
     * @return JSON representation suitable for inclusion in a JMAP request or response
     */
    public JsonNode toJson() {
        ObjectNode node = MAPPER.createObjectNode();

        if (id != null) {
            node.put("id", id);
        }
        node.put("name", name);
        node.put("email", email);

        if (replyTo != null) {
            ArrayNode arr = node.putArray("replyTo");
            for (EmailAddress ea : replyTo) {
                arr.add(ea.toJson());
            }
        }

        if (bcc != null) {
            ArrayNode arr = node.putArray("bcc");
            for (EmailAddress ea : bcc) {
                arr.add(ea.toJson());
            }
        }

        node.put("textSignature", textSignature);
        node.put("htmlSignature", htmlSignature);

        if (mayDelete != null) {
            node.put("mayDelete", mayDelete);
        }

        return node;
    }
}
