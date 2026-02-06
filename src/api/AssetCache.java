package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exception.AssetNotFoundException;
import exception.CoinAPIException;
import logger.Logger;
import model.Asset;
import util.GsonProvider;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
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
        return lastUpdated == null || lastUpdated.plusMinutes(CACHE_DURATION).isBefore(LocalDateTime.now());
    }

    public List<Asset> getCachedValues() {
        if (assetCache == null) {
            return Collections.emptyList();
        }
        return List.copyOf(assetCache.values());
    }

    @Override
    public void close() {
        shutdown();
    }

    public Asset getAssetById(String assetId) {
        return assetCache.get(assetId.toUpperCase());
    }

    public Double getAssetPrice(String assetId) {
        Asset asset = getAssetById(assetId);
        return asset != null ? asset.price() : null;
    }

    public boolean containsAsset(String assetId) {
        return assetCache.containsKey(assetId.toUpperCase());
    }

    public int getAssetCount() {
        return assetCache.size();
    }

    private synchronized void shutdown() {
        this.scheduler.shutdown();
    }

    private synchronized void updateCache() {
        try {
            HttpResponse<String> response = apiCall.fetchAll();

            List<Asset> assets = parseResponse(response.body());
            updateCacheMap(assets);
            lastUpdated = LocalDateTime.now();

        } catch (CoinAPIException | AssetNotFoundException e) {
            LOGGER.log(e, "SYSTEM_CACHE");
        } catch (Exception e) {
            LOGGER.log(e, "SYSTEM_FATAL");
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
}
