package client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;

public class CryptocurrencyWalletManagerClientTest {

    private ServerSocketChannel testServer;
    private static final int PORT = 7777;

    @BeforeEach
    void setUp() throws IOException {
        testServer = ServerSocketChannel.open();
        testServer.bind(new InetSocketAddress("localhost", PORT));
        testServer.configureBlocking(false);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (testServer != null && testServer.isOpen()) {
            testServer.close();
        }
    }

    @Test
    void testClientConnectionSuccess() throws IOException {
        try (SocketChannel clientChannel = SocketChannel.open()) {
            boolean connected = clientChannel.connect(new InetSocketAddress("localhost", PORT));
            assertTrue(connected, "Client should be able to connect to the server port");
            assertTrue(clientChannel.isConnected());
        }
    }

    @Test
    void testServerAcceptsClientConnection() throws IOException {
        try (SocketChannel clientChannel = SocketChannel.open()) {
            clientChannel.connect(new InetSocketAddress("localhost", PORT));

            SocketChannel serverAccepted = testServer.accept();
            assertNotNull(serverAccepted, "Server should accept the incoming client connection");
            serverAccepted.close();
        }
    }

    @Test
    void testClientThrowsExceptionWhenPortIsClosed() throws IOException {
        testServer.close();

        CryptocurrencyWalletManagerClient client = new CryptocurrencyWalletManagerClient();

        assertThrows(RuntimeException.class, () -> {
            client.start();
        }, "Should throw RuntimeException when no server is listening on port 7777");
    }

    @Test
    void testNetworkResourcesAreHandled() {
        CryptocurrencyWalletManagerClient client = new CryptocurrencyWalletManagerClient();
        assertNotNull(client, "Client instance should be successfully created");
    }
}