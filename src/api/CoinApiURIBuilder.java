package api;

import logger.Logger;

import java.net.URI;
import java.net.URISyntaxException;

class CoinApiURIBuilder {
    private static final String SCHEME = "https";
    private static final String HOST = "rest.coinapi.io";
    private static final String BASE_PATH = "/v1/assets";
    private static final String PATH_SEPARATOR = "/";

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
                pathBuilder.append(PATH_SEPARATOR).append(assetId);
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
            LOGGER.log(e, "SYSTEM_URI_CONFIG");
            throw new RuntimeException("Failed to construct a valid URI for CoinAPI", e);
        }
    }

    private static void validateAssetID(String assetId) {
        if (assetId == null || assetId.isBlank()) {
            throw new IllegalArgumentException("Invalid assetId passed to construct object of type CoinApiURIBuilder");
        }
    }

}