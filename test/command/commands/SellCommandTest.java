package command.commands;

import api.AssetCache;
import exception.InvalidCommandException;
import model.User;
import model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import repository.UserRepository;
import server.session.ClientContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class SellCommandTest {

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
    void testConstructSellCommandInvalid() {
        when(clientContextMock.isLoggedIn()).thenReturn(true);
        assertThrows(InvalidCommandException.class, () -> new SellCommand(null, clientContextMock, userRepositoryMock), "When assetID is null, InvalidCommandException is expected");

        when(clientContextMock.isLoggedIn()).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> new SellCommand("BTC", clientContextMock, userRepositoryMock), "Parameter 'clientContext' passed to construct SellCommand is not logged in user, IllegalArgumentException expected!");
    }

    @Test
    void testExecuteSellSuccessfully() {
        Wallet wallet = new Wallet();
        wallet.deposit(1000.0);
        wallet.buy("BTC", 100.0, 500.0);

        User user = new User("mihaela@gmail.com", "password", wallet);

        when(clientContextMock.isLoggedIn()).thenReturn(true);
        when(clientContextMock.getLoggedInUser()).thenReturn(user);
        when(assetCacheMock.getAssetPrice("BTC")).thenReturn(150.0);

        SellCommand command = new SellCommand("btc", clientContextMock, userRepositoryMock);

        String result = command.execute(assetCacheMock);

        assertNotNull(result);
        assertEquals("You [ mihaela@gmail.com ] successfully sold BTC", result);

        assertEquals(1250.0, user.wallet().getBalanceUsd(), 0.001);
    }

    @Test
    void testExecuteSellAssetNotFoundInCache() {
        User user = new User("mihaela@gmail.com", "password", new Wallet());
        when(clientContextMock.isLoggedIn()).thenReturn(true);
        when(clientContextMock.getLoggedInUser()).thenReturn(user);

        when(assetCacheMock.getAssetPrice("BTC")).thenReturn(null);

        SellCommand command = new SellCommand("BTC", clientContextMock, userRepositoryMock);
        String result = command.execute(assetCacheMock);

        assertEquals("Asset 'BTC''s price unavailable.", result);
    }

    @Test
    void testExecuteSellAssetNotPresentInWallet() {
        User user = new User("mihaela@gmail.com", "password", new Wallet());
        when(clientContextMock.isLoggedIn()).thenReturn(true);
        when(clientContextMock.getLoggedInUser()).thenReturn(user);
        when(assetCacheMock.getAssetPrice("BTC")).thenReturn(50000.0);

        SellCommand command = new SellCommand("BTC", clientContextMock, userRepositoryMock);
        String result = command.execute(assetCacheMock);

        assertEquals("You [ mihaela@gmail.com ] could not sell BTC! Not present in wallet!", result);
    }

    @Test
    void testExecuteSellWithNullCache() {
        when(clientContextMock.isLoggedIn()).thenReturn(true);
        SellCommand command = new SellCommand("BTC", clientContextMock, userRepositoryMock);

        assertThrows(IllegalArgumentException.class, () -> command.execute(null), "When cache is null, IllegalArgumentException is expected");
    }
}