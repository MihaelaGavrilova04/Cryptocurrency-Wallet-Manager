package command.commands;

import api.AssetCache;
import server.session.ClientContext;

public final class LogoutCommand implements AuthenticatedCommand {
    private static final String LOGOUT_SUCCESS = "Logged out successfully!";
    private final ClientContext clientContext;

    public LogoutCommand(ClientContext context) {
        this.clientContext = context;
    }

    @Override
    public String execute(AssetCache cache) {
        clientContext.logout();
        return LOGOUT_SUCCESS;
    }
}
