package com.aspose.jmap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Immutable model class representing a JMAP {@code Email} object.
 *
 * <p>All fields correspond directly to the JMAP wire format property names.
 * The class provides a {@code fromJson} factory for deserialization and a
 * {@code toJson} method for serialization.</p>
 */
public final class Email {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String id;
    private final String blobId;
    private final String threadId;
    private final Map<String, Boolean> mailboxIds;
    private final Map<String, Boolean> keywords;
    private final Long size;
    private final String receivedAt;
    private final List<String> messageId;
    private final List<String> inReplyTo;
    private final List<String> references;
    private final List<EmailAddress> sender;
    private final List<EmailAddress> from;
    private final List<EmailAddress> to;
    private final List<EmailAddress> cc;
    private final List<EmailAddress> bcc;
    private final List<EmailAddress> replyTo;
    private final String subject;
    private final String sentAt;
    private final EmailBodyPart bodyStructure;
    private final Map<String, EmailBodyValue> bodyValues;
    private final List<EmailBodyPart> textBody;
    private final List<EmailBodyPart> htmlBody;
    private final List<EmailBodyPart> attachments;
    private final Boolean hasAttachment;
    private final String preview;

    /* package-visible all‑args constructor */
    Email(String id,
          String blobId,
          String threadId,
          Map<String, Boolean> mailboxIds,
          Map<String, Boolean> keywords,
          Long size,
          String receivedAt,
          List<String> messageId,
          List<String> inReplyTo,
          List<String> references,
          List<EmailAddress> sender,
          List<EmailAddress> from,
          List<EmailAddress> to,
          List<EmailAddress> cc,
          List<EmailAddress> bcc,
          List<EmailAddress> replyTo,
          String subject,
          String sentAt,
          EmailBodyPart bodyStructure,
          Map<String, EmailBodyValue> bodyValues,
          List<EmailBodyPart> textBody,
          List<EmailBodyPart> htmlBody,
          List<EmailBodyPart> attachments,
          Boolean hasAttachment,
          String preview) {
        this.id = id;
        this.blobId = blobId;
        this.threadId = threadId;
        this.mailboxIds = mailboxIds != null
                ? Collections.unmodifiableMap(new HashMap<>(mailboxIds))
                : Collections.emptyMap();
        this.keywords = keywords != null
                ? Collections.unmodifiableMap(new HashMap<>(keywords))
                : Collections.emptyMap();
        this.size = size;
        this.receivedAt = receivedAt;
        this.messageId = messageId != null ? Collections.unmodifiableList(new ArrayList<>(messageId)) : null;
        this.inReplyTo = inReplyTo != null ? Collections.unmodifiableList(new ArrayList<>(inReplyTo)) : null;
        this.references = references != null ? Collections.unmodifiableList(new ArrayList<>(references)) : null;
        this.sender = sender != null ? Collections.unmodifiableList(new ArrayList<>(sender)) : null;
        this.from = from != null ? Collections.unmodifiableList(new ArrayList<>(from)) : null;
        this.to = to != null ? Collections.unmodifiableList(new ArrayList<>(to)) : null;
        this.cc = cc != null ? Collections.unmodifiableList(new ArrayList<>(cc)) : null;
        this.bcc = bcc != null ? Collections.unmodifiableList(new ArrayList<>(bcc)) : null;
        this.replyTo = replyTo != null ? Collections.unmodifiableList(new ArrayList<>(replyTo)) : null;
        this.subject = subject;
        this.sentAt = sentAt;
        this.bodyStructure = bodyStructure;
        this.bodyValues = bodyValues != null
                ? Collections.unmodifiableMap(new HashMap<>(bodyValues))
                : Collections.emptyMap();
        this.textBody = textBody != null ? Collections.unmodifiableList(new ArrayList<>(textBody)) : null;
        this.htmlBody = htmlBody != null ? Collections.unmodifiableList(new ArrayList<>(htmlBody)) : null;
        this.attachments = attachments != null ? Collections.unmodifiableList(new ArrayList<>(attachments)) : null;
        this.hasAttachment = hasAttachment;
        this.preview = preview;
    }

    /** @return the server‑assigned id, or {@code null} if not present */
    public String getId() { return id; }

    /** @return the server‑assigned blobId, or {@code null} if not present */
    public String getBlobId() { return blobId; }

    /** @return the server‑assigned threadId, or {@code null} if not present */
    public String getThreadId() { return threadId; }

    /** @return map of mailbox ids to {@code true} (required) */
    public Map<String, Boolean> getMailboxIds() { return mailboxIds; }

    /** @return map of keywords to {@code true} (may be empty) */
    public Map<String, Boolean> getKeywords() { return keywords; }

    /** @return size in octets, or {@code null} if not present */
    public Long getSize() { return size; }

    /** @return RFC 3339 UTC timestamp when the message was received, or {@code null} */
    public String getReceivedAt() { return receivedAt; }

    /** @return list of {@code Message‑Id} header values, or {@code null} */
    public List<String> getMessageId() { return messageId; }

    /** @return list of {@code In‑Reply‑To} header values, or {@code null} */
    public List<String> getInReplyTo() { return inReplyTo; }

    /** @return list of {@code References} header values, or {@code null} */
    public List<String> getReferences() { return references; }

    /** @return list of {@code Sender} addresses, or {@code null} */
    public List<EmailAddress> getSender() { return sender; }

    /** @return list of {@code From} addresses, or {@code null} */
    public List<EmailAddress> getFrom() { return from; }

    /** @return list of {@code To} addresses, or {@code null} */
    public List<EmailAddress> getTo() { return to; }

    /** @return list of {@code Cc} addresses, or {@code null} */
    public List<EmailAddress> getCc() { return cc; }

    /** @return list of {@code Bcc} addresses, or {@code null} */
    public List<EmailAddress> getBcc() { return bcc; }

    /** @return list of {@code Reply‑To} addresses, or {@code null} */
    public List<EmailAddress> getReplyTo() { return replyTo; }

    /** @return subject string, or {@code null} */
    public String getSubject() { return subject; }

    /** @return RFC 3339 date string when the message was sent, or {@code null} */
    public String getSentAt() { return sentAt; }

    /** @return body structure, or {@code null} */
    public EmailBodyPart getBodyStructure() { return bodyStructure; }

    /** @return map of body part ids to {@link EmailBodyValue}, may be empty */
    public Map<String, EmailBodyValue> getBodyValues() { return bodyValues; }

    /** @return list of text body parts, or {@code null} */
    public List<EmailBodyPart> getTextBody() { return textBody; }

    /** @return list of HTML body parts, or {@code null} */
    public List<EmailBodyPart> getHtmlBody() { return htmlBody; }

    /** @return list of attachment parts, or {@code null} */
    public List<EmailBodyPart> getAttachments() { return attachments; }

    /** @return {@code true} if the message has at least one attachment, else {@code null} */
    public Boolean getHasAttachment() { return hasAttachment; }

    /** @return preview string, or {@code null} */
    public String getPreview() { return preview; }

    /**
     * Deserialises a {@code JsonNode} into an {@code Email} instance.
     *
     * @param data JSON object representing an Email
     * @return immutable {@code Email}
     * @throws JmapProtocolError if required fields are missing or have an unexpected type
     */
    public static Email fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("Email JSON must be an object");
        }

        String id = getStringOrNull(data, "id");
        String blobId = getStringOrNull(data, "blobId");
        String threadId = getStringOrNull(data, "threadId");

        JsonNode mailboxIdsNode = data.get("mailboxIds");
        if (mailboxIdsNode == null || !mailboxIdsNode.isObject()) {
            throw new JmapProtocolError("Email mailboxIds is required and must be an object");
        }
        Map<String, Boolean> mailboxIds = new HashMap<>();
        mailboxIdsNode.fields().forEachRemaining(e -> mailboxIds.put(e.getKey(), e.getValue().asBoolean()));

        Map<String, Boolean> keywords = new HashMap<>();
        JsonNode keywordsNode = data.get("keywords");
        if (keywordsNode != null && keywordsNode.isObject()) {
            keywordsNode.fields().forEachRemaining(e -> keywords.put(e.getKey(), e.getValue().asBoolean()));
        }

        Long size = getLongOrNull(data, "size");
        String receivedAt = getStringOrNull(data, "receivedAt");

        List<String> messageId = getStringListOrNull(data, "messageId");
        List<String> inReplyTo = getStringListOrNull(data, "inReplyTo");
        List<String> references = getStringListOrNull(data, "references");

        List<EmailAddress> sender = getEmailAddressListOrNull(data, "sender");
        List<EmailAddress> from = getEmailAddressListOrNull(data, "from");
        List<EmailAddress> to = getEmailAddressListOrNull(data, "to");
        List<EmailAddress> cc = getEmailAddressListOrNull(data, "cc");
        List<EmailAddress> bcc = getEmailAddressListOrNull(data, "bcc");
        List<EmailAddress> replyTo = getEmailAddressListOrNull(data, "replyTo");

        String subject = getStringOrNull(data, "subject");
        String sentAt = getStringOrNull(data, "sentAt");

        EmailBodyPart bodyStructure = null;
        JsonNode bodyStructureNode = data.get("bodyStructure");
        if (bodyStructureNode != null && bodyStructureNode.isObject()) {
            bodyStructure = EmailBodyPart.fromJson(bodyStructureNode);
        }

        Map<String, EmailBodyValue> bodyValues = new HashMap<>();
        JsonNode bodyValuesNode = data.get("bodyValues");
        if (bodyValuesNode != null && bodyValuesNode.isObject()) {
            bodyValuesNode.fields().forEachRemaining(e -> bodyValues.put(e.getKey(), EmailBodyValue.fromJson(e.getValue())));
        }

        List<EmailBodyPart> textBody = getEmailBodyPartListOrNull(data, "textBody");
        List<EmailBodyPart> htmlBody = getEmailBodyPartListOrNull(data, "htmlBody");
        List<EmailBodyPart> attachments = getEmailBodyPartListOrNull(data, "attachments");

        Boolean hasAttachment = getBooleanOrNull(data, "hasAttachment");
        String preview = getStringOrNull(data, "preview");

        return new Email(id, blobId, threadId, mailboxIds, keywords, size, receivedAt,
                messageId, inReplyTo, references,
                sender, from, to, cc, bcc, replyTo,
                subject, sentAt,
                bodyStructure, bodyValues,
                textBody, htmlBody, attachments,
                hasAttachment, preview);
    }

    /**
     * Serialises this {@code Email} into a {@code JsonNode}.
     *
     * @return {@link ObjectNode} representing the Email
     */
    public JsonNode toJson() {
        ObjectNode obj = MAPPER.createObjectNode();

        if (id != null) obj.put("id", id);
        if (blobId != null) obj.put("blobId", blobId);
        if (threadId != null) obj.put("threadId", threadId);

        ObjectNode mailboxIdsNode = MAPPER.createObjectNode();
        mailboxIds.forEach(mailboxIdsNode::put);
        obj.set("mailboxIds", mailboxIdsNode);

        if (!keywords.isEmpty()) {
            ObjectNode keywordsNode = MAPPER.createObjectNode();
            keywords.forEach(keywordsNode::put);
            obj.set("keywords", keywordsNode);
        }

        if (size != null) obj.put("size", size);
        if (receivedAt != null) obj.put("receivedAt", receivedAt);
        if (messageId != null) obj.set("messageId", stringListToArrayNode(messageId));
        if (inReplyTo != null) obj.set("inReplyTo", stringListToArrayNode(inReplyTo));
        if (references != null) obj.set("references", stringListToArrayNode(references));

        if (sender != null) obj.set("sender", emailAddressListToArrayNode(sender));
        if (from != null) obj.set("from", emailAddressListToArrayNode(from));
        if (to != null) obj.set("to", emailAddressListToArrayNode(to));
        if (cc != null) obj.set("cc", emailAddressListToArrayNode(cc));
        if (bcc != null) obj.set("bcc", emailAddressListToArrayNode(bcc));
        if (replyTo != null) obj.set("replyTo", emailAddressListToArrayNode(replyTo));

        if (subject != null) obj.put("subject", subject);
        if (sentAt != null) obj.put("sentAt", sentAt);
        if (bodyStructure != null) obj.set("bodyStructure", bodyStructure.toJson());

        if (!bodyValues.isEmpty()) {
            ObjectNode bodyValuesNode = MAPPER.createObjectNode();
            bodyValues.forEach((k, v) -> bodyValuesNode.set(k, v.toJson()));
            obj.set("bodyValues", bodyValuesNode);
        }

        if (textBody != null) obj.set("textBody", emailBodyPartListToArrayNode(textBody));
        if (htmlBody != null) obj.set("htmlBody", emailBodyPartListToArrayNode(htmlBody));
        if (attachments != null) obj.set("attachments", emailBodyPartListToArrayNode(attachments));

        if (hasAttachment != null) obj.put("hasAttachment", hasAttachment);
        if (preview != null) obj.put("preview", preview);

        return obj;
    }

    /* Helper methods for parsing */

    private static String getStringOrNull(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v != null && v.isTextual()) ? v.asText() : null;
    }

    private static Long getLongOrNull(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v != null && v.isNumber()) ? v.longValue() : null;
    }

    private static Boolean getBooleanOrNull(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v != null && v.isBoolean()) ? v.booleanValue() : null;
    }

    private static List<String> getStringListOrNull(JsonNode node, String field) {
        JsonNode arr = node.get(field);
        if (arr != null && arr.isArray()) {
            List<String> list = new ArrayList<>();
            arr.forEach(e -> {
                if (e.isTextual()) {
                    list.add(e.asText());
                }
            });
            return list;
        }
        return null;
    }

    private static List<EmailAddress> getEmailAddressListOrNull(JsonNode node, String field) {
        JsonNode arr = node.get(field);
        if (arr != null && arr.isArray()) {
            List<EmailAddress> list = new ArrayList<>();
            arr.forEach(e -> list.add(EmailAddress.fromJson(e)));
            return list;
        }
        return null;
    }

    private static List<EmailBodyPart> getEmailBodyPartListOrNull(JsonNode node, String field) {
        JsonNode arr = node.get(field);
        if (arr != null && arr.isArray()) {
            List<EmailBodyPart> list = new ArrayList<>();
            arr.forEach(e -> list.add(EmailBodyPart.fromJson(e)));
            return list;
        }
        return null;
    }

    /* Helper methods for serialisation */

    private static ArrayNode stringListToArrayNode(List<String> list) {
        ArrayNode arr = MAPPER.createArrayNode();
        list.forEach(arr::add);
        return arr;
    }

    private static ArrayNode emailAddressListToArrayNode(List<EmailAddress> list) {
        ArrayNode arr = MAPPER.createArrayNode();
        list.forEach(e -> arr.add(e.toJson()));
        return arr;
    }

    private static ArrayNode emailBodyPartListToArrayNode(List<EmailBodyPart> list) {
        ArrayNode arr = MAPPER.createArrayNode();
        list.forEach(p -> arr.add(p.toJson()));
        return arr;
    }
}
