package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents the rights a user has on a {@link Mailbox}.
 *
 * <p>All properties correspond directly to the JMAP wire format and are therefore
 * named exactly as defined in the specification.</p>
 */
public final class MailboxRights {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Boolean mayReadItems;
    private final Boolean mayAddItems;
    private final Boolean mayRemoveItems;
    private final Boolean maySetSeen;
    private final Boolean maySetKeywords;
    private final Boolean mayCreateChild;
    private final Boolean mayRename;
    private final Boolean mayDelete;
    private final Boolean maySubmit;

    /**
     * Package‑visible all‑args constructor.
     */
    public MailboxRights(
            Boolean mayReadItems,
            Boolean mayAddItems,
            Boolean mayRemoveItems,
            Boolean maySetSeen,
            Boolean maySetKeywords,
            Boolean mayCreateChild,
            Boolean mayRename,
            Boolean mayDelete,
            Boolean maySubmit) {
        this.mayReadItems = mayReadItems;
        this.mayAddItems = mayAddItems;
        this.mayRemoveItems = mayRemoveItems;
        this.maySetSeen = maySetSeen;
        this.maySetKeywords = maySetKeywords;
        this.mayCreateChild = mayCreateChild;
        this.mayRename = mayRename;
        this.mayDelete = mayDelete;
        this.maySubmit = maySubmit;
    }

    /** @return true if the user may read items in the mailbox, or null if unspecified */
    public Boolean getMayReadItems() {
        return mayReadItems;
    }

    /** @return true if the user may add items to the mailbox, or null if unspecified */
    public Boolean getMayAddItems() {
        return mayAddItems;
    }

    /** @return true if the user may remove items from the mailbox, or null if unspecified */
    public Boolean getMayRemoveItems() {
        return mayRemoveItems;
    }

    /** @return true if the user may set the {@code \Seen} flag, or null if unspecified */
    public Boolean getMaySetSeen() {
        return maySetSeen;
    }

    /** @return true if the user may set keywords, or null if unspecified */
    public Boolean getMaySetKeywords() {
        return maySetKeywords;
    }

    /** @return true if the user may create child mailboxes, or null if unspecified */
    public Boolean getMayCreateChild() {
        return mayCreateChild;
    }

    /** @return true if the user may rename the mailbox, or null if unspecified */
    public Boolean getMayRename() {
        return mayRename;
    }

    /** @return true if the user may delete the mailbox, or null if unspecified */
    public Boolean getMayDelete() {
        return mayDelete;
    }

    /** @return true if the user may submit messages from the mailbox, or null if unspecified */
    public Boolean getMaySubmit() {
        return maySubmit;
    }

    /**
     * Parses a {@code MailboxRights} object from its JSON representation.
     *
     * @param data JSON object node representing a MailboxRights.
     * @return a {@code MailboxRights} instance.
     * @throws JmapProtocolError if the JSON is malformed.
     */
    public static MailboxRights fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidArguments",
                    "Invalid MailboxRights JSON: expected an object");
        }

        Boolean mayReadItems = getOptionalBoolean(data, "mayReadItems");
        Boolean mayAddItems = getOptionalBoolean(data, "mayAddItems");
        Boolean mayRemoveItems = getOptionalBoolean(data, "mayRemoveItems");
        Boolean maySetSeen = getOptionalBoolean(data, "maySetSeen");
        Boolean maySetKeywords = getOptionalBoolean(data, "maySetKeywords");
        Boolean mayCreateChild = getOptionalBoolean(data, "mayCreateChild");
        Boolean mayRename = getOptionalBoolean(data, "mayRename");
        Boolean mayDelete = getOptionalBoolean(data, "mayDelete");
        Boolean maySubmit = getOptionalBoolean(data, "maySubmit");

        return new MailboxRights(
                mayReadItems,
                mayAddItems,
                mayRemoveItems,
                maySetSeen,
                maySetKeywords,
                mayCreateChild,
                mayRename,
                mayDelete,
                maySubmit);
    }

    /**
     * Serialises this {@code MailboxRights} instance to a JSON object node.
     *
     * @return a {@link JsonNode} representing this object.
     */
    public JsonNode toJson() {
        ObjectNode obj = MAPPER.createObjectNode();

        if (mayReadItems != null) obj.put("mayReadItems", mayReadItems);
        if (mayAddItems != null) obj.put("mayAddItems", mayAddItems);
        if (mayRemoveItems != null) obj.put("mayRemoveItems", mayRemoveItems);
        if (maySetSeen != null) obj.put("maySetSeen", maySetSeen);
        if (maySetKeywords != null) obj.put("maySetKeywords", maySetKeywords);
        if (mayCreateChild != null) obj.put("mayCreateChild", mayCreateChild);
        if (mayRename != null) obj.put("mayRename", mayRename);
        if (mayDelete != null) obj.put("mayDelete", mayDelete);
        if (maySubmit != null) obj.put("maySubmit", maySubmit);

        return obj;
    }

    private static Boolean getOptionalBoolean(JsonNode node, String fieldName) {
        JsonNode child = node.get(fieldName);
        if (child == null || child.isNull()) {
            return null;
        }
        if (!child.isBoolean()) {
            throw new JmapProtocolError("invalidArguments",
                    "Invalid type for '" + fieldName + "': expected boolean");
        }
        return child.booleanValue();
    }
}
