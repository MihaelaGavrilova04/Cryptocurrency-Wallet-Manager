package command.commands;

public sealed interface PublicCommand extends Command permits RegisterCommand, LoginCommand, HelpCommand, ListCommand {
}