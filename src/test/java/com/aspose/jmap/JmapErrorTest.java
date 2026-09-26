package com.aspose.jmap;
// No import needed: this test file shares the exact same package as the production code under test (see single_package_rule) - every production class/interface is already visible here without importing it.

public class JmapErrorTest {

    @org.junit.jupiter.api.Test
    public void testProtocolErrorMessageAndGetters() {
        String type = "unknownMethod";
        String description = "Method not recognized";
        JmapProtocolError error = new JmapProtocolError(type, description);
        org.junit.jupiter.api.Assertions.assertEquals(type + ": " + description, error.getMessage());
        org.junit.jupiter.api.Assertions.assertEquals(type, error.getType());
        org.junit.jupiter.api.Assertions.assertEquals(description, error.getDescription());
    }

    @org.junit.jupiter.api.Test
    public void testProtocolErrorNullDescription() {
        String type = "invalidArguments";
        JmapProtocolError error = new JmapProtocolError(type, null);
        org.junit.jupiter.api.Assertions.assertEquals(type, error.getMessage());
        org.junit.jupiter.api.Assertions.assertEquals(type, error.getType());
        org.junit.jupiter.api.Assertions.assertNull(error.getDescription());
    }

    @org.junit.jupiter.api.Test
    public void testNetworkErrorCausePreserved() {
        java.io.IOException cause = new java.io.IOException("IO failure");
        JmapNetworkError error = new JmapNetworkError("Network issue", cause);
        org.junit.jupiter.api.Assertions.assertEquals("Network issue", error.getMessage());
        org.junit.jupiter.api.Assertions.assertSame(cause, error.getCause());
    }
}
