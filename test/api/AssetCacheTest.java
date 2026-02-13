package api;

import model.Asset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class AssetCacheTest {

        private AssetCache assetCache;
        private ApiCall apiCallMock;

        @BeforeEach
        void setUp() {
            apiCallMock = mock(ApiCall.class);
            assetCache = new AssetCache(apiCallMock);
        }

        @AfterEach
        void tearDown() {
            assetCache.close();
        }

        @Test
        void testCacheIsPopulatedWithCorrectCount() {
            assertEquals(8, assetCache.getAssetCount(), "Cache should contain only crypto assets");
        }

        @Test
        void testContainsAssetCaseInsensitive() {
            assertTrue(assetCache.containsAsset("btc"));
            assertTrue(assetCache.containsAsset("BTC"));
            assertTrue(assetCache.containsAsset("Btc"));
        }

        @Test
        void testGetAssetByIdReturnsCorrectData() {
            Asset btc = assetCache.getAssetById("BTC");

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

        @Test
        void testIsCacheExpiredInitialState() {
            assertFalse(assetCache.isCacheExpired());
        }
}

