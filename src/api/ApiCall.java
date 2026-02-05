package api;

import com.google.gson.Gson;
import util.GsonProvider;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiCall {

    private final HttpClient httpClient;
    private final String apiKey;

    private final Gson GSON;
    private final CoinApiURIBuilder coinApiURIBuilder;

    private static final String API_KEY_HEADER = "X-CoinAPI-Key";

    public ApiCall(String apiKey, HttpClient httpClient) {

        validateObjectConstruction(apiKey, httpClient);

        this.apiKey = apiKey;
        this.httpClient = httpClient;
        GSON = GsonProvider.getGson();
        this.coinApiURIBuilder = new CoinApiURIBuilder();
    }

    public HttpResponse<String> fetchAll() {
        URI uri = coinApiURIBuilder.build();
        System.out.println(uri.toString());
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
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            return response;

            // TO DO: HANDLE BETTER & CHECK STATUS CODE
        } catch (IOException e) {
            throw new UncheckedIOException("Network error", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Request interrupted", e);
        }
    }

    private HttpRequest buildRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .header(API_KEY_HEADER, apiKey)
                .header("Accept", "application/json")
                .GET()
                .build();
    }

    private void validateObjectConstruction(String apiKey, HttpClient httpClient) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key cannot be null or blank");
        }
        if (httpClient == null) {
            throw new IllegalArgumentException("HttpClient cannot be null");
        }
    }

    private void validateAssetID(String assetID) {
        if (assetID == null || assetID.isBlank()) {
            throw new IllegalArgumentException("Asset ID cannot be null or blank");
        }
    }

}
