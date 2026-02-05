package command;

import api.AssetCache;
import model.User;

public final class SellCommand implements AuthenticatedCommand {
    private final String assetId;

    public SellCommand(String assetId) {
        this.assetId = assetId;
    }

    @Override
    public String execute(User user, AssetCache cache) {
        Double currentPrice = cache.getAssetPrice(assetId);

        if (currentPrice == null) {
            return String.format("Asset '%s''s price unavailable.", assetId);
        }

        return (user.wallet().sell(assetId, currentPrice)) ?
                String.format("%s successfully sold %s", user.email(), assetId) :
                String.format("%s could not sell %s", user.email(), assetId);
    }
}
