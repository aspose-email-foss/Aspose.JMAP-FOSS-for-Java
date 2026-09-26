package com.aspose.jmap;

/**
 * Represents a network-level error that occurs while communicating with a JMAP server.
 * This exception is thrown by {@link JmapTransport} implementations when an underlying
 * {@link java.io.IOException} or {@link java.lang.InterruptedException} is encountered.
 *
 * <p>The exception is unchecked; client methods do not declare it in a {@code throws}
 * clause.</p>
 */
public class JmapNetworkError extends JmapError {
    /**
     * Constructs a new {@code JmapNetworkError} with the specified detail message.
     *
     * @param message the detail message
     */
    public JmapNetworkError(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code JmapNetworkError} with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the error
     */
    public JmapNetworkError(String message, Throwable cause) {
        super(message, cause);
    }
}
