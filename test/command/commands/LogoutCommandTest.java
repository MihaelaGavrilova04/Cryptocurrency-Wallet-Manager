package command.commands;

import api.AssetCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import server.session.ClientContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LogoutCommandTest {

    private ClientContext clientContextMock;
    private AssetCache assetCacheMock;
    private static final String EXPECTED_LOGOUT_RESULT = "Logged out successfully!";

    @BeforeEach
    void setUp() {
        clientContextMock = Mockito.mock(ClientContext.class);
        assetCacheMock = Mockito.mock(AssetCache.class);
    }

    @Test
    void testExecuteLogoutCommandSuccessfully() {
        LogoutCommand logoutCommand = new LogoutCommand(clientContextMock);
        String result = logoutCommand.execute(assetCacheMock);

        assertNotNull(result);
        assertEquals(EXPECTED_LOGOUT_RESULT, result);

        verify(clientContextMock, times(1)).logout();
    }

    @Test
    void testLogoutCommandWithNullCache() {
        LogoutCommand logoutCommand = new LogoutCommand(clientContextMock);

        String result = logoutCommand.execute(null);

        assertEquals(EXPECTED_LOGOUT_RESULT, result);
        verify(clientContextMock, times(1)).logout();
    }
}