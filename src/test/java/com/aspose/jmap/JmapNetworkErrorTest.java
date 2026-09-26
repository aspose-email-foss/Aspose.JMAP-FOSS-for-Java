package com.aspose.jmap;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JmapNetworkErrorTest {

    @Test
    void messageConstructor_setsMessageOnly() {
        String msg = "Network failure";
        JmapNetworkError ex = new JmapNetworkError(msg);
        assertEquals(msg, ex.getMessage(), "Message should match the provided value");
        assertNull(ex.getCause(), "Cause should be null when only message is provided");
        assertTrue(ex instanceof JmapError, "JmapNetworkError must extend JmapError");
    }

    @Test
    void messageAndCauseConstructor_setsBoth() {
        String msg = "Timeout while contacting server";
        Throwable cause = new IllegalStateException("Interrupted");
        JmapNetworkError ex = new JmapNetworkError(msg, cause);
        assertEquals(msg, ex.getMessage(), "Message should match the provided value");
        assertEquals(cause, ex.getCause(), "Cause should match the provided throwable");
    }
}
