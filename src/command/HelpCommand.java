package command;

public final class HelpCommand implements PublicCommand {

    @Override
    public String execute() {
        StringBuilder sb = new StringBuilder();

        sb.append("Crypto Wallet" + System.lineSeparator());
        sb.append("Available Commands:" + System.lineSeparator());

        sb.append("[Public]" + System.lineSeparator());
        sb.append("register --username=<user> --password=<pass>  -> Register a new user" + System.lineSeparator());
        sb.append("login --username=<user> --password=<pass>     -> Log into the system" + System.lineSeparator());
        sb.append("list-offerings                                -> Get all cryptocurrencies" + System.lineSeparator());
        sb.append("help                                          -> Show help message" + System.lineSeparator());

        sb.append("[Authenticated]" + System.lineSeparator());
        sb.append("deposit-money <amount>                         -> Add money wallet (USD)" + System.lineSeparator());
        sb.append("buy --offering=<code> --money=<amount>         -> Get crypto for amount" + System.lineSeparator());
        sb.append("sell --offering=<code>                         -> Sell all of crypto type" + System.lineSeparator());
        sb.append("get-wallet-summary                             -> View active investments" + System.lineSeparator());
        sb.append("get-wallet-overall-summary                     -> View total profit/loss" + System.lineSeparator());
        sb.append("logout                                         -> Log out of the system" + System.lineSeparator());

        return sb.toString();
    }
}