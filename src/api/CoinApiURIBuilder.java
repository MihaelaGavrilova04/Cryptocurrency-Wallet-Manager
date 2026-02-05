package api;

import java.net.URI;
import java.net.URISyntaxException;

public class CoinApiURIBuilder {
    private static final String SCHEME = "https";
    private static final String HOST = "rest.coinapi.io";
    private static final String BASE_PATH = "/v1/assets";

    private String assetId;

    public CoinApiURIBuilder withAssetId(String assetId) {
        this.assetId = assetId;
        return this;
    }

    public URI build() {
        try {
            StringBuilder pathBuilder = new StringBuilder(BASE_PATH);

            if (assetId != null && !assetId.isBlank()) {
                pathBuilder.append("/").append(assetId);
            }

            return new URI(
                    SCHEME,
                    null,
                    HOST,
                    -1,
                    pathBuilder.toString(),
                    null,
                    null
            );

        } catch (URISyntaxException e) {
            // TO DO : HANDLE LOGIC GLOBALLY
            throw new IllegalStateException(e);
        }
    }
}