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
import exception.InvalidCommandException;
import logger.Logger;
import model.Asset;
import repository.UserRepository;
import server.session.ClientContext;

import java.util.List;

public class CommandFactory {
    private static final String DELIMITER = "\\s+";

    private static final String USERNAME_FLAG = "--username=";
    private static final String PASSWORD_FLAG = "--password=";
    private static final String ASSET_FLAG = "--offering=";
    private static final String MONEY_FLAG = "--money=";

    private static final int COMMAND_INDEX = 0;
    private static final int USERNAME_INDEX = 1;
    private static final int PASSWORD_INDEX = 2;
    private static final int DEPOSIT_AMOUNT_INDEX = 1;
    private static final int ASSET_ID_INDEX = 1;
    private static final int BUY_MONEY_AMOUNT_INDEX = 2;

    private final UserRepository repository;
    private final AssetCache assetCache;

    private static final Logger LOGGER = Logger.getInstance();

    public CommandFactory(UserRepository repository, AssetCache assetCache) {
        validateObjectConstruction(repository, assetCache);

        this.repository = repository;
        this.assetCache = assetCache;
    }

    public Command getCommand(String inputString, ClientContext context) {
        validateInput(inputString);
        validateContext(context);

        String[] input = parseInput(inputString);
        return getCommandByString(input, context);
    }

    public List<Asset> getCachedValues() {
        return assetCache.getCachedValues();
    }

    public AssetCache getAssetCache() {
        return assetCache;
    }

    private String[] parseInput(String inputString) {
        return inputString.strip().split(DELIMITER);
    }

    private Command getCommandByString(String[] tokens, ClientContext context) {

        String commandString = tokens[COMMAND_INDEX].toLowerCase();

        Command toReturn = switch (commandString) {
            case "register" -> createRegisterCommand(tokens);
            case "login" -> createLoginCommand(tokens, context);
            case "deposit" -> createDepositCommand(tokens, context);
            case "list-offerings" -> createListOfferingsCommand();
            case "buy" -> createBuyCommand(tokens, context);
            case "sell" -> createSellCommand(tokens, context);
            case "get-wallet-summary" -> createSummaryCommand(context);
            case "get-wallet-overall-summary" -> createSummaryOverallCommand(context);
            case "help" -> createHelpCommand();
            case "logout" -> createLogoutCommand(context);
            default -> throw new InvalidCommandException("No such command present!");
        };

        return toReturn;
    }

    private Command createRegisterCommand(String[] tokens) {
        validateNumberOfArgs(tokens, CommandType.REGISTER.getNumArgs());
        validateCommandFlags(tokens, "register");

        String username = tokens[USERNAME_INDEX].replace(USERNAME_FLAG, "");
        String password = tokens[PASSWORD_INDEX].replace(PASSWORD_FLAG, "");

        return new RegisterCommand(repository, username, password);
    }

    private Command createLoginCommand(String[] tokens, ClientContext context) {
        validateNumberOfArgs(tokens, CommandType.LOGIN.getNumArgs());
        validateCommandFlags(tokens, "login");

        String username = tokens[USERNAME_INDEX].replace(USERNAME_FLAG, "");
        String password = tokens[PASSWORD_INDEX].replace(PASSWORD_FLAG, "");

        return new LoginCommand(repository, username, password, context);
    }

    private static void validateCommandFlags(String[] tokens, String command) {
        if (!tokens[USERNAME_INDEX].contains(USERNAME_FLAG)) {
            throw new InvalidCommandException(
                    String.format("To %s, check command via 'help'! %s is expected!", command, USERNAME_FLAG));
        }

        if (!tokens[PASSWORD_INDEX].contains(PASSWORD_FLAG)) {
            throw new InvalidCommandException(
                    String.format("To register, check command via 'help'! %s is expected!", PASSWORD_FLAG));
        }
    }

    private Command createDepositCommand(String[] tokens, ClientContext context) {
        validateNumberOfArgs(tokens, CommandType.DEPOSIT.getNumArgs());

        return new DepositCommand(parseDoubleSafely(tokens[DEPOSIT_AMOUNT_INDEX]), context, repository);
    }

    private Command createListOfferingsCommand() {
        return new ListCommand(assetCache);
    }

    private Command createBuyCommand(String[] tokens, ClientContext context) {
        validateNumberOfArgs(tokens, CommandType.BUY.getNumArgs());

        if (!tokens[ASSET_ID_INDEX].contains(ASSET_FLAG)) {
            throw new InvalidCommandException(
                    String.format("To buy an asset, check command via 'help'! %s is expected!", ASSET_FLAG));
        }

        if (!tokens[BUY_MONEY_AMOUNT_INDEX].contains(MONEY_FLAG)) {
            throw new InvalidCommandException(
                    String.format("To buy an asset, check command via 'help'! %s is expected!", MONEY_FLAG));
        }

        String assetId = tokens[ASSET_ID_INDEX].replace(ASSET_FLAG, "");
        double amount = parseDoubleSafely(tokens[BUY_MONEY_AMOUNT_INDEX].replace(MONEY_FLAG, ""));

        return new BuyCommand(assetId, amount, context, repository);
    }

    private Command createSellCommand(String[] tokens, ClientContext context) {

        validateNumberOfArgs(tokens, CommandType.SELL.getNumArgs());

        if (!tokens[ASSET_ID_INDEX].contains(ASSET_FLAG)) {
            throw new InvalidCommandException(
                    String.format("To sell an asset, check command via 'help'! %s is expected!", ASSET_FLAG));
        }

        String assetId = tokens[ASSET_ID_INDEX].replace(ASSET_FLAG, "");
        return new SellCommand(assetId, context, repository);
    }

    private Command createSummaryCommand(ClientContext context) {
        return new SummaryCommand(context);
    }

    private Command createSummaryOverallCommand(ClientContext context) {
        return new SummaryOverallCommand(context);
    }

    private Command createHelpCommand() {
        return new HelpCommand();
    }

    private Command createLogoutCommand(ClientContext context) {
        return new LogoutCommand(context);
    }

    private double parseDoubleSafely(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            LOGGER.log(e, "USER.INPUT.INVALID");
            throw new InvalidCommandException("'" + value + "' is not a valid double!");
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
            throw new InvalidCommandException("The command passed is invalid. Try 'help' to get additional info!");
        }
    }

    private void validateContext(ClientContext clientContext) {
        if (clientContext == null) {
            throw new IllegalArgumentException("Server passed the session context as null!");
        }
    }

    private static void validateNumberOfArgs(String[] tokens , int numArgsExpected) {
        int numArgsPassed = tokens.length - 1;

        if (numArgsExpected != numArgsPassed) {
            throw new InvalidCommandException("Invalid number of arguments passed. Try 'help' for additional info!");
        }
    }
}