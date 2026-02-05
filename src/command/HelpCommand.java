package command.unauthenticated;

import command.Command;

import java.nio.channels.SelectionKey;

public final class HelpCommand implements PublicCommand {
    @Override
    public String execute(String[] input, SelectionKey key) {
        return "";
    }
}
