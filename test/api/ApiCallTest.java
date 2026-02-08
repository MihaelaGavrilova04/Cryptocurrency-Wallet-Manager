package api;

import exception.CoinAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiCallTest {
    private ApiCall apiCall;
    private HttpClient httpClientMock;
    private HttpResponse<String> httpResponseMock;

    @BeforeEach
    void setUp() {
        httpClientMock = mock(HttpClient.class);
        httpResponseMock = mock(HttpResponse.class);
        apiCall = new ApiCall("dummy-key", httpClientMock);
    }

    @Test
    void testApiCallConstruction() {
        assertDoesNotThrow(()->new ApiCall("dummy-key", httpClientMock));
    }

    @Test
    void testApiCallConstructionNullKey() {
        assertThrows(IllegalArgumentException.class, ()->new ApiCall(null, HttpClient.newBuilder().build()), "When ApiCall object is constructed with invalid params, IllegalArgumentException is expected!");
    }

    @Test
    void testApiCallConstructionBlankKey() {
        assertThrows(IllegalArgumentException.class, ()->new ApiCall("", HttpClient.newBuilder().build()), "When ApiCall object is constructed with invalid params, IllegalArgumentException is expected!");
    }

    @Test
    void testApiCallConstructionNullHttpClient() {
        assertThrows(IllegalArgumentException.class, ()->new ApiCall("dummy-key", null), "When ApiCall object is constructed with invalid params, IllegalArgumentException is expected!");
    }

    @Test
    void testFetchAllSuccessful() throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn("{\"status\":\"success\"}");

        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        HttpResponse<String> response = apiCall.fetchAll();

        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals("{\"status\":\"success\"}", response.body());
    }

    @Test
    void testFetchByIDSameLogic() throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn("{\"asset_id\":\"BTC\"}");

        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        HttpResponse<String> response = apiCall.fetchByID("BTC");

        assertTrue(response.body().contains("BTC"));
    }

    @Test
    void testHandleResponseUnauthorized() throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(401);
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        assertThrows(CoinAPIException.class, () -> apiCall.fetchAll(),
                "Expected CoinAPIException for 401 Unauthorized code");
    }

    @Test
    void testFetchByIDInvalidParams() {
        assertThrows(IllegalArgumentException.class, () -> apiCall.fetchByID(null),
                "Should throw for null asset ID");
    }

    @Test
    void testFetchResponseThrowsUncheckedIOExceptionOnNetworkError() throws IOException, InterruptedException {
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Connection reset"));

        assertThrows(UncheckedIOException.class, () -> apiCall.fetchAll(),
                "UncheckedIOException expected to wrap IOException");
    }

    @Test
    void testFetchAllThrowsCoinAPIExceptionOnRateLimit() throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(429);
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        CoinAPIException exception = assertThrows(CoinAPIException.class, () -> apiCall.fetchAll());
        assertEquals("API limit exceeded.", exception.getMessage());
    }

    @Test
    void testFetchByIDWithInvalidParametersCode400() throws IOException, InterruptedException {
        when(httpResponseMock.statusCode()).thenReturn(400);
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        assertThrows(exception.InvalidCommandException.class, () -> apiCall.fetchByID("dummy"),
                "Should throw InvalidCommandException");
    }
}
