package command.type;

public enum CommandType {

    REGISTER("register", 2),
    LOGIN("login", 2),
    DEPOSIT("deposit", 1),
    LIST("list-offerings", 0),
    BUY("buy", 2),
    SELL("sell", 1),
    SUMMARY("get-wallet-summary", 0),
    SUMMARY_OVERALL("get-wallet-overall-summary", 0),
    HELP("help", 0),
    LOGOUT("logout", 0);

    private final String command;
    private final int numArgs;

    private CommandType(String command, int numArgs) {
        this.command = command;
        this.numArgs = numArgs;
    }

    public String getCommand() {
        return command;
    }

    public int getNumArgs() {
        return numArgs;
    }

    public CommandType findCommandType(String commandStr) {
        if (commandStr == null || commandStr.isEmpty()) {
            throw new IllegalArgumentException("Argument command passed is invalid!");
        }

        for (CommandType commandType : values()) {
            if (getCommand().equalsIgnoreCase(commandStr)) {
                return commandType;
            }
        }

        throw new RuntimeException("No such command present!");
    }

    public static boolean requiresAuthentication(CommandType type) {
        return switch (type) {
            case REGISTER, LOGIN, LIST, HELP -> false;
            case BUY, SELL, DEPOSIT, SUMMARY, SUMMARY_OVERALL, LOGOUT -> true;
        };
    }
}
