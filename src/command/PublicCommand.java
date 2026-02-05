package command.unauthenticated;

import command.Command;

public sealed interface PublicCommand extends Command permits RegisterCommand, LoginCommand, HelpCommand {
}