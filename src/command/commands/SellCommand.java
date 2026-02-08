package command.commands;

import api.AssetCache;
import exception.InvalidCommandException;
import model.User;
import repository.UserRepository;
import server.session.ClientContext;

public final class SellCommand implements AuthenticatedCommand {
    private final String assetId;
    private final ClientContext clientContext;
    private final UserRepository userRepository;

    public SellCommand(String assetId, ClientContext clientContext, UserRepository userRepository) {
        validateObjectConstruction(assetId, clientContext, userRepository);

        this.assetId = assetId.toUpperCase();
        this.clientContext = clientContext;
        this.userRepository = userRepository;
    }

    @Override
    public String execute(AssetCache cache) {
        validateCache(cache);

        Double currentPrice = cache.getAssetPrice(assetId);

        if (currentPrice == null) {
            return String.format("Asset '%s''s price unavailable.", assetId);
        }

        User loggedInUser = clientContext.getLoggedInUser();

        if (loggedInUser.wallet().sell(assetId, currentPrice)) {
            userRepository.updateUser(loggedInUser);
            return String.format("You [ %s ] successfully sold %s", loggedInUser.email(), assetId);
        }

        return String.format("You [ %s ] could not sell %s! Not present in wallet!", loggedInUser.email(), assetId);
    }

    private static void validateObjectConstruction(String assetId, ClientContext clientContext, UserRepository userRepository) {
        if (assetId == null || assetId.isBlank()) {
            throw new InvalidCommandException("The asset ID you want to operate on is invalid!");
        }

        if (clientContext == null || !clientContext.isLoggedIn()) {
            throw new IllegalArgumentException(
                    "Parameter 'clientContext' passed to construct SellCommand is invalid (null | no logged in user)!");
        }

        if (userRepository == null) {
            throw new IllegalArgumentException("Parameter 'userRepository' passed to construct SellCommand is null");
        }
    }

    private static void validateCache(AssetCache cache) {
        if (cache == null) {
            throw new IllegalArgumentException("Parameter 'cached' passed to execute function is invalid!");
        }
    }
}
