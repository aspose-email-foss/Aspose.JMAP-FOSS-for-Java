package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mixin interface providing Mail module convenience methods.
 *
 * <p>All methods send a single JMAP invocation using {@link #sendRequest}
 * from {@link JmapCoreOperations} and return deserialized model objects.</p>
 */
public interface MailClientMethods extends JmapCoreOperations {

    /** Shared Jackson mapper for converting between {@link Object} and {@link JsonNode}. */
    ObjectMapper MAPPER = new ObjectMapper();

    /** Capability URN required for all mail‑module calls. */
    String MAIL_CAPABILITY = "urn:ietf:params:jmap:mail";

    /** Fixed call identifier used for the single‑call convenience methods. */
    String CALL_ID = "c1";

    /**
     * Retrieves all mailboxes for the given account.
     *
     * @param accountId the JMAP account identifier
     * @return list of {@link Mailbox} objects
     */
    default List<Mailbox> listMailboxes(String accountId) {
        Map<String, Object> args = Map.of("accountId", accountId);
        Invocation inv = new Invocation("Mailbox/get", args, CALL_ID);
        JmapResponseEnvelope resp = sendRequest(
                Collections.singletonList(inv),
                Collections.singletonList(MAIL_CAPABILITY)
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) resp.getMethodResponses()
                .get(0).getArguments();

        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) result.get("list");
        List<Mailbox> out = new ArrayList<>();
        if (list != null) {
            for (Object o : list) {
                JsonNode node = MAPPER.valueToTree(o);
                out.add(Mailbox.fromJson(node));
            }
        }
        return out;
    }

    /**
     * Retrieves specific mailboxes by their identifiers.
     *
     * @param accountId the JMAP account identifier
     * @param ids       list of mailbox ids to fetch
     * @return list of {@link Mailbox} objects
     */
    default List<Mailbox> getMailbox(String accountId, List<String> ids) {
        Map<String, Object> args = Map.of(
                "accountId", accountId,
                "ids", ids
        );
        Invocation inv = new Invocation("Mailbox/get", args, CALL_ID);
        JmapResponseEnvelope resp = sendRequest(
                Collections.singletonList(inv),
                Collections.singletonList(MAIL_CAPABILITY)
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) resp.getMethodResponses()
                .get(0).getArguments();

        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) result.get("list");
        List<Mailbox> out = new ArrayList<>();
        if (list != null) {
            for (Object o : list) {
                JsonNode node = MAPPER.valueToTree(o);
                out.add(Mailbox.fromJson(node));
            }
        }
        return out;
    }

    /**
     * Creates a new mailbox.
     *
     * @param accountId the JMAP account identifier
     * @param mailbox   the client‑side {@link Mailbox} representation (without server‑assigned fields)
     * @return the created {@link Mailbox} with server‑assigned fields merged
     */
    default Mailbox createMailbox(String accountId, Mailbox mailbox) {
        // client‑chosen creation id – any opaque string works; using "new" for simplicity
        Map<String, Object> createMap = Map.of("new", mailbox.toJson());
        Map<String, Object> args = Map.of(
                "accountId", accountId,
                "create", createMap
        );
        Invocation inv = new Invocation("Mailbox/set", args, CALL_ID);
        JmapResponseEnvelope resp = sendRequest(
                Collections.singletonList(inv),
                Collections.singletonList(MAIL_CAPABILITY)
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) resp.getMethodResponses()
                .get(0).getArguments();

        @SuppressWarnings("unchecked")
        Map<String, Object> created = (Map<String, Object>) result.get("created");
        if (created == null || created.isEmpty()) {
            throw new JmapProtocolError("invalidResponse", "Mailbox/set did not return a created entry");
        }

        // Exactly one entry – the key is the client‑chosen id, the value is the partial server object
        Map.Entry<String, Object> entry = created.entrySet().iterator().next();
        JsonNode serverNode = MAPPER.valueToTree(entry.getValue());

        // Merge server fields over the client‑side object (server wins on conflict)
        ObjectNode merged = ((ObjectNode) mailbox.toJson()).deepCopy();
        merged.setAll((ObjectNode) serverNode);
        return Mailbox.fromJson(merged);
    }

    /**
     * Deletes the specified mailboxes.
     *
     * @param accountId the JMAP account identifier
     * @param ids       list of mailbox ids to delete
     * @return list of ids that were destroyed (may be empty)
     */
    default List<String> deleteMailbox(String accountId, List<String> ids) {
        Map<String, Object> args = Map.of(
                "accountId", accountId,
                "destroy", ids
        );
        Invocation inv = new Invocation("Mailbox/set", args, CALL_ID);
        JmapResponseEnvelope resp = sendRequest(
                Collections.singletonList(inv),
                Collections.singletonList(MAIL_CAPABILITY)
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) resp.getMethodResponses()
                .get(0).getArguments();

        @SuppressWarnings("unchecked")
        List<String> destroyed = (List<String>) result.get("destroyed");
        return destroyed != null ? destroyed : Collections.emptyList();
    }

    /**
     * Lists message ids for the given account (simple {@code Email/query} with no filter,
     * no sort, no limit, and starting at position {@code 0}).
     *
     * @param accountId the JMAP account identifier
     * @return the {@link EmailQueryResponse} describing the matching ids and pagination info
     */
    default EmailQueryResponse listMessages(String accountId) {
        return listMessages(accountId, null, null, null, 0);
    }

    /**
     * Performs an {@code Email/query} to list message ids matching the supplied filter and
     * sort criteria.
     *
     * @param accountId the JMAP account identifier
     * @param filter    optional filter object, serialised as-is into the {@code filter}
     *                  argument (may be {@code null} for no filtering)
     * @param sort      optional list of {@link Comparator} objects controlling result order
     *                  (may be {@code null} or empty for server-default order)
     * @param limit     optional maximum number of ids to return (may be {@code null})
     * @param position  the zero-based index of the first result to return (default {@code 0})
     * @return the {@link EmailQueryResponse} describing the matching ids and pagination info
     */
    default EmailQueryResponse listMessages(String accountId, Map<String, Object> filter,
            List<Comparator> sort, Integer limit, int position) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("accountId", accountId);
        if (filter != null) {
            args.put("filter", filter);
        }
        if (sort != null && !sort.isEmpty()) {
            List<JsonNode> sortJson = new ArrayList<>();
            for (Comparator c : sort) {
                sortJson.add(c.toJson());
            }
            args.put("sort", sortJson);
        }
        if (limit != null) {
            args.put("limit", limit);
        }
        args.put("position", position);

        Invocation inv = new Invocation("Email/query", args, CALL_ID);
        JmapResponseEnvelope resp = sendRequest(
                Collections.singletonList(inv),
                Collections.singletonList(MAIL_CAPABILITY)
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) resp.getMethodResponses()
                .get(0).getArguments();

        JsonNode node = MAPPER.valueToTree(result);
        return EmailQueryResponse.fromJson(node);
    }

    /**
     * Retrieves a single message.
     *
     * @param accountId  the JMAP account identifier
     * @param id         the message id
     * @param properties optional list of properties to fetch (may be {@code null})
     * @return the {@link Email} object
     */
    default Email fetchMessage(String accountId, String id, List<String> properties) {
        Map<String, Object> args = (properties == null)
                ? Map.of("accountId", accountId, "ids", List.of(id))
                : Map.of("accountId", accountId, "ids", List.of(id), "properties", properties);

        Invocation inv = new Invocation("Email/get", args, CALL_ID);
        JmapResponseEnvelope resp = sendRequest(
                Collections.singletonList(inv),
                Collections.singletonList(MAIL_CAPABILITY)
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) resp.getMethodResponses()
                .get(0).getArguments();

        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) result.get("list");
        if (list == null || list.isEmpty()) {
            throw new JmapProtocolError("notFound", "Email not found");
        }
        JsonNode node = MAPPER.valueToTree(list.get(0));
        return Email.fromJson(node);
    }

    /**
     * Moves a message to a different mailbox.
     *
     * @param accountId the JMAP account identifier
     * @param emailId   the message id
     * @param mailboxId the target mailbox id
     * @return the partial update map returned by the server (may be empty)
     */
    default Map<String, Object> moveMessage(String accountId, String emailId, String mailboxId) {
        Map<String, Object> mailboxIds = Map.of(mailboxId, Boolean.TRUE);
        Map<String, Object> updateEntry = Map.of("mailboxIds", mailboxIds);
        Map<String, Object> args = Map.of(
                "accountId", accountId,
                "update", Map.of(emailId, updateEntry)
        );

        Invocation inv = new Invocation("Email/set", args, CALL_ID);
        JmapResponseEnvelope resp = sendRequest(
                Collections.singletonList(inv),
                Collections.singletonList(MAIL_CAPABILITY)
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) resp.getMethodResponses()
                .get(0).getArguments();

        @SuppressWarnings("unchecked")
        Map<String, Object> updated = (Map<String, Object>) result.get("updated");
        if (updated != null && updated.get(emailId) instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> partial = (Map<String, Object>) updated.get(emailId);
            return partial;
        }
        return Collections.emptyMap();
    }

    /**
     * Sets or clears a keyword on a message.
     *
     * @param accountId the JMAP account identifier
     * @param emailId   the message id
     * @param keyword   the keyword name
     * @param value     {@code true} to set, {@code false} to clear
     * @return the partial update map returned by the server (may be empty)
     */
    default Map<String, Object> setMessageKeyword(String accountId, String emailId,
                                                  String keyword, boolean value) {
        Map<String, Object> keywords = Map.of(keyword, value);
        Map<String, Object> updateEntry = Map.of("keywords", keywords);
        Map<String, Object> args = Map.of(
                "accountId", accountId,
                "update", Map.of(emailId, updateEntry)
        );

        Invocation inv = new Invocation("Email/set", args, CALL_ID);
        JmapResponseEnvelope resp = sendRequest(
                Collections.singletonList(inv),
                Collections.singletonList(MAIL_CAPABILITY)
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) resp.getMethodResponses()
                .get(0).getArguments();

        @SuppressWarnings("unchecked")
        Map<String, Object> updated = (Map<String, Object>) result.get("updated");
        if (updated != null && updated.get(emailId) instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> partial = (Map<String, Object>) updated.get(emailId);
            return partial;
        }
        return Collections.emptyMap();
    }

    /**
     * Deletes the specified messages.
     *
     * @param accountId the JMAP account identifier
     * @param ids       list of message ids to delete
     * @return list of ids that were destroyed (may be empty)
     */
    default List<String> deleteMessage(String accountId, List<String> ids) {
        Map<String, Object> args = Map.of(
                "accountId", accountId,
                "destroy", ids
        );
        Invocation inv = new Invocation("Email/set", args, CALL_ID);
        JmapResponseEnvelope resp = sendRequest(
                Collections.singletonList(inv),
                Collections.singletonList(MAIL_CAPABILITY)
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) resp.getMethodResponses()
                .get(0).getArguments();

        @SuppressWarnings("unchecked")
        List<String> destroyed = (List<String>) result.get("destroyed");
        return destroyed != null ? destroyed : Collections.emptyList();
    }

    /**
     * Retrieves all identities for the given account.
     *
     * @param accountId the JMAP account identifier
     * @return list of {@link Identity} objects (may be empty)
     */
    default List<Identity> listIdentities(String accountId) {
        Map<String, Object> args = Map.of("accountId", accountId);
        Invocation inv = new Invocation("Identity/get", args, CALL_ID);
        JmapResponseEnvelope resp = sendRequest(
                Collections.singletonList(inv),
                Collections.singletonList(MAIL_CAPABILITY)
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) resp.getMethodResponses()
                .get(0).getArguments();

        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) result.get("list");
        List<Identity> out = new ArrayList<>();
        if (list != null) {
            for (Object o : list) {
                JsonNode node = MAPPER.valueToTree(o);
                out.add(Identity.fromJson(node));
            }
        }
        return out;
    }
}
