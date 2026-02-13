package command.commands;

import api.AssetCache;
import exception.InvalidCommandException;
import exception.UnauthenticatedException;
import model.User;
import repository.UserRepository;
import server.session.ClientContext;

public final class DepositCommand implements AuthenticatedCommand {
    private final double moneyToDeposit;
    private static final double EPSILON = 0.000001;

    private final ClientContext clientContext;
    private final UserRepository userRepository;

    public DepositCommand(double moneyToDeposit, ClientContext clientContext, UserRepository userRepository) {
        validateObjectConstruction(moneyToDeposit, clientContext, userRepository);

        this.moneyToDeposit = moneyToDeposit;
        this.clientContext = clientContext;
        this.userRepository = userRepository;
    }

    @Override
    public String execute(AssetCache cache) {

        User loggedInUser = clientContext.getLoggedInUser();
        loggedInUser.wallet().deposit(moneyToDeposit);

        userRepository.updateUser(loggedInUser);

        return String.format("$%.2f deposited successfully to %s's balance",
                moneyToDeposit, loggedInUser.email());
    }

    private static void validateObjectConstruction(double money, ClientContext clientContext,
                                                   UserRepository userRepository) {
        if (money < EPSILON) {
            throw new InvalidCommandException("The amount of money is expected to be positive when depositing!");
        }

        if (clientContext == null) {
            throw new IllegalArgumentException("Parameter 'ClientContext' passed to construct DepositCommand is null!");
        }

        if (!clientContext.isLoggedIn()) {
            throw new UnauthenticatedException("You should log in to deposit money!");
        }

        if (userRepository == null) {
            throw new InvalidCommandException("Parameter 'userRepository' passed to construct DepositCommand is null!");
        }
    }

}
