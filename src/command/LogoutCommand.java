package command;

import api.AssetCache;
import model.User;

public final class LogoutCommand implements AuthenticatedCommand {

    @Override
    public String execute(User user, AssetCache cache) {
        return "";
    }
}
