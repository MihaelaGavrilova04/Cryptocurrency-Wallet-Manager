package command.commands;

import api.AssetCache;
import exception.UnauthenticatedException;
import server.session.ClientContext;

public final class SummaryCommand implements AuthenticatedCommand {
    private final ClientContext clientContext;

    public SummaryCommand(ClientContext clientContext) {
        validateObjectConstruction(clientContext);

        this.clientContext = clientContext;
    }

    private static void validateObjectConstruction(ClientContext clientContext) {
        if (clientContext == null) {
            throw new IllegalArgumentException(
                    "Parameter 'clientContext' passed to construct SummaryCommand object is null!");
        }

        if (!clientContext.isLoggedIn()) {
            throw new UnauthenticatedException("No logged in user to summarize the transactions");
        }
    }

    @Override
    public String execute(AssetCache cache) {
        return clientContext.getLoggedInUser().wallet().getWalletSummary();
    }
}
