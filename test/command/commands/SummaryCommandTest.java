package command.commands;

import api.AssetCache;
import exception.UnauthenticatedException;
import model.User;
import model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import server.session.ClientContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class SummaryCommandTest {

    private ClientContext clientContextMock;
    private AssetCache assetCacheMock;

    @BeforeEach
    void setUp() {
        clientContextMock = Mockito.mock(ClientContext.class);
        assetCacheMock = Mockito.mock(AssetCache.class);
    }

    @Test
    void testConstructSummaryCommandInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new SummaryCommand(null), "When ClientContext is null, IllegalArgumentException is expected");

        when(clientContextMock.isLoggedIn()).thenReturn(false);
        assertThrows(UnauthenticatedException.class, () -> new SummaryCommand(clientContextMock), "When user not logged in, UnauthenticatedException is expected");
    }

    @Test
    void testExecuteSummaryCommandSuccessfully() {
        Wallet wallet = new Wallet();
        wallet.deposit(100.0);
        User user = new User("mihaela@gmail.com", "pass123", wallet);

        when(clientContextMock.isLoggedIn()).thenReturn(true);
        when(clientContextMock.getLoggedInUser()).thenReturn(user);

        SummaryCommand command = new SummaryCommand(clientContextMock);
        String result = command.execute(assetCacheMock);

        assertNotNull(result);
        assertEquals(user.wallet().getWalletSummary(), result);
    }

    @Test
    void testExecuteSummaryCommandWithNoTransactions() {
        User user = new User("mihaela@gmail.com", "pass123", new Wallet());

        when(clientContextMock.isLoggedIn()).thenReturn(true);
        when(clientContextMock.getLoggedInUser()).thenReturn(user);

        SummaryCommand command = new SummaryCommand(clientContextMock);
        String result = command.execute(assetCacheMock);

        assertNotNull(result);
        assertEquals(user.wallet().getWalletSummary(), result);
    }
}
