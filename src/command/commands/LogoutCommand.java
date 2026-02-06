package command.commands;

import api.AssetCache;
import model.User;

public final class LogoutCommand implements AuthenticatedCommand {
    public static final String LOGOUT_SUCCESS = "Logged out successfully!";

    @Override
    public String execute(User user, AssetCache cache) {
        return LOGOUT_SUCCESS;
    }
}
