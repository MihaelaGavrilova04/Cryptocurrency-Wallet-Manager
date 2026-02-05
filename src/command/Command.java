package command;

import command.authenticated.AuthenticatedCommand;
import command.unauthenticated.PublicCommand;

public sealed interface Command permits PublicCommand, AuthenticatedCommand {
    String execute();
}
