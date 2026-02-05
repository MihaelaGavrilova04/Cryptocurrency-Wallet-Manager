package command.authenticated;

import api.AssetCache;
import command.Command;
import model.User;

public sealed interface AuthenticatedCommand extends Command permits BuyCommand, DepositCommand, SummaryCommand {
    String execute(User user, AssetCache cache);

    @Override
    default String execute() {
        throw new IllegalStateException("Authenticated command requires a user context.");
    }
}