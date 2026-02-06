package command.commands;

import api.AssetCache;
import model.User;

public final class BuyCommand implements AuthenticatedCommand {
    private final String assetId;
    private final double money;

    private static final double EPSILON = 0.000001;

    public BuyCommand(String assetId, double money) {
        validateObjectConstruction(assetId, money);

        this.assetId = assetId.toUpperCase();
        this.money = money;
    }

    @Override
    public String execute(User user, AssetCache cache) {
        validateUser(user);
        validateCache(cache);

        Double price = cache.getAssetPrice(assetId);

        if (price == null) {
            return String.format("Information about asset %s not found.", assetId);
        }

        boolean success = user.wallet().buy(assetId, price, money);

        return success ? String.format("Successfully bought asset with id : %s!", assetId) :
                         "Failed: Not enough money.";
    }

    private static void validateObjectConstruction(String assetId, double money) {
        if (assetId == null || assetId.isBlank()) {
            throw new IllegalArgumentException("Parameter AssetId passed to construct BuyCommand object is invalid!");
        }

        if (money < EPSILON) {
            throw new IllegalArgumentException("Parameter 'money' passed to construct BuyCommand object is invalid!");
        }
    }

    private static void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Parameter 'user' used to execute BuyCommand should not be null!");
        }
    }

    private static void validateCache(AssetCache cache) {
        if (cache == null || cache.isCacheExpired()) {
            throw new IllegalArgumentException("Parameter 'cache' used to execute BuyCommand should not be invalid!");
        }
    }
}