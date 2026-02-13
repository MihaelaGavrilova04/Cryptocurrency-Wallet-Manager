package command;

import api.AssetCache;
import com.google.errorprone.annotations.RestrictedApi;
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
import exception.InvalidCommandException;
import model.Asset;
import model.User;
import model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repository.UserRepository;
import server.session.ClientContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class CommandFactoryTest {

    private UserRepository repositoryMock;
    private AssetCache assetCacheMock;
    private CommandFactory commandFactory;
    private ClientContext contextMock;

    @BeforeEach
    void setUp() {
        repositoryMock = Mockito.mock(UserRepository.class);
        assetCacheMock = Mockito.mock(AssetCache.class);
        contextMock = Mockito.mock(ClientContext.class);

        commandFactory = new CommandFactory(repositoryMock, assetCacheMock);
    }

    @Test
    void testCommandFactoryConstructionWithValidArguments() {
        assertNotNull(commandFactory);
        assertEquals(assetCacheMock, commandFactory.getAssetCache());
    }

    @Test
    void testConstructionOnNullRepository() {
        assertThrows(IllegalArgumentException.class, () ->
                new CommandFactory(null, assetCacheMock));
    }

    @Test
    void testConstructionOnNullCache() {
        assertThrows(IllegalArgumentException.class, () ->
                new CommandFactory(repositoryMock, null));
    }

    @Test
    void testGetCommandInvalidInputString() {
        assertThrows(InvalidCommandException.class, () -> commandFactory.getCommand(null, contextMock), "InvalidCommandException is expected when inputString is invalid");
    }

    @Test
    void testGetCommandNullContext() {
        assertThrows(IllegalArgumentException.class, () -> commandFactory.getCommand("dummy-input-string", null), "IllegalArgumentException is expected when clientContext is null");
    }

    @Test
    void testGetLoginCommand() {
        Command loginCmd = commandFactory.getCommand("login --username=example@abv.bg --password=example123", contextMock);

        assertNotNull(loginCmd);
        assertInstanceOf(LoginCommand.class, loginCmd);
    }

    @Test
    void testGetLoginCommandInvalidFlags() {
        assertThrows(InvalidCommandException.class, ()->commandFactory.getCommand("login --usr=example@abv.bg --password=example123", contextMock), "InvalidCommandException is expected when command's flags are invalid");
        assertThrows(InvalidCommandException.class, ()->commandFactory.getCommand("login --username=example@abv.bg --pass=example123", contextMock), "InvalidCommandException is expected when command's flags are invalid");
    }

    @Test
    void testGetRegisterCommand() {
        Command registerCmd = commandFactory.getCommand("register --username=example@abv.bg --password=example123", contextMock);

        assertNotNull(registerCmd);
        assertInstanceOf(RegisterCommand.class, registerCmd);
    }

    @Test
    void testGetRegisterCommandInvalidFlags() {
        assertThrows(InvalidCommandException.class, ()->commandFactory.getCommand("register --usr=example@abv.bg --password=example123", contextMock), "InvalidCommandException is expected when command's flags are invalid");
        assertThrows(InvalidCommandException.class, ()->commandFactory.getCommand("register --username=example@abv.bg --pass=example123", contextMock), "InvalidCommandException is expected when command's flags are invalid");
    }

//        sb.append("list-offerings
//        sb.append("help
//
//        sb.append("[Authenticated]").append(System.lineSeparato
//        sb.append("deposit <amount>
//        sb.append("buy --offering=<code> --money=<amount>
//        sb.append("sell --offering=<code>
//        sb.append("get-wallet-summary
//        sb.append("get-wallet-overall-summary
//        sb.append("logout
    @Test
    void testGetBuyCommand() {
        when(contextMock.getLoggedInUser()).thenReturn(new User("example@abv.bg", "password", new Wallet()));
        when(contextMock.isLoggedIn()).thenReturn(true);

        contextMock.getLoggedInUser().wallet().deposit(1000);

        Command buyCmd = commandFactory.getCommand("buy --offering=BTC --money=20.00", contextMock);

        assertNotNull(buyCmd);
        assertInstanceOf(BuyCommand.class, buyCmd);
    }

    @Test
    void testGetBuyCommandInvalidFlags() {
        assertThrows(InvalidCommandException.class, ()->commandFactory.getCommand("buy --offer=BTC --money=20.00", contextMock), "InvalidCommandException is expected when command's flags are invalid");
        assertThrows(InvalidCommandException.class, ()->commandFactory.getCommand("--offering=BTC --money=-20.00", contextMock), "InvalidCommandException is expected when command's flags are invalid");
    }

    @Test
    void testGetSellCommand() {
        when(contextMock.getLoggedInUser()).thenReturn(new User("example@abv.bg", "password", new Wallet()));
        when(contextMock.isLoggedIn()).thenReturn(true);

        contextMock.getLoggedInUser().wallet().deposit(1000.00);
        contextMock.getLoggedInUser().wallet().buy("BTC", 43000.0, 0.01);

        Command sellCmd = commandFactory.getCommand("sell --offering=BTC", contextMock);

        assertNotNull(sellCmd);
        assertInstanceOf(SellCommand.class, sellCmd);
        assertEquals(1000.00, contextMock.getLoggedInUser().wallet().getBalanceUsd(), 0.011);
    }

    @Test
    void testGetSellCommandInvalidFlags() {
        assertThrows(InvalidCommandException.class, ()->commandFactory.getCommand("sell --offer=BTC", contextMock), "InvalidCommandException is expected when command's flags are invalid");
        assertThrows(InvalidCommandException.class, ()->commandFactory.getCommand("--offering=BTC --money=-20.00", contextMock), "InvalidCommandException is expected when command's flags are invalid");
    }

    @Test
    void testGetDepositCommand() {
        when(contextMock.getLoggedInUser()).thenReturn(new User("example@abv.bg", "password", new Wallet()));
        when(contextMock.isLoggedIn()).thenReturn(true);

        Command depositCmd = commandFactory.getCommand("deposit 1000.00", contextMock);

        assertNotNull(depositCmd);
        assertInstanceOf(DepositCommand.class, depositCmd);
    }

    @Test
    void testGetDepositCommandInvalidFlags() {
        assertThrows(InvalidCommandException.class, ()->commandFactory.getCommand("deposit -103.00", contextMock), "InvalidCommandException is expected when command's flags are invalid");
    }

    @Test
    void testGetHelpCommand() {
        Command helpCommand = commandFactory.getCommand("help", contextMock);
        assertInstanceOf(HelpCommand.class, helpCommand);
    }

    @Test
    void testListCommand() {
        Command listCmd = commandFactory.getCommand("list-offerings", contextMock);

        assertNotNull(listCmd);
        assertInstanceOf(ListCommand.class, listCmd);
    }

    @Test
    void testLogoutCommand() {
        Command logoutCmd = commandFactory.getCommand("logout", contextMock);

        assertNotNull(logoutCmd);
        assertInstanceOf(LogoutCommand.class, logoutCmd);
    }

    @Test
    void testSummaryCommand() {
        when(contextMock.getLoggedInUser()).thenReturn(new User("example@abv.bg", "password", new Wallet()));
        when(contextMock.isLoggedIn()).thenReturn(true);

        Command summaryCommand = commandFactory.getCommand("get-wallet-summary", contextMock);

        assertNotNull(summaryCommand);
        assertInstanceOf(SummaryCommand.class, summaryCommand);
    }

    @Test
    void testSummaryOverallCommand() {
        when(contextMock.getLoggedInUser()).thenReturn(new User("example@abv.bg", "password", new Wallet()));
        when(contextMock.isLoggedIn()).thenReturn(true);

        Command summaryOverallCommand = commandFactory.getCommand("get-wallet-overall-summary", contextMock);

        assertNotNull(summaryOverallCommand);
        assertInstanceOf(SummaryOverallCommand.class, summaryOverallCommand);
    }

    @Test
    void getCommandByStringInvalidCommandException() {
        assertThrows(InvalidCommandException.class, ()->commandFactory.getCommand("invalid-command", contextMock));
    }

    @Test
    void testGetAssetCacheSameObject() {
        assertSame(assetCacheMock, commandFactory.getAssetCache());
    }

    @Test
    void testGetCachedValuesSameObject() {
        assertNotSame(assetCacheMock.getCachedValues(), commandFactory.getAssetCache());
    }

}
