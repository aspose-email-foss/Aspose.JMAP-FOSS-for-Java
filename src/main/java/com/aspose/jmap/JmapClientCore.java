package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Core client implementation providing low‑level JMAP operations.
 * <p>
 * Implements {@link JmapCoreOperations} and supplies the {@code sendRequest}
 * helper used by higher‑level mixin interfaces.
 */
public class JmapClientCore implements JmapCoreOperations {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String CORE_CAPABILITY = "urn:ietf:params:jmap:core";

    private final JmapClientOptions options;
    private final JmapTransport transport;
    private final String authHeader;

    private Session session;
    private String apiUrl;
    private String uploadUrlTemplate;
    private String downloadUrlTemplate;
    private String eventSourceUrl;

    /**
     * Constructs a core client with the given options.
     *
     * @param options configuration for the client
     */
    public JmapClientCore(JmapClientOptions options) {
        this.options = options;
        this.transport = options.getTransport() != null ? options.getTransport() : new HttpJmapTransport();
        this.authHeader = resolveAuthHeader(options);
    }

    /**
     * Computes the {@code Authorization} header value for this client: an OAuth 2.0 bearer
     * token (per <a href="https://www.rfc-editor.org/rfc/rfc6750">RFC 6750</a>) when
     * {@link JmapClientOptions#getBearerToken()} is set to a non-empty value, otherwise HTTP
     * Basic authentication built from the configured username/password.
     */
    private static String resolveAuthHeader(JmapClientOptions options) {
        String bearerToken = options.getBearerToken();
        if (bearerToken != null && !bearerToken.isEmpty()) {
            return "Bearer " + bearerToken;
        }
        return "Basic " + Base64.getEncoder()
                .encodeToString((options.getUsername() + ":" + options.getPassword())
                .getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Retrieves the JMAP session object from the well‑known URL.
     *
     * @return the {@link Session} describing the server capabilities and URLs
     * @throws JmapNetworkError  if the HTTP request fails or returns a non‑200 status
     * @throws JmapProtocolError if the response cannot be parsed as a {@link Session}
     */
    public Session connect() {
        JmapHttpRequest request = new JmapHttpRequest(
                "GET",
                options.getSessionUrl(),
                Map.of("Authorization", List.of(authHeader)),
                null
        );

        JmapHttpResponse response = transport.send(request);
        if (response.getStatusCode() != 200) {
            throw new JmapNetworkError("Failed to fetch session: HTTP " + response.getStatusCode());
        }

        JsonNode bodyNode;
        try {
            bodyNode = MAPPER.readTree(response.getBody());
        } catch (Exception e) {
            throw new JmapProtocolError("InvalidSessionResponse", "Unable to parse session JSON");
        }

        Session sess = Session.fromJson(bodyNode);
        this.session = sess;

        // Resolve relative URLs against the session URL's origin
        String origin = originOf(options.getSessionUrl());
        this.apiUrl = resolveUrl(sess.getApiUrl(), origin);
        this.uploadUrlTemplate = resolveUrl(sess.getUploadUrl(), origin);
        this.downloadUrlTemplate = resolveUrl(sess.getDownloadUrl(), origin);
        this.eventSourceUrl = resolveUrl(sess.getEventSourceUrl(), origin);

        return sess;
    }

    /**
     * Sends a batch of JMAP method calls.
     *
     * @param methodCalls list of {@link Invocation} objects representing the calls
     * @param using       list of capability URNs required for the calls
     * @return the parsed {@link JmapResponseEnvelope}
     * @throws JmapNetworkError  if the HTTP request fails or returns a non‑200 status
     * @throws JmapProtocolError if the server returns a method‑level error
     */
    @Override
    public JmapResponseEnvelope sendRequest(List<Invocation> methodCalls, List<String> using) {
        if (apiUrl == null) {
            throw new IllegalStateException("Client not connected; call connect() first");
        }

        // The third argument (extraHeaders) is empty for core requests.
        JmapRequestEnvelope envelope = new JmapRequestEnvelope(using, methodCalls, Collections.emptyMap());
        JsonNode json = envelope.toJson();

        JmapHttpRequest request = new JmapHttpRequest(
                "POST",
                apiUrl,
                Map.of(
                        "Authorization", List.of(authHeader),
                        "Content-Type", List.of("application/json")
                ),
                json.toString().getBytes(StandardCharsets.UTF_8)
        );

        JmapHttpResponse response = transport.send(request);
        if (response.getStatusCode() != 200) {
            throw new JmapNetworkError("JMAP request failed: HTTP " + response.getStatusCode());
        }

        JsonNode respNode;
        try {
            respNode = MAPPER.readTree(response.getBody());
        } catch (Exception e) {
            throw new JmapProtocolError("InvalidResponse", "Unable to parse JMAP response JSON");
        }

        JmapResponseEnvelope respEnvelope = JmapResponseEnvelope.fromJson(respNode);

        // Detect method‑level errors
        for (Invocation inv : respEnvelope.getMethodResponses()) {
            if ("error".equals(inv.getName())) {
                @SuppressWarnings("unchecked")
                Map<String, Object> args = inv.getArguments();
                String type = (String) args.getOrDefault("type", "unknown");
                String description = (String) args.getOrDefault("description", "");
                throw new JmapProtocolError(type, description);
            }
        }

        return respEnvelope;
    }

    /**
     * Calls the Core/echo method, which returns the supplied arguments unchanged.
     *
     * @param arguments arbitrary JSON‑compatible map to be echoed
     * @return the echoed map from the server
     */
    public Map<String, Object> echo(Map<String, Object> arguments) {
        Invocation inv = new Invocation("Core/echo", arguments, java.util.UUID.randomUUID().toString());
        JmapResponseEnvelope resp = sendRequest(
                Collections.singletonList(inv),
                Collections.singletonList(CORE_CAPABILITY)
        );
        Invocation result = resp.getMethodResponses().get(0);
        @SuppressWarnings("unchecked")
        Map<String, Object> echoed = result.getArguments();
        return echoed;
    }

    /**
     * Uploads a binary blob to the server.
     *
     * @param accountId   the account identifier
     * @param data        raw bytes of the blob
     * @param contentType MIME type of the blob (e.g. {@code "message/rfc822"})
     * @return an {@code UploadBlobResponse} describing the stored blob
     */
    public UploadBlobResponse uploadBlob(String accountId, byte[] data, String contentType) {
        String url = uploadUrlTemplate.replace("{accountId}", encodeUrlSegment(accountId));
        JmapHttpRequest request = new JmapHttpRequest(
                "POST",
                url,
                Map.of(
                        "Authorization", List.of(authHeader),
                        "Content-Type", List.of(contentType)
                ),
                data
        );

        JmapHttpResponse response = transport.send(request);
        int status = response.getStatusCode();
        if (status != 200 && status != 201) {
            throw new JmapNetworkError("Blob upload failed: HTTP " + status);
        }

        JsonNode bodyNode;
        try {
            bodyNode = MAPPER.readTree(response.getBody());
        } catch (Exception e) {
            throw new JmapProtocolError("InvalidUploadBlobResponse", "Unable to parse upload response");
        }

        return UploadBlobResponse.fromJson(bodyNode);
    }

    /**
     * Downloads a previously uploaded blob.
     *
     * @param accountId the account identifier
     * @param blobId    the server‑assigned blob identifier
     * @param type      the MIME type placeholder (may be {@code "*"} if unknown)
     * @param name      the filename placeholder (may be {@code "*"} if unknown)
     * @return raw bytes of the blob
     */
    public byte[] downloadBlob(String accountId, String blobId, String type, String name) {
        String url = downloadUrlTemplate
                .replace("{accountId}", encodeUrlSegment(accountId))
                .replace("{blobId}", encodeUrlSegment(blobId))
                .replace("{type}", encodeUrlSegment(type))
                .replace("{name}", encodeUrlSegment(name));

        JmapHttpRequest request = new JmapHttpRequest(
                "GET",
                url,
                Map.of("Authorization", List.of(authHeader)),
                null
        );

        JmapHttpResponse response = transport.send(request);
        if (response.getStatusCode() != 200) {
            throw new JmapNetworkError("Blob download failed: HTTP " + response.getStatusCode());
        }

        return response.getBody();
    }

    // -------------------------------------------------------------------------
    // Helper methods
    // -------------------------------------------------------------------------

    /**
     * Percent-encodes a value substituted into a URL template placeholder (e.g.
     * {@code {accountId}}). These values are spliced verbatim into a URL path segment, and
     * {@code /}, {@code ?}, {@code #}, etc. in them would otherwise rewrite the request path
     * or smuggle extra query parameters into the request.
     *
     * <p>{@link URLEncoder} encodes for {@code application/x-www-form-urlencoded} (space
     * becomes {@code +}), not a URI path segment (space should become {@code %20}) - the
     * trailing {@code replace} corrects that one difference.
     */
    private static String encodeUrlSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    /**
     * Extracts the scheme+authority ("origin") from an absolute URL, e.g.
     * {@code https://example.com/.well-known/jmap} -&gt; {@code https://example.com}.
     */
    private static String originOf(String absoluteUrl) {
        int schemeEnd = absoluteUrl.indexOf("://");
        if (schemeEnd < 0) {
            return absoluteUrl;
        }
        int pathStart = absoluteUrl.indexOf('/', schemeEnd + 3);
        return pathStart < 0 ? absoluteUrl : absoluteUrl.substring(0, pathStart);
    }

    /**
     * Resolves a possibly-relative URL (or URI-template, e.g. {@code /upload/{accountId}})
     * against the given origin. Deliberately hand-rolled string logic, NEVER {@link URI} or
     * {@link java.net.URL} - both reject/mangle the literal {@code {}} characters a
     * not-yet-substituted URI-template placeholder contains, corrupting it before the
     * caller ever gets a chance to {@code .replace()} it (confirmed: {@code URI.create()}
     * throws outright on a literal {@code {}} in the path).
     */
    private static String resolveUrl(String candidate, String origin) {
        if (candidate == null) {
            return null;
        }
        if (candidate.startsWith("http://") || candidate.startsWith("https://")) {
            return candidate;
        }
        return candidate.startsWith("/") ? origin + candidate : origin + "/" + candidate;
    }
}

/**
 * Core operations interface required by mixin client modules.
 * <p>
 * Only the {@code sendRequest} method is abstract; all other higher‑level
 * JMAP calls are provided as default methods in module‑specific interfaces.
 */
interface JmapCoreOperations {
    /**
     * Sends a batch of JMAP method calls.
     *
     * @param methodCalls list of {@link Invocation} objects representing the calls
     * @param using       list of capability URNs required for the calls
     * @return the parsed {@link JmapResponseEnvelope}
     */
    JmapResponseEnvelope sendRequest(List<Invocation> methodCalls, List<String> using);
}

/**
 * Model representing the response to an {@code uploadBlob} request.
 * <p>
 * This class is immutable and provides {@code fromJson} / {@code toJson}
 * methods consistent with the library's model conventions.
 */
final class UploadBlobResponse {
    private final String accountId;
    private final String blobId;
    private final String type;
    private final long size;

    UploadBlobResponse(String accountId, String blobId, String type, long size) {
        this.accountId = accountId;
        this.blobId = blobId;
        this.type = type;
        this.size = size;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getBlobId() {
        return blobId;
    }

    public String getType() {
        return type;
    }

    public long getSize() {
        return size;
    }

    /**
     * Parses an {@code UploadBlobResponse} from a JSON node.
     *
     * @param data JSON object node representing the response
     * @return a new {@code UploadBlobResponse} instance
     * @throws JmapProtocolError if required fields are missing or of wrong type
     */
    public static UploadBlobResponse fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("InvalidUploadBlobResponse", "Response must be a JSON object");
        }

        JsonNode accountIdNode = data.get("accountId");
        JsonNode blobIdNode = data.get("blobId");
        JsonNode typeNode = data.get("type");
        JsonNode sizeNode = data.get("size");

        if (accountIdNode == null || !accountIdNode.isTextual()
                || blobIdNode == null || !blobIdNode.isTextual()
                || typeNode == null || !typeNode.isTextual()
                || sizeNode == null || !sizeNode.canConvertToLong()) {
            throw new JmapProtocolError("InvalidUploadBlobResponse", "Missing or invalid required fields");
        }

        return new UploadBlobResponse(
                accountIdNode.asText(),
                blobIdNode.asText(),
                typeNode.asText(),
                sizeNode.longValue()
        );
    }

    /**
     * Serialises this response to a JSON object node.
     *
     * @return a JSON object representing the response
     */
    public JsonNode toJson() {
        ObjectNode obj = MAPPER.createObjectNode();
        obj.put("accountId", accountId);
        obj.put("blobId", blobId);
        obj.put("type", type);
        obj.put("size", size);
        return obj;
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();
}
