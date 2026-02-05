package command;

import api.AssetCache;
import model.Asset;
import model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class SummaryOverallCommand implements AuthenticatedCommand {

    @Override
    public String execute(User user, AssetCache cache) {
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
}
