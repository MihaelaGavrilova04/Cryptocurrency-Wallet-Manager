package command.commands;

import api.AssetCache;
import exception.InvalidCommandException;
import exception.UnauthenticatedException;
import model.User;
import model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repository.UserRepository;
import server.session.ClientContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DepositCommandTest {

    private ClientContext clientContextMock;
    private UserRepository userRepositoryMock;
    private AssetCache assetCacheMock;

    @BeforeEach
    void setUp() {
        clientContextMock = Mockito.mock(ClientContext.class);
        userRepositoryMock = Mockito.mock(UserRepository.class);
        assetCacheMock = Mockito.mock(AssetCache.class);
    }

    @Test
    void testConstructDepositCommandInvalid() {
        when(clientContextMock.isLoggedIn()).thenReturn(false);
        assertThrows(UnauthenticatedException.class, () -> new DepositCommand(100.0, clientContextMock, userRepositoryMock), "When user is not logged in, UnauthenticatedException is expected");

        when(clientContextMock.isLoggedIn()).thenReturn(true);
        assertThrows(InvalidCommandException.class, () -> new DepositCommand(-50.0, clientContextMock, userRepositoryMock), "Can not deposit negative amount");

        assertThrows(IllegalArgumentException.class, () -> new DepositCommand(100.0, null, userRepositoryMock), "When param of type 'ClientContext' is passed, IllegalArgumentException expected");

        assertThrows(InvalidCommandException.class, () -> new DepositCommand(100.0, clientContextMock, null), "When param of type 'UserRepository' is passed, IllegalArgumentException expected");
    }

    @Test
    void testExecuteDepositCommandSuccessfully() {
        double depositAmount = 500.0;
        Wallet wallet = new Wallet();
        User user = new User("example@abv.bg", "password", wallet);

        when(clientContextMock.isLoggedIn()).thenReturn(true);
        when(clientContextMock.getLoggedInUser()).thenReturn(user);

        DepositCommand depositCommand = new DepositCommand(depositAmount, clientContextMock, userRepositoryMock);

        String result = depositCommand.execute(assetCacheMock);

        assertNotNull(result);
        assertEquals("$500,00 deposited successfully to example@abv.bg's balance", result);

        assertEquals(500.0, user.wallet().getBalanceUsd(), 0.001);

        verify(userRepositoryMock, times(1)).updateUser(user);
    }

    @Test
    void testDepositCommandType() {
        when(clientContextMock.isLoggedIn()).thenReturn(true);
        Command command = new DepositCommand(100.0, clientContextMock, userRepositoryMock);

        assertInstanceOf(DepositCommand.class, command);
    }

}
