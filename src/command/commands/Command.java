package command.commands;

public sealed interface Command permits PublicCommand, AuthenticatedCommand {
    String execute();
}
