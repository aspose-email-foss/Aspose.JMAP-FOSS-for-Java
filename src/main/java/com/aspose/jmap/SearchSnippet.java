package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Highlighted subject/preview snippet for an Email id matched by a filter's text search.
 *
 * <p>Corresponds to the JMAP {@code SearchSnippet} data object.</p>
 */
public final class SearchSnippet {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String emailId;
    private final String subject; // nullable
    private final String preview; // nullable

    /**
     * Package‑visible all‑args constructor.
     *
     * @param emailId the identifier of the email (required)
     * @param subject the highlighted subject, may contain {@code <mark>} tags (optional)
     * @param preview the highlighted preview text (optional)
     */
    public SearchSnippet(String emailId, String subject, String preview) {
        this.emailId = emailId;
        this.subject = subject;
        this.preview = preview;
    }

    /** @return the email identifier */
    public String getEmailId() {
        return emailId;
    }

    /** @return the highlighted subject, or {@code null} if not present */
    public String getSubject() {
        return subject;
    }

    /** @return the highlighted preview, or {@code null} if not present */
    public String getPreview() {
        return preview;
    }

    /**
     * Parses a {@code SearchSnippet} from its JSON representation.
     *
     * @param data the JSON node representing the object
     * @return a {@code SearchSnippet} instance
     * @throws JmapProtocolError if required properties are missing or have invalid types
     */
    public static SearchSnippet fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalid", "SearchSnippet JSON must be an object");
        }

        JsonNode emailIdNode = data.get("emailId");
        if (emailIdNode == null || emailIdNode.isNull() || !emailIdNode.isTextual()) {
            throw new JmapProtocolError("invalid", "Missing required property emailId");
        }
        String emailId = emailIdNode.asText();

        JsonNode subjectNode = data.get("subject");
        String subject = (subjectNode != null && !subjectNode.isNull()) ? subjectNode.asText() : null;

        JsonNode previewNode = data.get("preview");
        String preview = (previewNode != null && !previewNode.isNull()) ? previewNode.asText() : null;

        return new SearchSnippet(emailId, subject, preview);
    }

    /**
     * Serialises this {@code SearchSnippet} to a JSON object node.
     *
     * @return a {@link JsonNode} representing this instance
     */
    public JsonNode toJson() {
        ObjectNode obj = MAPPER.createObjectNode();
        obj.put("emailId", emailId);
        if (subject != null) {
            obj.put("subject", subject);
        }
        if (preview != null) {
            obj.put("preview", preview);
        }
        return obj;
    }
}
