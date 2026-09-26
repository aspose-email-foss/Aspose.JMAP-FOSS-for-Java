package com.aspose.jmap;

/**
 * Options for configuring a {@link JmapClientCore}.
 * <p>
 * Use the {@link Builder} to create an immutable instance.
 */
public final class JmapClientOptions {
    private final String sessionUrl;
    private final String username;
    private final String password;
    private final String bearerToken;
    private final JmapTransport transport; // may be {@code null}

    private JmapClientOptions(Builder builder) {
        this.sessionUrl = builder.sessionUrl;
        this.username = builder.username;
        this.password = builder.password;
        this.bearerToken = builder.bearerToken;
        this.transport = builder.transport;
    }

    /** @return the well‑known session URL */
    public String getSessionUrl() {
        return sessionUrl;
    }

    /** @return the username for HTTP Basic authentication */
    public String getUsername() {
        return username;
    }

    /** @return the password for HTTP Basic authentication */
    public String getPassword() {
        return password;
    }

    /**
     * @return the OAuth 2.0 bearer token ({@code Authorization: Bearer &lt;token&gt;}, per
     *     <a href="https://www.rfc-editor.org/rfc/rfc6750">RFC 6750</a>), or {@code null} if
     *     HTTP Basic authentication should be used instead
     */
    public String getBearerToken() {
        return bearerToken;
    }

    /** @return the injected {@link JmapTransport} or {@code null} to use the default */
    public JmapTransport getTransport() {
        return transport;
    }

    /** Builder for {@link JmapClientOptions}. */
    public static class Builder {
        private String sessionUrl;
        private String username;
        private String password;
        private String bearerToken;
        private JmapTransport transport;

        /** Sets the well‑known session URL. */
        public Builder sessionUrl(String sessionUrl) {
            this.sessionUrl = sessionUrl;
            return this;
        }

        /** Sets the username for HTTP Basic authentication. */
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        /** Sets the password for HTTP Basic authentication. */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * Sets the OAuth 2.0 bearer token ({@code Authorization: Bearer &lt;token&gt;}, per
         * <a href="https://www.rfc-editor.org/rfc/rfc6750">RFC 6750</a>) to use instead of HTTP
         * Basic authentication. When set to a non-empty value, it takes precedence over
         * {@link #username(String)} / {@link #password(String)}.
         */
        public Builder bearerToken(String bearerToken) {
            this.bearerToken = bearerToken;
            return this;
        }

        /** Optionally injects a custom {@link JmapTransport}. */
        public Builder transport(JmapTransport transport) {
            this.transport = transport;
            return this;
        }

        /** Builds the immutable {@link JmapClientOptions} instance. */
        public JmapClientOptions build() {
            return new JmapClientOptions(this);
        }
    }
}
