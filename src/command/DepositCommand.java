package command;

import api.AssetCache;
import model.User;

public final class DepositCommand implements AuthenticatedCommand {
    private final double moneyToDeposit;

    public DepositCommand(double moneyToDeposit) {
        this.moneyToDeposit = moneyToDeposit;
    }

    @Override
    public String execute(User user, AssetCache cache) {
        user.wallet().deposit(moneyToDeposit);

        return String.format("$%.2f deposited successfully to %s's balance",
                moneyToDeposit, user.email());    }
}
