package command.commands;

import api.AssetCache;
import model.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class ListCommandTest {

    private AssetCache assetCacheMock;

    @BeforeEach
    void setUp() {
        assetCacheMock = Mockito.mock(AssetCache.class);
    }

    @Test
    void testConstructListCommandInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new ListCommand(null), "When ListCommand is constructed with null, IllegalArgumentException is expected");
    }

    @Test
    void testExecuteListCommandWithData() {
        Asset btc = new Asset("BTC", "Bitcoin", 1, 50000.0);
        Asset eth = new Asset("ETH", "Ethereum", 1, 3000.0);
        List<Asset> assets = List.of(btc, eth);

        when(assetCacheMock.getCachedValues()).thenReturn(assets);

        ListCommand command = new ListCommand(assetCacheMock);

        String result = command.execute();

        assertNotNull(result);
        assertTrue(result.contains("All the latest offerings:"));
        assertTrue(result.contains(btc.toString()));
        assertTrue(result.contains(eth.toString()));
    }

    @Test
    void testExecuteListCommandEmptyCache() {
        when(assetCacheMock.getCachedValues()).thenReturn(Collections.emptyList());

        ListCommand command = new ListCommand(assetCacheMock);
        String result = command.execute();

        assertEquals("No information available!", result);
    }

    @Test
    void testExecuteListCommandNullCache() {
        when(assetCacheMock.getCachedValues()).thenReturn(null);

        ListCommand command = new ListCommand(assetCacheMock);
        String result = command.execute();

        assertEquals("No information available!", result);
    }
}