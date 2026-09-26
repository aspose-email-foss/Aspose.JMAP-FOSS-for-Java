package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents a JMAP Mailbox object.
 * <p>
 * This class is immutable; use the provided constructor or {@link #fromJson(JsonNode)} factory
 * to create instances.
 */
public final class Mailbox {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String id;                 // server-assigned, may be null when creating
    private final String name;               // required
    private final String parentId;           // nullable
    private final String role;               // nullable
    private final long sortOrder;            // default 0
    private final Long totalEmails;          // nullable, server-assigned
    private final Long unreadEmails;         // nullable, server-assigned
    private final Long totalThreads;         // nullable, server-assigned
    private final Long unreadThreads;        // nullable, server-assigned
    private final MailboxRights myRights;    // nullable, server-assigned
    private final boolean isSubscribed;      // default false

    /**
     * Package‑visible all‑args constructor. The order matches the JMAP specification.
     */
    public Mailbox(String id,
            String name,
            String parentId,
            String role,
            long sortOrder,
            Long totalEmails,
            Long unreadEmails,
            Long totalThreads,
            Long unreadThreads,
            MailboxRights myRights,
            boolean isSubscribed) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.role = role;
        this.sortOrder = sortOrder;
        this.totalEmails = totalEmails;
        this.unreadEmails = unreadEmails;
        this.totalThreads = totalThreads;
        this.unreadThreads = unreadThreads;
        this.myRights = myRights;
        this.isSubscribed = isSubscribed;
    }

    /** @return the server‑assigned identifier, or {@code null} for a create payload */
    public String getId() {
        return id;
    }

    /** @return the mailbox name (required) */
    public String getName() {
        return name;
    }

    /** @return the parent mailbox identifier, or {@code null} */
    public String getParentId() {
        return parentId;
    }

    /** @return the role (e.g. inbox, sent), or {@code null} */
    public String getRole() {
        return role;
    }

    /** @return the sort order (default 0) */
    public long getSortOrder() {
        return sortOrder;
    }

    /** @return total number of emails, or {@code null} if not present */
    public Long getTotalEmails() {
        return totalEmails;
    }

    /** @return number of unread emails, or {@code null} if not present */
    public Long getUnreadEmails() {
        return unreadEmails;
    }

    /** @return total number of threads, or {@code null} if not present */
    public Long getTotalThreads() {
        return totalThreads;
    }

    /** @return number of unread threads, or {@code null} if not present */
    public Long getUnreadThreads() {
        return unreadThreads;
    }

    /** @return the rights object, or {@code null} if not present */
    public MailboxRights getMyRights() {
        return myRights;
    }

    /** @return whether the mailbox is subscribed (default {@code false}) */
    public boolean isSubscribed() {
        return isSubscribed;
    }

    /**
     * Parses a {@link JsonNode} representing a Mailbox into a {@code Mailbox} instance.
     *
     * @param data the JSON object node
     * @return a new {@code Mailbox}
     * @throws JmapProtocolError if required properties are missing or have an unexpected type
     */
    public static Mailbox fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidArguments", "Mailbox JSON must be an object");
        }

        JsonNode nameNode = data.get("name");
        if (nameNode == null || nameNode.isNull()) {
            throw new JmapProtocolError("invalidArguments", "Mailbox missing required property: name");
        }
        String name = nameNode.asText();

        String id = getNullableText(data.get("id"));
        String parentId = getNullableText(data.get("parentId"));
        String role = getNullableText(data.get("role"));

        long sortOrder = getLongOrDefault(data.get("sortOrder"), 0L);
        Long totalEmails = getNullableLong(data.get("totalEmails"));
        Long unreadEmails = getNullableLong(data.get("unreadEmails"));
        Long totalThreads = getNullableLong(data.get("totalThreads"));
        Long unreadThreads = getNullableLong(data.get("unreadThreads"));

        MailboxRights myRights = null;
        JsonNode rightsNode = data.get("myRights");
        if (rightsNode != null && !rightsNode.isNull()) {
            myRights = MailboxRights.fromJson(rightsNode);
        }

        boolean isSubscribed = getBooleanOrDefault(data.get("isSubscribed"), false);

        return new Mailbox(id, name, parentId, role, sortOrder,
                totalEmails, unreadEmails, totalThreads, unreadThreads,
                myRights, isSubscribed);
    }

    /**
     * Serialises this {@code Mailbox} to a {@link JsonNode}.
     *
     * @return an {@link ObjectNode} containing the mailbox fields
     */
    public JsonNode toJson() {
        ObjectNode node = MAPPER.createObjectNode();

        if (id != null) {
            node.put("id", id);
        }
        node.put("name", name);
        if (parentId != null) {
            node.put("parentId", parentId);
        }
        if (role != null) {
            node.put("role", role);
        }
        node.put("sortOrder", sortOrder);
        if (totalEmails != null) {
            node.put("totalEmails", totalEmails);
        }
        if (unreadEmails != null) {
            node.put("unreadEmails", unreadEmails);
        }
        if (totalThreads != null) {
            node.put("totalThreads", totalThreads);
        }
        if (unreadThreads != null) {
            node.put("unreadThreads", unreadThreads);
        }
        if (myRights != null) {
            node.set("myRights", myRights.toJson());
        }
        node.put("isSubscribed", isSubscribed);
        return node;
    }

    // -------------------------------------------------------------------------
    // Helper methods for JSON conversion
    // -------------------------------------------------------------------------

    private static String getNullableText(JsonNode node) {
        return (node != null && !node.isNull()) ? node.asText() : null;
    }

    private static Long getNullableLong(JsonNode node) {
        return (node != null && !node.isNull()) ? node.longValue() : null;
    }

    private static long getLongOrDefault(JsonNode node, long defaultValue) {
        return (node != null && !node.isNull()) ? node.longValue() : defaultValue;
    }

    private static boolean getBooleanOrDefault(JsonNode node, boolean defaultValue) {
        return (node != null && !node.isNull()) ? node.booleanValue() : defaultValue;
    }
}
