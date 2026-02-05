package command.unauthenticated;

import command.Command;

import java.nio.channels.SelectionKey;

public class ListCommand implements Command {
    @Override
    public String execute(String[] input, SelectionKey key) {
        return "";
    }
}
