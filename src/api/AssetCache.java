package api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AssetCache {
    Map<String, String> cache; // Map<String, Asset>

    public AssetCache() {
        this.cache = new HashMap<>();
    }

    public void updateCache(Map<String, String> other) {
        cache = other;
    }

    public void updateAsset(String assetID, String asset) {
        cache.put(assetID, asset);
    }
}
