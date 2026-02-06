package api;

import com.google.gson.Gson;
import exception.AssetNotFoundException;
import exception.CoinAPIException;
import logger.Logger;
import util.GsonProvider;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiCall {

    private static final String API_KEY_HEADER = "X-CoinAPI-Key";
    private static final int HTTP_OK = 200;
    private static final int HTTP_REDIRECTION = 300;
    private static final int HTTP_BAD_REQUEST = 400;
    private static final int HTTP_UNAUTHORIZED = 401;
    private static final int HTTP_FORBIDDEN = 403;
    private static final int HTTP_TOO_MANY_REQUESTS = 429;
    private static final int HTTP_NO_DATA = 550;
    private final HttpClient httpClient;
    private final String apiKey;
    private final CoinApiURIBuilder coinApiURIBuilder;

    private static final Logger LOGGER = Logger.getInstance();

    public ApiCall(String apiKey, HttpClient httpClient) {

        validateObjectConstruction(apiKey, httpClient);

        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.coinApiURIBuilder = new CoinApiURIBuilder();
    }

    public HttpResponse<String> fetchAll() {
        URI uri = coinApiURIBuilder.build();
        return fetchResponse(uri);
    }

    public HttpResponse<String> fetchByID(String assetID) {
        validateAssetID(assetID);

        URI uri = coinApiURIBuilder.withAssetId(assetID).build();

        return fetchResponse(uri);
    }

    private HttpResponse<String> fetchResponse(URI uri) {
        try {
            HttpRequest request = buildRequest(uri);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            handleResponseCode(response.statusCode());
            return response;

        } catch (IOException e) {
            LOGGER.log(e, "SYSTEM");
            throw new UncheckedIOException("Network error", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(e, "SYSTEM");
            throw new RuntimeException("Request interrupted", e);
        }
    }

    private static void handleResponseCode(int code) {
        if (code >= HTTP_OK && code < HTTP_REDIRECTION) {
            return;
        }

        if (code == HTTP_NO_DATA) {
            throw new AssetNotFoundException("The requested asset was not found.");
        }

        String errorMessage = switch (code) {
            case HTTP_BAD_REQUEST -> "Missing or invalid parameters.";
            case HTTP_UNAUTHORIZED -> "Unauthorized: API Key is invalid.";
            case HTTP_FORBIDDEN -> "Forbidden: Your API key lacks privileges";
            case HTTP_TOO_MANY_REQUESTS -> "API limit exceeded.";
            default -> "Unexpected error from CoinAPI with status code: " + code;
        };

        throw new CoinAPIException(errorMessage);
    }

    private HttpRequest buildRequest(URI uri) {
        return HttpRequest.newBuilder().uri(uri).header(API_KEY_HEADER, apiKey).header("Accept", "application/json").GET().build();
    }

    private static void validateObjectConstruction(String apiKey, HttpClient httpClient) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key can not be null or blank");
        }
        if (httpClient == null) {
            throw new IllegalArgumentException("HttpClient can not be null");
        }
    }

    private static void validateAssetID(String assetID) {
        if (assetID == null || assetID.isBlank()) {
            throw new IllegalArgumentException("Asset ID can not be null or blank");
        }
    }

}
