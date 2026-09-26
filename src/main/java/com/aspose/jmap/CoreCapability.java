package com.aspose.jmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the {@code urn:ietf:params:jmap:core} capability object.
 *
 * <p>This class is immutable and provides {@code fromJson} / {@code toJson}
 * methods for explicit JSON (de)serialization.</p>
 */
public final class CoreCapability {
    /** The URN identifying this capability. */
    public static final String URN = "urn:ietf:params:jmap:core";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final long maxSizeUpload;
    private final long maxConcurrentUpload;
    private final long maxSizeRequest;
    private final long maxConcurrentRequests;
    private final long maxCallsInRequest;
    private final long maxObjectsInGet;
    private final long maxObjectsInSet;
    private final List<String> collationAlgorithms;

    /**
     * Package‑visible all‑args constructor.
     *
     * @param maxSizeUpload          maximum size of a single upload (bytes)
     * @param maxConcurrentUpload    maximum number of concurrent uploads
     * @param maxSizeRequest         maximum size of a request (bytes)
     * @param maxConcurrentRequests  maximum number of concurrent requests
     * @param maxCallsInRequest      maximum number of method calls in a single request
     * @param maxObjectsInGet        maximum number of objects that can be retrieved in a {@code get} call
     * @param maxObjectsInSet        maximum number of objects that can be set in a {@code set} call
     * @param collationAlgorithms    list of supported collation algorithm names
     */
    public CoreCapability(
            long maxSizeUpload,
            long maxConcurrentUpload,
            long maxSizeRequest,
            long maxConcurrentRequests,
            long maxCallsInRequest,
            long maxObjectsInGet,
            long maxObjectsInSet,
            List<String> collationAlgorithms) {
        this.maxSizeUpload = maxSizeUpload;
        this.maxConcurrentUpload = maxConcurrentUpload;
        this.maxSizeRequest = maxSizeRequest;
        this.maxConcurrentRequests = maxConcurrentRequests;
        this.maxCallsInRequest = maxCallsInRequest;
        this.maxObjectsInGet = maxObjectsInGet;
        this.maxObjectsInSet = maxObjectsInSet;
        this.collationAlgorithms = Collections.unmodifiableList(new ArrayList<>(collationAlgorithms));
    }

    /** @return maximum size of a single upload (bytes) */
    public long getMaxSizeUpload() {
        return maxSizeUpload;
    }

    /** @return maximum number of concurrent uploads */
    public long getMaxConcurrentUpload() {
        return maxConcurrentUpload;
    }

    /** @return maximum size of a request (bytes) */
    public long getMaxSizeRequest() {
        return maxSizeRequest;
    }

    /** @return maximum number of concurrent requests */
    public long getMaxConcurrentRequests() {
        return maxConcurrentRequests;
    }

    /** @return maximum number of method calls in a single request */
    public long getMaxCallsInRequest() {
        return maxCallsInRequest;
    }

    /** @return maximum number of objects that can be retrieved in a {@code get} call */
    public long getMaxObjectsInGet() {
        return maxObjectsInGet;
    }

    /** @return maximum number of objects that can be set in a {@code set} call */
    public long getMaxObjectsInSet() {
        return maxObjectsInSet;
    }

    /** @return an unmodifiable list of supported collation algorithm names */
    public List<String> getCollationAlgorithms() {
        return collationAlgorithms;
    }

    /**
     * Parses a {@link JsonNode} representing a Core capability object.
     *
     * @param data JSON object node
     * @return a {@code CoreCapability} instance
     * @throws JmapProtocolError if required properties are missing or have an unexpected type
     */
    public static CoreCapability fromJson(JsonNode data) {
        if (data == null || !data.isObject()) {
            throw new JmapProtocolError("invalidArguments", "CoreCapability JSON must be an object");
        }

        JsonNode node;

        node = data.get("maxSizeUpload");
        if (node == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required property maxSizeUpload");
        }
        long maxSizeUpload = node.longValue();

        node = data.get("maxConcurrentUpload");
        if (node == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required property maxConcurrentUpload");
        }
        long maxConcurrentUpload = node.longValue();

        node = data.get("maxSizeRequest");
        if (node == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required property maxSizeRequest");
        }
        long maxSizeRequest = node.longValue();

        node = data.get("maxConcurrentRequests");
        if (node == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required property maxConcurrentRequests");
        }
        long maxConcurrentRequests = node.longValue();

        node = data.get("maxCallsInRequest");
        if (node == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required property maxCallsInRequest");
        }
        long maxCallsInRequest = node.longValue();

        node = data.get("maxObjectsInGet");
        if (node == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required property maxObjectsInGet");
        }
        long maxObjectsInGet = node.longValue();

        node = data.get("maxObjectsInSet");
        if (node == null) {
            throw new JmapProtocolError("invalidArguments", "Missing required property maxObjectsInSet");
        }
        long maxObjectsInSet = node.longValue();

        node = data.get("collationAlgorithms");
        if (node == null || !node.isArray()) {
            throw new JmapProtocolError("invalidArguments", "Missing required property collationAlgorithms or it is not an array");
        }
        List<String> collationAlgorithms = new ArrayList<>();
        for (JsonNode el : node) {
            collationAlgorithms.add(el.textValue());
        }

        return new CoreCapability(
                maxSizeUpload,
                maxConcurrentUpload,
                maxSizeRequest,
                maxConcurrentRequests,
                maxCallsInRequest,
                maxObjectsInGet,
                maxObjectsInSet,
                collationAlgorithms);
    }

    /**
     * Serialises this capability object to a {@link JsonNode}.
     *
     * @return JSON representation of this {@code CoreCapability}
     */
    public JsonNode toJson() {
        ObjectNode obj = MAPPER.createObjectNode();
        obj.put("maxSizeUpload", maxSizeUpload);
        obj.put("maxConcurrentUpload", maxConcurrentUpload);
        obj.put("maxSizeRequest", maxSizeRequest);
        obj.put("maxConcurrentRequests", maxConcurrentRequests);
        obj.put("maxCallsInRequest", maxCallsInRequest);
        obj.put("maxObjectsInGet", maxObjectsInGet);
        obj.put("maxObjectsInSet", maxObjectsInSet);

        ArrayNode arr = obj.putArray("collationAlgorithms");
        for (String alg : collationAlgorithms) {
            arr.add(alg);
        }

        return obj;
    }
}
