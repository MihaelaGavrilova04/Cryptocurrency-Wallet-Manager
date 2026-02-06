package api;

import logger.Logger;

import java.net.URI;
import java.net.URISyntaxException;

public class CoinApiURIBuilder {
    private static final String SCHEME = "https";
    private static final String HOST = "rest.coinapi.io";
    private static final String BASE_PATH = "/v1/assets";

    private String assetId;

    private static final Logger LOGGER = Logger.getInstance();

    public CoinApiURIBuilder withAssetId(String assetId) {
        validateAssetID(assetId);

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
            LOGGER.log(e, "SYSTEM_CONFIG");
            throw new IllegalStateException("Failed to construct a valid URI for CoinAPI", e);
        }
    }

    private static void validateAssetID(String assetId) {
        if (assetId == null || assetId.isBlank()) {
            throw new IllegalArgumentException("Invalid assetId passet to construct object of type CoinApiURIBuilder");
        }
    }

}