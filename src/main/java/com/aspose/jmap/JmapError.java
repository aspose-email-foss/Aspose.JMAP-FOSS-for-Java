package com.aspose.jmap;

/**
 * Base class for all unchecked JMAP‑related errors in this library.
 * <p>
 * Subclasses represent specific error domains, such as protocol errors
 * returned by a JMAP server or network errors occurring while communicating
 * with the server. This class is deliberately unchecked so that client
 * methods do not need to declare {@code throws} clauses.
 *
 * <h2>Inline documentation</h2>
 * <p><strong>PatchObject</strong> – a JSON object where each key is a JSON
 * Pointer (RFC 6901) relative to the object being patched, and each value is
 * either the new value to set at that path or {@code null} to remove the
 * path. Used as the {@code update} argument shape in every object type's
 * {@code /set} method (e.g. {@code Mailbox/set}, {@code Email/set}).
 * <p>This type has no dedicated Java representation; callers construct it
 * directly as a {@link com.fasterxml.jackson.databind.JsonNode} implementation
 * (e.g., {@link com.fasterxml.jackson.databind.node.ObjectNode}).
 */
public class JmapError extends RuntimeException {

    /**
     * Creates a new {@code JmapError} with the given detail message.
     *
     * @param message the detail message
     */
    public JmapError(String message) {
        super(message);
    }

    /**
     * Creates a new {@code JmapError} with the given detail message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause
     */
    public JmapError(String message, Throwable cause) {
        super(message, cause);
    }
}
