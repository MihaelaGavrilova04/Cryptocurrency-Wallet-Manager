package command;

public sealed interface Command permits PublicCommand, AuthenticatedCommand {
    String execute();
}
