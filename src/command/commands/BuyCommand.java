package command.commands;

import api.AssetCache;
import exception.InvalidCommandException;
import exception.UnauthenticatedException;
import model.User;
import repository.UserRepository;
import server.session.ClientContext;

public final class BuyCommand implements AuthenticatedCommand {
    private static final double EPSILON = 0.000001;

    private final String assetId;
    private final double money;
    private final ClientContext clientContext;
    private final UserRepository userRepository;

    public BuyCommand(String assetId, double money, ClientContext clientContext, UserRepository userRepository) {
        validateObjectConstruction(assetId, money, clientContext, userRepository);
        this.assetId = assetId.toUpperCase();
        this.money = money;
        this.clientContext = clientContext;
        this.userRepository = userRepository;
    }

    @Override
    public String execute(AssetCache cache) {
        validateCache(cache);

        Double price = cache.getAssetPrice(assetId);

        if (price == null) {
            return String.format("Information about asset %s not found.", assetId);
        }

        User loggedInUser = clientContext.getLoggedInUser();

        boolean success = loggedInUser.wallet().buy(assetId, price, money);

        if (success) {
            userRepository.updateUser(loggedInUser);
            return String.format("Successfully bought asset with id : %s!", assetId);
        }

        return "Failed: Not enough money.";
    }

    private static void validateObjectConstruction(String assetId, double money,
                                                   ClientContext clientContext, UserRepository userRepository) {
        if (assetId == null || assetId.isBlank()) {
            throw new InvalidCommandException("The id of the asset passed is invalid!");
        }

        if (money < EPSILON) {
            throw new InvalidCommandException("The amount of money passed invalid!");
        }

        if (clientContext == null) {
            throw new IllegalArgumentException("Parameter 'ClientContext' passed to construct BuyCommand is null!");
        }

        if (!clientContext.isLoggedIn()) {
            throw new UnauthenticatedException("No logged in user to execute BuyCommand!");
        }

        if (userRepository == null) {
            throw new IllegalArgumentException("Parameter 'userRepository' passed to construct BuyCommand is null!");
        }
    }

    private static void validateCache(AssetCache cache) {
        if (cache == null) {
            throw new IllegalArgumentException("Parameter 'cache' used to execute BuyCommand should not be invalid!");
        }
    }
}