package com.aspose.jmap;

// No import needed: this test file shares the exact same package as the production code under test
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;

public class HttpJmapTransportTest {

    @Test
    void sendSimpleRequest_returnsResponseAndPreservesHeadersAndBody() {
        // arrange
        Map<String, List<String>> reqHeaders = Map.of(
                "Authorization", List.of("Bearer token"),
                "Custom-Header", List.of("value1", "value2")
        );
        JmapHttpRequest jmapRequest = new JmapHttpRequest(
                "POST",
                "https://example.com/jmap",
                reqHeaders,
                "{\"foo\":\"bar\"}".getBytes(StandardCharsets.UTF_8)
        );

        HttpResponse<byte[]> fakeResponse = createResponse(
                200,
                Map.of("Content-Type", List.of("application/json")),
                "{\"result\":\"ok\"}"
        );

        FakeHttpClient fakeClient = new FakeHttpClient(List.of(fakeResponse));
        HttpJmapTransport transport = new HttpJmapTransport(fakeClient);

        // act
        JmapHttpResponse result = transport.send(jmapRequest);

        // assert response content
        assertEquals(200, result.getStatusCode());
        assertArrayEquals("{\"result\":\"ok\"}".getBytes(StandardCharsets.UTF_8), result.getBody());
        assertEquals("application/json", firstHeaderValue(result.getHeaders(), "Content-Type"));

        // assert request that was sent to the HttpClient
        HttpRequest captured = fakeClient.capturedRequests.get(0);
        assertEquals("POST", captured.method());
        assertEquals(URI.create("https://example.com/jmap"), captured.uri());
        assertEquals("Bearer token", firstHeaderValue(captured.headers().map(), "Authorization"));
        assertEquals("value1", captured.headers().firstValue("Custom-Header").orElse(null));
        // body
        assertTrue(captured.bodyPublisher().isPresent());
        // bodyPublisher is not directly readable; we rely on the fact that the transport
        // would have used the exact string we supplied.
    }

    @Test
    void sendRedirect_isFollowedAndHeadersPreserved() {
        // arrange initial request
        Map<String, List<String>> reqHeaders = Map.of(
                "Authorization", List.of("Bearer token")
        );
        JmapHttpRequest jmapRequest = new JmapHttpRequest(
                "GET",
                "https://example.com/jmap",
                reqHeaders,
                null
        );

        // first response is a redirect
        HttpResponse<byte[]> redirectResponse = createResponse(
                302,
                Map.of("Location", List.of("https://example.com/jmap/redirected")),
                null
        );
        // final response after redirect
        HttpResponse<byte[]> finalResponse = createResponse(
                200,
                Map.of(),
                "{\"final\":\"yes\"}"
        );

        FakeHttpClient fakeClient = new FakeHttpClient(List.of(redirectResponse, finalResponse));
        HttpJmapTransport transport = new HttpJmapTransport(fakeClient);

        // act
        JmapHttpResponse result = transport.send(jmapRequest);

        // assert final response
        assertEquals(200, result.getStatusCode());
        assertArrayEquals("{\"final\":\"yes\"}".getBytes(StandardCharsets.UTF_8), result.getBody());

        // two requests should have been captured: original and redirect
        assertEquals(2, fakeClient.capturedRequests.size());

        HttpRequest original = fakeClient.capturedRequests.get(0);
        HttpRequest redirected = fakeClient.capturedRequests.get(1);

        // original request unchanged
        assertEquals("GET", original.method());
        assertEquals(URI.create("https://example.com/jmap"), original.uri());

        // redirected request should preserve method, headers and have new URI
        assertEquals("GET", redirected.method());
        assertEquals(URI.create("https://example.com/jmap/redirected"), redirected.uri());
        assertEquals("Bearer token", firstHeaderValue(redirected.headers().map(), "Authorization"));
    }

    @Test
    void sendNetworkFailure_throwsJmapNetworkError() {
        // arrange a client that throws IOException
        FakeHttpClient failingClient = new FakeHttpClient(IOException::new);
        HttpJmapTransport transport = new HttpJmapTransport(failingClient);
        JmapHttpRequest request = new JmapHttpRequest(
                "POST",
                "https://example.com/jmap",
                Collections.emptyMap(),
                "{}".getBytes(StandardCharsets.UTF_8)
        );

        // act & assert
        JmapNetworkError ex = assertThrows(JmapNetworkError.class, () -> transport.send(request));
        assertTrue(ex.getMessage().contains("Failed to send JMAP HTTP request"));
        assertNotNull(ex.getCause());
        assertTrue(ex.getCause() instanceof IOException);
    }

    @Test
    void sendBinaryBody_roundTripsNonUtf8BytesExactly() {
        // Regression test: a prior version used HttpResponse.BodyHandlers.ofString()/
        // HttpRequest.BodyPublishers.ofString(), forcing every request/response body through
        // a UTF-8 (de)serialization - real (non-text) attachment content, such as an image
        // or PDF, would come back corrupted. Not valid UTF-8 (starts like a JPEG magic
        // number).
        byte[] binaryPayload = new byte[] {
                (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0x00, 0x10, 0x4A, 0x46
        };

        JmapHttpRequest jmapRequest = new JmapHttpRequest(
                "POST",
                "https://example.com/jmap/upload",
                Map.of("Content-Type", List.of("image/jpeg")),
                binaryPayload
        );

        HttpResponse<byte[]> fakeResponse = new HttpResponse<>() {
            @Override public int statusCode() { return 200; }
            @Override public HttpRequest request() { return null; }
            @Override public Optional<HttpResponse<byte[]>> previousResponse() { return Optional.empty(); }
            @Override public HttpHeaders headers() { return HttpHeaders.of(Map.of(), (s1, s2) -> true); }
            @Override public byte[] body() { return binaryPayload; }
            @Override public Optional<SSLSession> sslSession() { return Optional.empty(); }
            @Override public URI uri() { return null; }
            @Override public Version version() { return Version.HTTP_1_1; }
        };

        FakeHttpClient fakeClient = new FakeHttpClient(List.of(fakeResponse));
        HttpJmapTransport transport = new HttpJmapTransport(fakeClient);

        JmapHttpResponse result = transport.send(jmapRequest);

        assertArrayEquals(binaryPayload, result.getBody());
        HttpRequest captured = fakeClient.capturedRequests.get(0);
        assertTrue(captured.bodyPublisher().isPresent());
        assertEquals(binaryPayload.length, captured.bodyPublisher().get().contentLength());
    }

    // -------------------------------------------------------------------------
    // Helper utilities
    // -------------------------------------------------------------------------

    private static HttpResponse<byte[]> createResponse(int status,
                                                       Map<String, List<String>> headers,
                                                       String body) {
        byte[] bodyBytes = body == null ? null : body.getBytes(StandardCharsets.UTF_8);
        return new HttpResponse<>() {
            @Override public int statusCode() { return status; }
            @Override public HttpRequest request() { return null; }
            @Override public Optional<HttpResponse<byte[]>> previousResponse() { return Optional.empty(); }
            @Override public HttpHeaders headers() {
                return HttpHeaders.of(headers, (s1, s2) -> true);
            }
            @Override public byte[] body() { return bodyBytes; }
            @Override public Optional<SSLSession> sslSession() { return Optional.empty(); }
            @Override public URI uri() { return null; }
            @Override public Version version() { return Version.HTTP_1_1; }
        };
    }

    private static String firstHeaderValue(Map<String, List<String>> headers, String name) {
        List<String> values = headers.get(name);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    /**
     * Minimal stub HttpClient that returns predefined responses or throws a supplied exception.
     */
    private static class FakeHttpClient extends HttpClient {
        private final Queue<HttpResponse<byte[]>> responseQueue = new ArrayDeque<>();
        private final List<HttpRequest> capturedRequests = new ArrayList<>();
        private final IOException toThrowOnSend;

        FakeHttpClient(List<HttpResponse<byte[]>> responses) {
            this.responseQueue.addAll(responses);
            this.toThrowOnSend = null;
        }

        FakeHttpClient(Supplier<IOException> exceptionSupplier) {
            this.toThrowOnSend = exceptionSupplier.get();
        }

        @Override
        public <T> HttpResponse<T> send(HttpRequest request,
                                        HttpResponse.BodyHandler<T> responseBodyHandler)
                throws IOException, InterruptedException {
            capturedRequests.add(request);
            if (toThrowOnSend != null) {
                throw toThrowOnSend;
            }
            @SuppressWarnings("unchecked")
            HttpResponse<T> resp = (HttpResponse<T>) responseQueue.poll();
            if (resp == null) {
                throw new IOException("No more fake responses");
            }
            return resp;
        }

        // The remaining abstract methods are not used by the tests; provide simple defaults.
        @Override public Optional<CookieHandler> cookieHandler() { return Optional.empty(); }
        @Override public Optional<Duration> connectTimeout() { return Optional.empty(); }
        @Override public Redirect followRedirects() { return Redirect.NEVER; }
        @Override public Optional<ProxySelector> proxy() { return Optional.empty(); }
        @Override public SSLContext sslContext() { return null; }
        @Override public SSLParameters sslParameters() { return null; }
        @Override public Optional<Authenticator> authenticator() { return Optional.empty(); }
        @Override public Version version() { return Version.HTTP_1_1; }
        @Override public Optional<Executor> executor() { return Optional.empty(); }
        @Override public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request,
                                                                          HttpResponse.BodyHandler<T> responseBodyHandler) {
            throw new UnsupportedOperationException("sendAsync not needed for tests");
        }
        @Override public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request,
                                                                          HttpResponse.BodyHandler<T> responseBodyHandler,
                                                                          HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
            throw new UnsupportedOperationException("sendAsync not needed for tests");
        }
    }
}
