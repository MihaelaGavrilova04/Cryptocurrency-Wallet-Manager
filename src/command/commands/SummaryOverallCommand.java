package command.commands;

import api.AssetCache;
import exception.UnauthenticatedException;
import model.Asset;
import server.session.ClientContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class SummaryOverallCommand implements AuthenticatedCommand {
    private final ClientContext clientContext;

    public SummaryOverallCommand(ClientContext clientContext) {
        validateObjectConstruction(clientContext);

        this.clientContext = clientContext;
    }

    private static void validateObjectConstruction(ClientContext clientContext) {
        if (clientContext == null) {
            throw new IllegalArgumentException("Parameter 'clientContext' to construct SummaryCommand object is null!");
        }

        if (!clientContext.isLoggedIn()) {
            throw new UnauthenticatedException("No logged in user to summarize their transactions");
        }
    }

    private static void validateCache(AssetCache cache) {
        if (cache == null) {
            throw new IllegalArgumentException("Parameter 'cached' passed to execute function is invalid!");
        }
    }

    @Override
    public String execute(AssetCache cache) {
        validateCache(cache);

        List<Asset> availableAssets = cache.getCachedValues();

        if (availableAssets == null || availableAssets.isEmpty()) {
            return "No data for available assets present.";
        }

        Map<String, Double> currentPrices = availableAssets
                .stream()
                .filter(asset -> asset != null && asset.id() != null && asset.price() != null)
                .collect(Collectors.toMap(Asset::id, Asset::price));

        return clientContext.getLoggedInUser().wallet().getWalletOverallSummary(currentPrices);
    }
}
