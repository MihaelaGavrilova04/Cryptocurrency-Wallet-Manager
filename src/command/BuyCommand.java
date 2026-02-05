package command;

import api.AssetCache;
import model.User;

public final class BuyCommand implements AuthenticatedCommand {
    private final String assetId;
    private final double money;

    public BuyCommand(String assetId, double money) {
        this.assetId = assetId;
        this.money = money;
    }

    @Override
    public String execute(User user, AssetCache cache) {
        Double price = cache.getAssetPrice(assetId);
        if (price == null) {
            return "Asset not found.";
        }

        boolean success = user.wallet().buy(assetId, price, money);

        return success ? "Success!" : "Failed: Not enough money.";
    }
}