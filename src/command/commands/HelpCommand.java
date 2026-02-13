package command.commands;

public final class HelpCommand implements PublicCommand {

    @Override
    public String execute() {
        StringBuilder sb = new StringBuilder();

        sb.append("Crypto Wallet").append(System.lineSeparator());
        sb.append("Available Commands:").append(System.lineSeparator());

        sb.append("[Public]").append(System.lineSeparator());
        sb.append("register --username=<user> --password=<pass> -> Register a new user").append(System.lineSeparator());
        sb.append("login --username=<user> --password=<pass>    -> Log into the system").append(System.lineSeparator());
        sb.append("list-offerings                               -> Cryptocurrencies").append(System.lineSeparator());
        sb.append("help                                         -> Show help message").append(System.lineSeparator());

        sb.append("[Authenticated]").append(System.lineSeparator());
        sb.append("deposit <amount>                             -> Add money wallet").append(System.lineSeparator());
        sb.append("buy --offering=<code> --money=<amount>       -> Buy asset of amount").append(System.lineSeparator());
        sb.append("sell --offering=<code>                       -> Sell asset type").append(System.lineSeparator());
        sb.append("get-wallet-summary                           -> View investments").append(System.lineSeparator());
        sb.append("get-wallet-overall-summary                   -> View total profit").append(System.lineSeparator());
        sb.append("logout                                       -> Log out of system").append(System.lineSeparator());

        return sb.toString();
    }
}