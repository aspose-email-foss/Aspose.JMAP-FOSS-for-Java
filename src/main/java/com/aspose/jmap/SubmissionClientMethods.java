package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mixin interface providing EmailSubmission (submission) related convenience methods.
 *
 * <p>Implements {@link JmapCoreOperations} to gain access to {@code sendRequest(...)}.</p>
 */
public interface SubmissionClientMethods extends JmapCoreOperations {

    /** Capability URN for the submission module. */
    String SUBMISSION_CAPABILITY = "urn:ietf:params:jmap:submission";

    /** Fixed call identifier used for single‑call convenience methods. */
    String CALL_ID = "c1";

    /**
     * Sends an email submission.
     *
     * @param accountId               the account identifier
     * @param submission              the {@link EmailSubmission} to create (must not contain an {@code id})
     * @param onSuccessUpdateEmail    optional {@code onSuccessUpdateEmail} map (may be {@code null})
     * @return the created {@link EmailSubmission} as returned by the server
     * @throws JmapNetworkError   if the underlying transport fails
     * @throws JmapProtocolError  if the server returns a protocol‑level error or an unexpected response shape
     */
    default EmailSubmission send(String accountId,
                                 EmailSubmission submission,
                                 Map<String, Object> onSuccessUpdateEmail) {
        // Build the "create" map – the client‑chosen creation id is the fixed CALL_ID
        Map<String, Object> createMap = new HashMap<>();
        createMap.put(CALL_ID, submission.toJson());

        // Assemble method arguments
        Map<String, Object> args = new HashMap<>();
        args.put("accountId", accountId);
        args.put("create", createMap);
        if (onSuccessUpdateEmail != null) {
            args.put("onSuccessUpdateEmail", onSuccessUpdateEmail);
        }

        Invocation inv = new Invocation("EmailSubmission/set", args, CALL_ID);
        JmapResponseEnvelope resp = sendRequest(
                Collections.singletonList(inv),
                Collections.singletonList(SUBMISSION_CAPABILITY)
        );

        Invocation result = resp.getMethodResponses().get(0);
        @SuppressWarnings("unchecked")
        Map<String, Object> resultArgs = (Map<String, Object>) result.getArguments();
        @SuppressWarnings("unchecked")
        Map<String, Object> created = (Map<String, Object>) resultArgs.get("created");
        if (created == null) {
            throw new JmapProtocolError("invalidResponse", "Missing 'created' in EmailSubmission/set response");
        }
        Object createdEntry = created.get(CALL_ID);
        if (createdEntry == null) {
            throw new JmapProtocolError("invalidResponse", "Missing created entry for id '" + CALL_ID + "'");
        }
        JsonNode partial = (createdEntry instanceof JsonNode)
                ? (JsonNode) createdEntry
                : MAPPER.valueToTree(createdEntry);
        // Per RFC 8620 5.3, the server's "created" entry is PARTIAL - it omits properties
        // already known from the client's create payload (e.g. identityId/emailId), so it
        // must be merged onto the originally-submitted object (server fields win) before
        // parsing; passing `partial` directly to fromJson fails outright since those two
        // fields are required.
        JsonNode merged = mergeJson(submission.toJson(), partial);
        return EmailSubmission.fromJson(merged);
    }

    /**
     * Merges {@code overlay}'s fields onto a copy of {@code base} (both must be JSON
     * objects); {@code overlay}'s values win on conflict. Used to reconstruct a full object
     * from a client-submitted create payload plus the server's partial "created" response.
     */
    private static JsonNode mergeJson(JsonNode base, JsonNode overlay) {
        if (base instanceof com.fasterxml.jackson.databind.node.ObjectNode
                && overlay instanceof com.fasterxml.jackson.databind.node.ObjectNode) {
            com.fasterxml.jackson.databind.node.ObjectNode merged =
                    ((com.fasterxml.jackson.databind.node.ObjectNode) base).deepCopy();
            merged.setAll((com.fasterxml.jackson.databind.node.ObjectNode) overlay);
            return merged;
        }
        return overlay;
    }

    /**
     * Cancels a previously sent email submission.
     *
     * @param accountId      the account identifier
     * @param submissionId   the server‑assigned id of the {@link EmailSubmission} to cancel
     * @throws JmapNetworkError   if the underlying transport fails
     * @throws JmapProtocolError  if the server returns a protocol‑level error
     */
    default void cancelSend(String accountId, String submissionId) {
        // Build the "update" map with the required patch. A PatchObject key is a JSON
        // Pointer (RFC 6901) relative to the object being patched - a bare top-level
        // property name has no leading slash; "/undoStatus" would instead point at a
        // property literally named the empty string, which a real JMAP server
        // rejects/ignores.
        Map<String, Object> patch = new HashMap<>();
        patch.put("undoStatus", "canceled");

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(submissionId, patch);

        Map<String, Object> args = new HashMap<>();
        args.put("accountId", accountId);
        args.put("update", updateMap);

        Invocation inv = new Invocation("EmailSubmission/set", args, CALL_ID);
        // Any protocol error will be thrown by sendRequest
        sendRequest(
                Collections.singletonList(inv),
                Collections.singletonList(SUBMISSION_CAPABILITY)
        );
    }

    /**
     * Lists all email submissions for the given account.
     *
     * @param accountId the account identifier
     * @return a list of {@link EmailSubmission} objects
     * @throws JmapNetworkError   if the underlying transport fails
     * @throws JmapProtocolError  if the server returns a protocol‑level error or an unexpected response shape
     */
    default List<EmailSubmission> listSubmissions(String accountId) {
        Map<String, Object> args = new HashMap<>();
        args.put("accountId", accountId);
        args.put("ids", null);
        args.put("properties", null);

        Invocation inv = new Invocation("EmailSubmission/get", args, CALL_ID);
        JmapResponseEnvelope resp = sendRequest(
                Collections.singletonList(inv),
                Collections.singletonList(SUBMISSION_CAPABILITY)
        );

        Invocation result = resp.getMethodResponses().get(0);
        @SuppressWarnings("unchecked")
        Map<String, Object> resultArgs = (Map<String, Object>) result.getArguments();
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) resultArgs.get("list");
        if (list == null) {
            return Collections.emptyList();
        }

        List<EmailSubmission> out = new ArrayList<>(list.size());
        for (Object item : list) {
            JsonNode node = (item instanceof JsonNode) ? (JsonNode) item : MAPPER.valueToTree(item);
            out.add(EmailSubmission.fromJson(node));
        }
        return out;
    }

    /** Shared mapper for converting between {@link Map}s and {@link JsonNode}s. */
    ObjectMapper MAPPER = new ObjectMapper();
}
