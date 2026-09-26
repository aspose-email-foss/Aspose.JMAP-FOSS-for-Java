package com.aspose.jmap;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents the JMAP Session resource.
 * <p>
 * The Session object is fetched once from the well‑known JMAP URL and cached.
 * It describes server capabilities, accounts, and URL templates for subsequent
 * requests.
 * </p>
 */
public final class Session {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Map<String, Object> capabilities;
    private final Map<String, Account> accounts;
    private final Map<String, String> primaryAccounts;
    private final String username;
    private final String apiUrl;
    private final String downloadUrl;
    private final String uploadUrl;
    private final String eventSourceUrl;
    private final String state;

    /**
     * Package‑visible all‑args constructor.
     */
    public Session(
            Map<String, Object> capabilities,
            Map<String, Account> accounts,
            Map<String, String> primaryAccounts,
            String username,
            String apiUrl,
            String downloadUrl,
            String uploadUrl,
            String eventSourceUrl,
            String state) {
        this.capabilities = capabilities;
        this.accounts = accounts;
        this.primaryAccounts = primaryAccounts;
        this.username = username;
        this.apiUrl = apiUrl;
        this.downloadUrl = downloadUrl;
        this.uploadUrl = uploadUrl;
        this.eventSourceUrl = eventSourceUrl;
        this.state = state;
    }

    /** @return the capabilities map (URN → capability object). */
    public Map<String, Object> getCapabilities() {
        return capabilities;
    }

    /** @return the accounts map (accountId → {@link Account}). */
    public Map<String, Account> getAccounts() {
        return accounts;
    }

    /** @return the primary accounts map (capability URN → accountId). */
    public Map<String, String> getPrimaryAccounts() {
        return primaryAccounts;
    }

    /** @return the authenticated username. */
    public String getUsername() {
        return username;
    }

    /** @return the API URL for POSTing JMAP method calls (may be relative). */
    public String getApiUrl() {
        return apiUrl;
    }

    /** @return the download URL template (may be relative). */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /** @return the upload URL template (may be relative). */
    public String getUploadUrl() {
        return uploadUrl;
    }

    /** @return the EventSource URL template (may be relative). */
    public String getEventSourceUrl() {
        return eventSourceUrl;
    }

    /** @return the opaque session state string. */
    public String getState() {
        return state;
    }

    /**
     * Resolves a possibly‑relative URL against the session's base URL.
     *
     * @param base   the base URL (the session URL)
     * @param target the possibly‑relative URL from the session object
     * @return an absolute URL string
     */
    public static String resolveUrl(String base, String target) {
        URI baseUri = URI.create(base);
        URI resolved = baseUri.resolve(target);
        return resolved.toString();
    }

    /**
     * Parses a {@code Session} from its JSON representation.
     *
     * @param data the JSON node representing the Session object
     * @return a {@code Session} instance
     * @throws JmapProtocolError if any required field is missing or has an unexpected type
     */
    public static Session fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidArguments", "Session JSON must be an object");
        }

        JsonNode capabilitiesNode = data.get("capabilities");
        JsonNode accountsNode = data.get("accounts");
        JsonNode primaryAccountsNode = data.get("primaryAccounts");
        JsonNode usernameNode = data.get("username");
        JsonNode apiUrlNode = data.get("apiUrl");
        JsonNode downloadUrlNode = data.get("downloadUrl");
        JsonNode uploadUrlNode = data.get("uploadUrl");
        JsonNode eventSourceUrlNode = data.get("eventSourceUrl");
        JsonNode stateNode = data.get("state");

        if (capabilitiesNode == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required field: capabilities");
        }
        if (accountsNode == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required field: accounts");
        }
        if (primaryAccountsNode == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required field: primaryAccounts");
        }
        if (usernameNode == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required field: username");
        }
        if (apiUrlNode == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required field: apiUrl");
        }
        if (downloadUrlNode == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required field: downloadUrl");
        }
        if (uploadUrlNode == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required field: uploadUrl");
        }
        if (eventSourceUrlNode == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required field: eventSourceUrl");
        }
        if (stateNode == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required field: state");
        }

        Map<String, Object> capabilities = MAPPER.convertValue(
                capabilitiesNode,
                new TypeReference<Map<String, Object>>() {});

        Map<String, Account> accounts = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> accFields = accountsNode.fields();
        while (accFields.hasNext()) {
            Map.Entry<String, JsonNode> entry = accFields.next();
            accounts.put(entry.getKey(), Account.fromJson(entry.getValue()));
        }

        Map<String, String> primaryAccounts = MAPPER.convertValue(
                primaryAccountsNode,
                new TypeReference<Map<String, String>>() {});

        String username = usernameNode.asText();
        String apiUrl = apiUrlNode.asText();
        String downloadUrl = downloadUrlNode.asText();
        String uploadUrl = uploadUrlNode.asText();
        String eventSourceUrl = eventSourceUrlNode.asText();
        String state = stateNode.asText();

        return new Session(
                capabilities,
                accounts,
                primaryAccounts,
                username,
                apiUrl,
                downloadUrl,
                uploadUrl,
                eventSourceUrl,
                state);
    }

    /**
     * Serialises this {@code Session} to a JSON node.
     *
     * @return a {@link JsonNode} representing this Session
     */
    public JsonNode toJson() {
        ObjectNode root = MAPPER.createObjectNode();

        root.set("capabilities", MAPPER.valueToTree(capabilities));

        ObjectNode accountsNode = MAPPER.createObjectNode();
        for (Map.Entry<String, Account> entry : accounts.entrySet()) {
            accountsNode.set(entry.getKey(), entry.getValue().toJson());
        }
        root.set("accounts", accountsNode);

        root.set("primaryAccounts", MAPPER.valueToTree(primaryAccounts));
        root.put("username", username);
        root.put("apiUrl", apiUrl);
        root.put("downloadUrl", downloadUrl);
        root.put("uploadUrl", uploadUrl);
        root.put("eventSourceUrl", eventSourceUrl);
        root.put("state", state);

        return root;
    }
}
