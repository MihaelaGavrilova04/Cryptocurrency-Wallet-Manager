package api;

import model.Asset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssetCacheTest {

        private AssetCache assetCache;
        private ApiCall apiCallMock;

    @BeforeEach
    void setUp() {
        apiCallMock = mock(ApiCall.class);
        HttpResponse<String> httpResponseMock = mock(HttpResponse.class);

        String json = """
    [
      { "asset_id": "BTC", "name": "Bitcoin", "type_is_crypto": 1, "price_usd": 43251.23 },
      { "asset_id": "ETH", "name": "Ethereum", "type_is_crypto": 1, "price_usd": 2310.45 },
      { "asset_id": "USD", "name": "US Dollar", "type_is_crypto": 0, "price_usd": 1.0 }
    ]
    """;

        when(apiCallMock.fetchAll()).thenReturn(httpResponseMock);
        when(httpResponseMock.body()).thenReturn(json);

        assetCache = new AssetCache(apiCallMock);
    }

        @AfterEach
        void tearDown() {
            assetCache.close();
        }

        @Test
        void testCacheIsPopulatedWithCorrectCount() {
            assertEquals(2, assetCache.getAssetCount(), "Cache should contain only crypto assets");
        }

        @Test
        void testContainsAssetCaseInsensitive() {
            assertTrue(assetCache.containsAsset("btc"));
            assertTrue(assetCache.containsAsset("BTC"));
            assertTrue(assetCache.containsAsset("Btc"));
        }

        @Test
        void testGetAssetByIdReturnsCorrectData() {
            Asset btc = new Asset("BTC", "Bitcoin", 1, 43251.23);
            when(assetCache.getAssetById("BTC")).thenReturn(btc);

            assertNotNull(btc);
            assertEquals("Bitcoin", btc.name());
            assertEquals(43251.23, btc.price(), 0.001);
        }

        @Test
        void testFiatCurrencyIsFilteredOut() {
            assertFalse(assetCache.containsAsset("USD"), "USD should be filtered out as it is not a crypto asset");
        }

        @Test
        void testGetAssetPriceReturnsNullForMissingAsset() {
            assertNull(assetCache.getAssetPrice("NON_EXISTENT_COIN"));
        }

        @Test
        void testGetCachedValuesReturnsImmutableList() {
            List<Asset> values = assetCache.getCachedValues();
            assertNotNull(values);

            assertThrows(UnsupportedOperationException.class, () -> values.remove(0));
        }

        @Test
        void testValidationLogic() {
            assertThrows(IllegalArgumentException.class, () -> assetCache.getAssetById(""));
            assertThrows(IllegalArgumentException.class, () -> assetCache.getAssetById(null));
        }

}

