package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of an {@code Email/query} call.
 *
 * <p>Corresponds to the JMAP {@code Email/query} response object (RFC 8620, section 5.5).</p>
 */
public final class EmailQueryResponse {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String accountId;
    private final String queryState;
    private final boolean canCalculateChanges;
    private final int position;
    private final List<String> ids;
    private final Long total;   // nullable, may be omitted by the server
    private final Integer limit; // nullable, may be omitted by the server

    /**
     * Package‑visible all‑args constructor.
     *
     * @param accountId            the JMAP account identifier (required)
     * @param queryState           the current state of the query result set (required)
     * @param canCalculateChanges  whether {@code Email/queryChanges} can be used to fetch updates
     * @param position             the zero‑based index of the first id in {@code ids} (required)
     * @param ids                  the list of matching email ids (required, may be empty)
     * @param total                the total number of results found, or {@code null} if not requested
     * @param limit                the limit applied by the server, or {@code null} if not present
     */
    public EmailQueryResponse(String accountId, String queryState, boolean canCalculateChanges,
            int position, List<String> ids, Long total, Integer limit) {
        this.accountId = accountId;
        this.queryState = queryState;
        this.canCalculateChanges = canCalculateChanges;
        this.position = position;
        this.ids = ids != null ? Collections.unmodifiableList(ids) : Collections.emptyList();
        this.total = total;
        this.limit = limit;
    }

    /** @return the JMAP account identifier */
    public String getAccountId() {
        return accountId;
    }

    /** @return the current state of the query result set */
    public String getQueryState() {
        return queryState;
    }

    /** @return {@code true} if {@code Email/queryChanges} can be used to fetch updates */
    public boolean isCanCalculateChanges() {
        return canCalculateChanges;
    }

    /** @return the zero‑based index of the first id in {@link #getIds()} */
    public int getPosition() {
        return position;
    }

    /** @return the immutable list of matching email ids (never {@code null}) */
    public List<String> getIds() {
        return ids;
    }

    /** @return the total number of results found, or {@code null} if not requested */
    public Long getTotal() {
        return total;
    }

    /** @return the limit applied by the server, or {@code null} if not present */
    public Integer getLimit() {
        return limit;
    }

    /**
     * Parses an {@code EmailQueryResponse} from its JSON representation.
     *
     * @param data the JSON node representing the {@code Email/query} response arguments
     * @return an {@code EmailQueryResponse} instance
     * @throws JmapProtocolError if required fields are missing or have invalid types
     */
    public static EmailQueryResponse fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidResponse", "EmailQueryResponse JSON must be an object");
        }

        JsonNode accountIdNode = data.get("accountId");
        if (accountIdNode == null || accountIdNode.isNull() || !accountIdNode.isTextual()) {
            throw new JmapProtocolError("invalidResponse",
                    "Missing required field 'accountId' or it is not a string");
        }
        String accountId = accountIdNode.asText();

        JsonNode queryStateNode = data.get("queryState");
        if (queryStateNode == null || queryStateNode.isNull() || !queryStateNode.isTextual()) {
            throw new JmapProtocolError("invalidResponse",
                    "Missing required field 'queryState' or it is not a string");
        }
        String queryState = queryStateNode.asText();

        JsonNode canCalculateChangesNode = data.get("canCalculateChanges");
        if (canCalculateChangesNode == null || !canCalculateChangesNode.isBoolean()) {
            throw new JmapProtocolError("invalidResponse",
                    "Missing required field 'canCalculateChanges' or it is not a boolean");
        }
        boolean canCalculateChanges = canCalculateChangesNode.asBoolean();

        JsonNode positionNode = data.get("position");
        if (positionNode == null || positionNode.isNull() || !positionNode.isNumber()) {
            throw new JmapProtocolError("invalidResponse",
                    "Missing required field 'position' or it is not a number");
        }
        int position = positionNode.asInt();

        JsonNode idsNode = data.get("ids");
        if (idsNode == null || !idsNode.isArray()) {
            throw new JmapProtocolError("invalidResponse",
                    "Missing required field 'ids' or it is not an array");
        }
        List<String> ids = new ArrayList<>();
        for (JsonNode idNode : idsNode) {
            if (!idNode.isTextual()) {
                throw new JmapProtocolError("invalidResponse", "Invalid entry in 'ids', expected string");
            }
            ids.add(idNode.asText());
        }

        JsonNode totalNode = data.get("total");
        Long total = (totalNode != null && !totalNode.isNull()) ? totalNode.longValue() : null;

        JsonNode limitNode = data.get("limit");
        Integer limit = (limitNode != null && !limitNode.isNull()) ? limitNode.intValue() : null;

        return new EmailQueryResponse(accountId, queryState, canCalculateChanges, position, ids, total, limit);
    }

    /**
     * Serialises this {@code EmailQueryResponse} to a JSON object.
     *
     * @return a {@link JsonNode} representing this instance
     */
    public JsonNode toJson() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("accountId", accountId);
        node.put("queryState", queryState);
        node.put("canCalculateChanges", canCalculateChanges);
        node.put("position", position);

        ArrayNode idsArray = node.putArray("ids");
        for (String id : ids) {
            idsArray.add(id);
        }

        if (total != null) {
            node.put("total", total);
        }
        if (limit != null) {
            node.put("limit", limit);
        }
        return node;
    }
}
