package command;

import api.AssetCache;
import model.User;

public final class SummaryCommand implements AuthenticatedCommand {

    @Override
    public String execute(User user, AssetCache cache) {
        return user.wallet().getWalletSummary();
    }
}
