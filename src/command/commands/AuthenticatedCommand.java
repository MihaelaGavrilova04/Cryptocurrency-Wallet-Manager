package command.commands;

import api.AssetCache;
import model.User;

public sealed interface AuthenticatedCommand extends Command permits BuyCommand, SellCommand,
        DepositCommand, SummaryCommand, SummaryOverallCommand, LogoutCommand {

}