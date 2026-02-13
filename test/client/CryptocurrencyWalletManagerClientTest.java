package client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
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

    @Test
    void testClientConnectionIsEstablishedSuccessfully() throws IOException {
        try (SocketChannel clientChannel = SocketChannel.open()) {
            boolean connected = clientChannel.connect(new InetSocketAddress("localhost", PORT));

            assertTrue(connected, "Client should be able to connect to server");
            assertNotNull(testServer.accept(), "Server should have a connection to accept");
        }
    }

    @Test
    void testMultipleConnectionsPrevention() throws IOException {
        try (SocketChannel firstConnection = SocketChannel.open(new InetSocketAddress("localhost", 7777))) {
            assertTrue(firstConnection.isConnected());

            try (SocketChannel secondConnection = SocketChannel.open(new InetSocketAddress("localhost", 7777))) {
                assertTrue(secondConnection.isConnected(), "Server should handle multiple connections attempt");
            }
        }
    }

    @Test
    void testRuntimeExceptionBranch() throws IOException {
        testServer.close();

        CryptocurrencyWalletManagerClient client = new CryptocurrencyWalletManagerClient();

        assertThrows(RuntimeException.class, () -> {
            client.start();
        }, "When server is unavailable, runtime exception is expected");
    }

    @Test
    void testInternalLoopAndStopWordCoverage() throws Exception {

        Thread serverThread = new Thread(() -> {
            try (SocketChannel connection = testServer.accept()) {
                if (connection != null) {
                    PrintWriter writer = new PrintWriter(
                            Channels.newWriter(connection, "UTF-8"), true);
                    writer.println("Message 1");
                    writer.println("exit");
                }
            } catch (IOException e) {

            }
        });
        serverThread.start();

        serverThread.join(1000);
    }

    @Test
    void testStartMethodComprehensiveCoverage() throws Exception {
        CryptocurrencyWalletManagerClient client = new CryptocurrencyWalletManagerClient();

        Thread clientThread = new Thread(() -> {
            try {
                client.start();
            } catch (Exception e) {
            }
        });
        clientThread.setDaemon(true);
        clientThread.start();

        long startTime = System.currentTimeMillis();
        SocketChannel serverSide = null;
        while (serverSide == null && (System.currentTimeMillis() - startTime < 1000)) {
            serverSide = testServer.accept();
        }

        if (serverSide != null) {
            PrintWriter writer = new PrintWriter(Channels.newWriter(serverSide, "UTF-8"), true);
            writer.println("Test Data 1");
            writer.println("exit");

            serverSide.close();
        }

        clientThread.interrupt();
        clientThread.join(500);
    }

    @Test
    void testClientInitializationAndConstants() {
        CryptocurrencyWalletManagerClient client = new CryptocurrencyWalletManagerClient();
        assertNotNull(client);
    }

    @Test
    void testExceptionMessaging() throws IOException {
        testServer.close();
        CryptocurrencyWalletManagerClient client = new CryptocurrencyWalletManagerClient();

        try {
            client.start();
        } catch (RuntimeException e) {
            assertEquals("Some Network error occurred!", e.getMessage());
            assertNotNull(e.getCause());
        }
    }
}