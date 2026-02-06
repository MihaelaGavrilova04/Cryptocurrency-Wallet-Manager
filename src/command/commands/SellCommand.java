package command.commands;

import api.AssetCache;
import model.User;

public final class SellCommand implements AuthenticatedCommand {
    private final String assetId;

    public SellCommand(String assetId) {
        validateObjectConstruction(assetId);
        this.assetId = assetId.toUpperCase();
    }

    @Override
    public String execute(User user, AssetCache cache) {
        validateUser(user);
        validateCache(cache);

        Double currentPrice = cache.getAssetPrice(assetId);

        if (currentPrice == null) {
            return String.format("Asset '%s''s price unavailable.", assetId);
        }

        return (user.wallet().sell(assetId, currentPrice)) ?
                String.format("%s successfully sold %s", user.email(), assetId) :
                String.format("%s could not sell %s", user.email(), assetId);
    }

    private static void validateObjectConstruction(String assetId) {
        if (assetId == null || assetId.isBlank()) {
            throw new IllegalArgumentException("Parameter 'assetId' passed to construct SellCommand is invalid!");
        }
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
