package command.commands;

import api.AssetCache;
import model.User;

public final class DepositCommand implements AuthenticatedCommand {
    private final double moneyToDeposit;
    private static final double EPSILON = 0.000001;

    public DepositCommand(double moneyToDeposit) {
        validateObjectConstruction(moneyToDeposit);
        this.moneyToDeposit = moneyToDeposit;
    }

    @Override
    public String execute(User user, AssetCache cache) {
        validateUser(user);

        user.wallet().deposit(moneyToDeposit);

        return String.format("$%.2f deposited successfully to %s's balance",
                moneyToDeposit, user.email());
    }

    private static void validateObjectConstruction(double money) {
        if (money < EPSILON) {
            throw new IllegalArgumentException("Parameter 'money' passed to construct DepositCommand is invalid!");
        }
    }

    private static void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Parameter 'user' passed to execute DepositCommand is null!");
        }
    }
}
