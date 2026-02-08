package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import exception.AssetNotFoundException;
//import exception.CoinAPIException;
import logger.Logger;
import model.Asset;
import util.GsonProvider;

import java.lang.reflect.Type;
//import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AssetCache implements AutoCloseable {

    private static final int CACHE_DURATION = 30;
    private static final int MAX_NUMBER_OF_ASSETS_IN_CACHE = 100;
    private static final Type ASSET_LIST_TYPE = new TypeToken<List<Asset>>() { }.getType();

    private final Gson gson;
    private final ScheduledExecutorService scheduler;

    private final Map<String, Asset> assetCache;

    private final ApiCall apiCall;
    private LocalDateTime lastUpdated;

    private static final Logger LOGGER = Logger.getInstance();

    public AssetCache(ApiCall apiCall) {
        validateApiCall(apiCall);

        this.apiCall = apiCall;
        gson = GsonProvider.getGson();
        this.assetCache = new ConcurrentHashMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        updateCache();
        scheduler.scheduleAtFixedRate(this::updateCache, CACHE_DURATION, CACHE_DURATION, TimeUnit.MINUTES);
    }

    public synchronized boolean isCacheExpired() {
        return lastUpdated == null ||
                lastUpdated.plusMinutes(CACHE_DURATION).isBefore(LocalDateTime.now());
    }

    public List<Asset> getCachedValues() {
        if (assetCache == null) {
            return Collections.emptyList();
        }

        if (isCacheExpired()) {
            updateCache();
        }

        return List.copyOf(assetCache.values());
    }

    @Override
    public void close() {
        shutdown();
    }

    public Asset getAssetById(String assetId) {
        validateAssetID(assetId);

        if (isCacheExpired()) {
            updateCache();
        }

        return assetCache.get(assetId.toUpperCase());
    }

    public Double getAssetPrice(String assetId) {
        validateAssetID(assetId);

        Asset asset = getAssetById(assetId);
        return asset != null ? asset.price() : null;
    }

    public boolean containsAsset(String assetId) {
        validateAssetID(assetId);

        if (isCacheExpired()) {
            updateCache();
        }

        return assetCache.containsKey(assetId.toUpperCase());
    }

    public int getAssetCount() {
        return assetCache.size();
    }

    private synchronized void shutdown() {
        this.scheduler.shutdown();
    }

//    private synchronized void updateCache() {
//        if (!isCacheExpired()) {
//            return;
//        }
//        try {
//            HttpResponse<String> response = apiCall.fetchAll();
//
//            List<Asset> assets = parseResponse(response.body());
//            updateCacheMap(assets);
//            lastUpdated = LocalDateTime.now();
//
//        } catch (CoinAPIException | AssetNotFoundException e) {
//            LOGGER.log(e, "SYSTEM_CACHE");
//        } catch (Exception e) {
//            LOGGER.log(e, "SYSTEM_FATAL");
//        }
//    }

    private synchronized void updateCache() {

        try {
            String jsonResponse = """
            [
              { "asset_id": "BTC", "name": "Bitcoin", "type_is_crypto": 1, "price_usd": 43251.23 },
              { "asset_id": "ETH", "name": "Ethereum", "type_is_crypto": 1, "price_usd": 2310.45 },
              { "asset_id": "BNB", "name": "BNB", "type_is_crypto": 1, "price_usd": 305.12 },
              { "asset_id": "SOL", "name": "Solana", "type_is_crypto": 1, "price_usd": 98.77 },
              { "asset_id": "ADA", "name": "Cardano", "type_is_crypto": 1, "price_usd": 0.62 },
              { "asset_id": "DOGE", "name": "Dogecoin", "type_is_crypto": 1, "price_usd": 0.085 },
              { "asset_id": "XRP", "name": "XRP", "type_is_crypto": 1, "price_usd": 0.57 },
              { "asset_id": "USDT", "name": "Tether", "type_is_crypto": 1, "price_usd": 1.0 },
              { "asset_id": "USD", "name": "US Dollar", "type_is_crypto": 0, "price_usd": 1.0 }
            ]
            """;

            List<Asset> assets = parseResponse(jsonResponse);

            Map<String, Asset> newEntries = new java.util.HashMap<>();
            for (Asset asset : assets) {
                if (asset != null && asset.id() != null) {
                    newEntries.put(asset.id().toUpperCase(), asset);
                }
            }

            assetCache.clear();
            assetCache.putAll(newEntries);

            lastUpdated = LocalDateTime.now();

        } catch (Exception e) {
            LOGGER.log(e, "SYSTEM_CACHE_ERROR");
        }
    }

    private void updateCacheMap(List<Asset> assets) {
        assetCache.clear();
        for (Asset asset : assets) {
            if (asset != null && asset.id() != null) {
                assetCache.put(asset.id().toUpperCase(), asset);
            }
        }
    }

    private List<Asset> parseResponse(String jsonResponse) {
        try {
            List<Asset> allAssets = gson.fromJson(jsonResponse, ASSET_LIST_TYPE);

            if (allAssets == null || allAssets.isEmpty()) {
                return Collections.emptyList();
            }

            return allAssets
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(Asset::isCryptoAsset)
                    .filter(asset -> asset.price() != null && asset.price() > 0)
                    .filter(a -> a.id() != null && !a.id().isBlank())
                    .filter(a -> a.name() != null && !a.name().isBlank())
                    .limit(MAX_NUMBER_OF_ASSETS_IN_CACHE)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            LOGGER.log(e, "SYSTEM");
            throw new RuntimeException("Can not parse json object to Asset Java object.");
        }
    }

    private static void validateApiCall(ApiCall apiCall) {
        if (apiCall == null) {
            throw new IllegalArgumentException("Parameter 'ApiCall' passed to construct AssetCache object is null");
        }
    }

    private static void validateAssetID(String assetId) {
        if (assetId == null || assetId.isBlank()) {
            throw new IllegalArgumentException("AssetId is null or blank!");
        }
    }
}
