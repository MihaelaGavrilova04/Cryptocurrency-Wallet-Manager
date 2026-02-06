package command.commands;

import api.AssetCache;
import model.Asset;
import model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class SummaryOverallCommand implements AuthenticatedCommand {

    @Override
    public String execute(User user, AssetCache cache) {
        validateUser(user);
        validateCache(cache);

        List<Asset> availableAssets = cache.getCachedValues();

        if (availableAssets == null || availableAssets.isEmpty()) {
            return "No data for available assets present.";
        }

        Map<String, Double> currentPrices = availableAssets.stream()
                .filter(asset -> asset != null && asset.id() != null && asset.price() != null)
                .collect(Collectors.toMap(
                        Asset::id,
                        Asset::price
                ));

        return user.wallet().getWalletOverallSummary(currentPrices);
    }

    private static void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Parameter 'user' passed to execute function is null!");
        }
    }

    private static void validateCache(AssetCache cache) {
        if (cache == null || cache.isCacheExpired()) {
            throw new IllegalArgumentException("Parameter 'cached' passed to execute function is invalid!");
        }
    }
}
