package command.authenticated;

import command.Command;

import java.nio.channels.SelectionKey;

public class SummaryOverallCommand implements Command {
    @Override
    public String execute(String[] input, SelectionKey key) {
        return "";
    }
}
