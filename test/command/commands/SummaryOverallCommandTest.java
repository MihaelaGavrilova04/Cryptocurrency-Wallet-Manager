package command.commands;

import api.AssetCache;
import exception.UnauthenticatedException;
import model.Asset;
import model.User;
import model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import server.session.ClientContext;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class SummaryOverallCommandTest {

    private ClientContext clientContextMock;
    private AssetCache assetCacheMock;

    @BeforeEach
    void setUp() {
        clientContextMock = Mockito.mock(ClientContext.class);
        assetCacheMock = Mockito.mock(AssetCache.class);
    }

    @Test
    void testConstructSummaryOverallCommandInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new SummaryOverallCommand(null), "When ClientContext is null, IllegalArgumentException is expected");

        when(clientContextMock.isLoggedIn()).thenReturn(false);
        assertThrows(UnauthenticatedException.class, () -> new SummaryOverallCommand(clientContextMock), "When user is not logged in, UnauthenticatedException is expected");
    }

    @Test
    void testExecuteSummaryOverallCommandSuccessfully() {
        Wallet wallet = new Wallet();
        wallet.deposit(1000.0);
        wallet.buy("BTC", 50000.0, 500.0);
        User user = new User("mihaela@gmail.com", "pass123", wallet);

        Asset btcAsset = new Asset("BTC", "Bitcoin", 1, 55000.0);
        List<Asset> assets = List.of(btcAsset);

        when(clientContextMock.isLoggedIn()).thenReturn(true);
        when(clientContextMock.getLoggedInUser()).thenReturn(user);
        when(assetCacheMock.getCachedValues()).thenReturn(assets);

        SummaryOverallCommand command = new SummaryOverallCommand(clientContextMock);
        String result = command.execute(assetCacheMock);

        assertNotNull(result);
        assertTrue(result.contains("WALLET OVERALL SUMMARY:"));
        assertTrue(result.contains("Total Invested: $ 500,00"));
        assertTrue(result.contains("Current Value: $ 550,00"));
        assertTrue(result.contains("Profit/Loss: $ 50,00"));
        assertTrue(result.contains("Return percentage: 10,00"));
    }

    @Test
    void testExecuteSummaryOverallCommandEmptyCache() {
        User user = new User("petya@abv.bg", "pass123", new Wallet());

        when(clientContextMock.isLoggedIn()).thenReturn(true);
        when(clientContextMock.getLoggedInUser()).thenReturn(user);
        when(assetCacheMock.getCachedValues()).thenReturn(Collections.emptyList());

        SummaryOverallCommand command = new SummaryOverallCommand(clientContextMock);
        String result = command.execute(assetCacheMock);

        assertEquals("No data for available assets present.", result);
    }

    @Test
    void testExecuteSummaryOverallCommandNullCache() {
        when(clientContextMock.isLoggedIn()).thenReturn(true);
        SummaryOverallCommand command = new SummaryOverallCommand(clientContextMock);

        assertThrows(IllegalArgumentException.class, () -> command.execute(null));
    }

    private void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Condition expected to be true");
        }
    }
}