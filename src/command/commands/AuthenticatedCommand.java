package command.commands;

import api.AssetCache;
import model.User;

public sealed interface AuthenticatedCommand extends Command permits BuyCommand, SellCommand,
        DepositCommand, SummaryCommand, SummaryOverallCommand, LogoutCommand {
    String execute(User user, AssetCache cache);

    @Override
    default String execute() {
        throw new IllegalStateException("Authenticated command requires a user context.");
    }
}