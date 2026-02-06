package command;

import api.AssetCache;
import command.commands.BuyCommand;
import command.commands.Command;
import command.commands.DepositCommand;
import command.commands.HelpCommand;
import command.commands.ListCommand;
import command.commands.LoginCommand;
import command.commands.LogoutCommand;
import command.commands.RegisterCommand;
import command.commands.SellCommand;
import command.commands.SummaryCommand;
import command.commands.SummaryOverallCommand;
import command.type.CommandType;
import logger.Logger;
import repository.UserRepository;

public class CommandFactory {
    private static final String DELIMITER = "\\s+";
    private static final int COMMAND_INDEX = 0;

    private static final String ASSET_FLAG = "--offering=";
    private static final String MONEY_FLAG = "--money=";

    private static final int USERNAME_INDEX = 1;
    private static final int PASSWORD_INDEX = 2;
    private static final int DEPOSIT_AMOUNT_INDEX = 1;
    private static final int ASSET_ID = 1;
    private static final int BUY_MONEY_AMOUNT = 2;

    private final UserRepository repository;
    private final AssetCache assetCache;

    private static final Logger LOGGER = Logger.getInstance();

    public CommandFactory(UserRepository repository, AssetCache assetCache) {
        validateObjectConstruction(repository, assetCache);

        this.repository = repository;
        this.assetCache = assetCache;
    }

    public Command getCommand(String inputString) {
        validateInput(inputString);

        String[] input = parseInput(inputString);
        return getCommandByString(input);
    }

    private String[] parseInput(String inputString) {
        return inputString.strip().split(DELIMITER);
    }

    private Command getCommandByString(String[] tokens) {

        String commandString = tokens[COMMAND_INDEX].toLowerCase();

        Command toReturn = switch (commandString) {
            case "register" -> createRegisterCommand(tokens);
            case "login" -> createLoginCommand(tokens);
            case "deposit" -> createDepositCommand(tokens);
            case "list-offerings" -> createListOfferingsCommand();
            case "buy" -> createBuyCommand(tokens);
            case "sell" -> createSellCommand(tokens);
            case "get-wallet-summary" -> createSummaryCommand();
            case "get-wallet-overall-summary" -> createSummaryOverallCommand();
            case "help" -> createHelpCommand();
            case "logout" -> createLogoutCommand();
            default -> throw new IllegalArgumentException("No such command present!");
        };

        return toReturn;
    }

    private Command createRegisterCommand(String[] tokens) {
        int numArgsPassed = tokens.length - 1;

        if (CommandType.REGISTER.getNumArgs() != numArgsPassed) {
            throw new IllegalArgumentException("Invalid number of args passed");
        }

        return new RegisterCommand(repository, tokens[USERNAME_INDEX], tokens[PASSWORD_INDEX]);
    }

    private Command createLoginCommand(String[] tokens) {
        int numArgsPassed = tokens.length - 1;

        if (CommandType.LOGIN.getNumArgs() != numArgsPassed) {
            throw new IllegalArgumentException("Invalid number of args passed");
        }

        return new LoginCommand(repository, tokens[USERNAME_INDEX], tokens[PASSWORD_INDEX]);
    }

    private Command createDepositCommand(String[] tokens) {
        int numArgsPassed = tokens.length - 1;

        if (CommandType.DEPOSIT.getNumArgs() != numArgsPassed) {
            throw new IllegalArgumentException("Invalid number of args passed");
        }

        return new DepositCommand(parseDoubleSafely(tokens[DEPOSIT_AMOUNT_INDEX]));
    }

    private Command createListOfferingsCommand() {
        return new ListCommand(assetCache);
    }

    private Command createBuyCommand(String[] tokens) {
        int numArgsPassed = tokens.length - 1;

        if (CommandType.BUY.getNumArgs() != numArgsPassed) {
            throw new IllegalArgumentException("Invalid number of args passed");
        }

        String assetId = tokens[ASSET_ID].replace(ASSET_FLAG, "");
        double amount = parseDoubleSafely(tokens[BUY_MONEY_AMOUNT].replace(MONEY_FLAG, ""));

        return new BuyCommand(assetId, amount);
    }

    private Command createSellCommand(String[] tokens) {
        int numArgsPassed = tokens.length - 1;

        if (CommandType.SELL.getNumArgs() != numArgsPassed) {
            throw new IllegalArgumentException("Invalid number of args passed");
        }

        String assetId = tokens[ASSET_ID].replace(ASSET_FLAG, "");
        return new SellCommand(assetId);
    }

    private Command createSummaryCommand() {
        return new SummaryCommand();
    }

    private Command createSummaryOverallCommand() {
        return new SummaryOverallCommand();
    }

    private Command createHelpCommand() {
        return new HelpCommand();
    }

    private Command createLogoutCommand() {
        return new LogoutCommand();
    }

    private double parseDoubleSafely(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            LOGGER.log(e, "USER.INPUT.INVALID");
            throw new IllegalArgumentException("'" + value + "' is not a valid double!");
        }
    }

    private static void validateObjectConstruction(UserRepository repository, AssetCache assetCache) {
        if (repository == null) {
            throw new IllegalArgumentException("Parameter 'repository' used to construct CommandFactory is null!");
        }

        if (assetCache == null) {
            throw new IllegalArgumentException("Parameter 'assetCache' used to construct CommandFactory is null!");
        }
    }

    private static void validateInput(String inputString) {
        if (inputString == null || inputString.isBlank()) {
            throw new IllegalArgumentException("Parameter 'inputString' is invalid");
        }
    }
}