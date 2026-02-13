package command.commands;

import api.AssetCache;

public sealed interface PublicCommand extends Command permits RegisterCommand, LoginCommand, HelpCommand, ListCommand {

    String execute();

    @Override
    default String execute(AssetCache cache) {
        return execute();
    }
}