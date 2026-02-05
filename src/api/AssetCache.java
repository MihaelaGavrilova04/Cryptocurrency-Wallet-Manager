package api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Asset;
import util.GsonProvider;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AssetCache implements AutoCloseable {
    private static final int CACHE_DURATION = 30;
    private static final int MAX_NUMBER_OF_ASSETS_IN_CACHE = 100;
    private static final Type ASSET_LIST_TYPE = new TypeToken<List<Asset>>() {
    }.getType();
    private final Gson gson;
    private final ScheduledExecutorService scheduler;
    private List<Asset> cache;
    private final ApiCall apiCall;
    private LocalDateTime lastUpdated;

    public AssetCache(ApiCall apiCall) {
        this.apiCall = apiCall;
        gson = GsonProvider.getGson();
        this.cache = Collections.emptyList();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.schedule(this::updateCache, 0, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::updateCache, CACHE_DURATION, CACHE_DURATION, TimeUnit.MINUTES);
        System.out.println("Schedule scheduled an api call !");
    }

    public synchronized boolean isCacheExpired() {
        return lastUpdated == null || lastUpdated.plusMinutes(CACHE_DURATION).isBefore(LocalDateTime.now());
    }

    public List<Asset> getCachedValues() {
        return cache != null ? Collections.unmodifiableList(cache) : Collections.emptyList();
    }

    @Override
    public void close() throws Exception {
        shutdown();
    }

    private synchronized void shutdown() {
        this.scheduler.shutdown();
    }

    private synchronized void updateCache() {

        HttpResponse<String> response = apiCall.fetchAll();
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            cache = parseResponse(response.body());
            lastUpdated = LocalDateTime.now();
        }
    }

    private List<Asset> parseResponse(String jsonResponse) {
        try {
            List<Asset> allAssets = gson.fromJson(jsonResponse, ASSET_LIST_TYPE);

            if (allAssets == null || allAssets.isEmpty()) {
                System.out.println("Hello");
                return List.of();
            }

            return allAssets.stream()
                    .filter(asset -> asset != null)
                    .filter(Asset::isCryptoAsset)
                    .filter(asset -> asset.price() != null && asset.price() > 0)
                    .filter(a -> a.id() != null && !a.id().isBlank())
                    .filter(a -> a.name() != null && !a.name().isBlank())
                    .limit(MAX_NUMBER_OF_ASSETS_IN_CACHE)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // to do: handle logging & error handling
            throw new RuntimeException("fix");
        }
    }
}
