package command.commands;

import api.AssetCache;

public sealed interface Command permits PublicCommand, AuthenticatedCommand {
    String execute(AssetCache cache);
}
