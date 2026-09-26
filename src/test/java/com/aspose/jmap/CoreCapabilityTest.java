package com.aspose.jmap;

// No import needed: this test file shares the exact same package as the production code under test
// (see single_package_rule) - every production class/interface is already visible here without importing it).

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CoreCapability}.
 */
public class CoreCapabilityTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void fromJson_and_toJson_normalCase() {
        // Build a full JSON representation
        ObjectNode json = MAPPER.createObjectNode();
        json.put("maxSizeUpload", 12345L);
        json.put("maxConcurrentUpload", 10L);
        json.put("maxSizeRequest", 54321L);
        json.put("maxConcurrentRequests", 20L);
        json.put("maxCallsInRequest", 30L);
        json.put("maxObjectsInGet", 40L);
        json.put("maxObjectsInSet", 50L);
        ArrayNode algs = json.putArray("collationAlgorithms");
        algs.add("algorithm1");
        algs.add("algorithm2");

        // Deserialize
        CoreCapability cap = CoreCapability.fromJson(json);

        // Verify getters
        assertEquals(12345L, cap.getMaxSizeUpload());
        assertEquals(10L, cap.getMaxConcurrentUpload());
        assertEquals(54321L, cap.getMaxSizeRequest());
        assertEquals(20L, cap.getMaxConcurrentRequests());
        assertEquals(30L, cap.getMaxCallsInRequest());
        assertEquals(40L, cap.getMaxObjectsInGet());
        assertEquals(50L, cap.getMaxObjectsInSet());
        assertEquals(List.of("algorithm1", "algorithm2"), cap.getCollationAlgorithms());

        // Serialize back and compare structures
        assertEquals(json, cap.toJson());
    }

    @Test
    void fromJson_missingRequiredProperty_throwsProtocolError() {
        // Only provide a subset of required fields
        ObjectNode incomplete = MAPPER.createObjectNode();
        incomplete.put("maxSizeUpload", 1L);
        // missing the other required properties

        assertThrows(JmapProtocolError.class, () -> CoreCapability.fromJson(incomplete));
    }

    @Test
    void getCollationAlgorithms_isUnmodifiable() {
        ObjectNode json = MAPPER.createObjectNode();
        json.put("maxSizeUpload", 1L);
        json.put("maxConcurrentUpload", 1L);
        json.put("maxSizeRequest", 1L);
        json.put("maxConcurrentRequests", 1L);
        json.put("maxCallsInRequest", 1L);
        json.put("maxObjectsInGet", 1L);
        json.put("maxObjectsInSet", 1L);
        json.putArray("collationAlgorithms").add("alg");

        CoreCapability cap = CoreCapability.fromJson(json);
        List<String> algs = cap.getCollationAlgorithms();

        assertThrows(UnsupportedOperationException.class, () -> algs.add("newAlg"));
    }
}
