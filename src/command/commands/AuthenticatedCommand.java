package command.commands;

public sealed interface AuthenticatedCommand extends Command permits BuyCommand, SellCommand,
        DepositCommand, SummaryCommand, SummaryOverallCommand, LogoutCommand {

}