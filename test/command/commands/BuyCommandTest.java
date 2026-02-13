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
import static org.mockito.Mockito.when;

public class BuyCommandTest {

    private BuyCommand command;
    private ClientContext clientContextMock;
    private UserRepository userRepositoryMock;
    private AssetCache assetCacheMock;

    @BeforeEach
    void setUp() {
        clientContextMock = Mockito.mock(ClientContext.class);
        userRepositoryMock = Mockito.mock(UserRepository.class);
    }

    @Test
    void testConstructBuyCommandObjectInvalid() {
        assertThrows(UnauthenticatedException.class, () -> new BuyCommand("btc", 100.00, clientContextMock, userRepositoryMock));

        when(clientContextMock.isLoggedIn()).thenReturn(true);

        assertThrows(InvalidCommandException.class, () -> new BuyCommand(null, 100.00, clientContextMock, userRepositoryMock));
        assertThrows(InvalidCommandException.class, () -> new BuyCommand("btc", -0.2, clientContextMock, userRepositoryMock));
        assertThrows(IllegalArgumentException.class, () -> new BuyCommand("btc", 100.00, null, userRepositoryMock));
        assertThrows(IllegalArgumentException.class, () -> new BuyCommand("btc", 100.00, clientContextMock, null));
    }

    @Test
    void testConstructBuyCommand() {
        when(clientContextMock.isLoggedIn()).thenReturn(true);

        Command buyCommand = new BuyCommand("BTC", 100.00, clientContextMock, userRepositoryMock);

        assertNotNull(buyCommand);
        assertInstanceOf(BuyCommand.class, buyCommand);
    }

    @Test
    void testExecuteBuyCommand() {
        Wallet wallet = new Wallet();
        wallet.deposit(10000.00);
        User user = new User("email@abv.bg", "password", wallet);

        when(clientContextMock.getLoggedInUser()).thenReturn(user);
        when(clientContextMock.isLoggedIn()).thenReturn(true);

        Command buyCommand = new BuyCommand("btc", 100.00, clientContextMock, userRepositoryMock);

        assetCacheMock = Mockito.mock(AssetCache.class);

        when(assetCacheMock.getAssetPrice("BTC")).thenReturn(100.00);

        String result = buyCommand.execute(assetCacheMock);

        assertNotNull(result);
        assertEquals("Successfully bought asset with id : BTC!", result);
    }

    @Test
    void testExecuteBuyCommandAssetNotFound() {
        when(clientContextMock.isLoggedIn()).thenReturn(true);
        when(clientContextMock.getLoggedInUser()).thenReturn(new User("example@abv.bg", "pass", new Wallet()));
        assetCacheMock = Mockito.mock(AssetCache.class);

        when(assetCacheMock.getAssetPrice("NON_EXISTENT")).thenReturn(null);

        Command buyCommand = new BuyCommand("non_existent", 100.00, clientContextMock, userRepositoryMock);
        String result = buyCommand.execute(assetCacheMock);

        assertEquals("Information about asset NON_EXISTENT not found.", result);
    }
}
