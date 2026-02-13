package server;

import command.CommandFactory;
import command.commands.Command;
import exception.InvalidCommandException;
import exception.UnauthenticatedException;
import exception.UserAlreadyLoggedInException;
import logger.Logger;
import server.session.ClientContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class CryptocurrencyWalletManagerServer {

    private static final String HOST = "0.0.0.0";
    private static final int PORT = 7777;

    private static final String STOP_WORD = "STOP";

    private final CommandFactory commandFactory;

    private Selector selector;
    private boolean isServerWorking;

    private static final Logger LOGGER = Logger.getInstance();

    public CryptocurrencyWalletManagerServer(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {

            this.selector = selector;

            configureServerSocketChannel(serverSocketChannel, selector);
            isServerWorking = true;

            while (isServerWorking) {
                handleReadyChannels(selector);
            }

        } catch (IOException e) {
            LOGGER.log(e, "Problem with server occurred!");
            throw new RuntimeException(e);
        }
    }

    private void handleReadyChannels(Selector selector) throws IOException {
        int readyChannels = selector.select();

        if (readyChannels == 0) {
            return;
        }

        Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();

        while (selectionKeyIterator.hasNext()) {
            SelectionKey key = selectionKeyIterator.next();
            selectionKeyIterator.remove();

            if (!key.isValid()) {
                continue;
            }

            if (key.isAcceptable()) {
                acceptClient(key, selector);
            } else if (key.isReadable()) {
                handleClient(key, this.commandFactory);
            }
        }
    }

    public void stop() {
        isServerWorking = false;
        if (selector != null && selector.isOpen()) {
            selector.wakeup();
        }
    }

    private static void acceptClient(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();

        if (clientChannel == null) {
            return;
        }

        clientChannel.configureBlocking(false);

        clientChannel.register(selector, SelectionKey.OP_READ, new ClientContext());
    }

    private static void handleClient(SelectionKey key, CommandFactory commandFactory) throws IOException {

        SocketChannel clientChannel = (SocketChannel) key.channel();
        ClientContext context = (ClientContext) key.attachment();

        ByteBuffer clientsBuffer = context.getBuffer();
        String clientRequest = getClientRequest(key, clientChannel, clientsBuffer);

        if (clientRequest == null) {
            return;
        }

        try {
            Command commandToExecute = commandFactory.getCommand(clientRequest, context);
            String updateMessageToClient = commandToExecute.execute(commandFactory.getAssetCache());
            putUpdateMessageToClientBuffer(clientsBuffer, updateMessageToClient);
        } catch (InvalidCommandException e) {
            putUpdateMessageToClientBuffer(clientsBuffer, e.getMessage());
        } catch (UnauthenticatedException e) {
            putUpdateMessageToClientBuffer(clientsBuffer, e.getMessage());
        } catch (UserAlreadyLoggedInException e) {
            putUpdateMessageToClientBuffer(clientsBuffer, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(e, "Some server error occurred");
            putUpdateMessageToClientBuffer(clientsBuffer, e.toString());
        }

        clientChannel.write(clientsBuffer);
    }

    private static void putUpdateMessageToClientBuffer(ByteBuffer clientsBuffer, String updateMessageToClient) {
        updateMessageToClient += System.lineSeparator() + STOP_WORD + System.lineSeparator();

        clientsBuffer.clear();
        clientsBuffer.put(updateMessageToClient.getBytes(StandardCharsets.UTF_8));
        clientsBuffer.flip();
    }

    private static String getClientRequest(SelectionKey key, SocketChannel clientChannel,
                                           ByteBuffer clientsBuffer) throws IOException {

        clientsBuffer.clear();
        int readBytesFromClient = clientChannel.read(clientsBuffer);

        if (readBytesFromClient == -1) {
            clientChannel.close();
            key.cancel();
            return null;
        }

        return obtainMessageFromClient(clientsBuffer);
    }

    private static String obtainMessageFromClient(ByteBuffer clientsBuffer) {
        clientsBuffer.flip();
        byte[] bytes = new byte[clientsBuffer.remaining()];
        clientsBuffer.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8).strip();
    }

    private static void configureServerSocketChannel(ServerSocketChannel serverSocketChannel,
                                                     Selector selector) throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(HOST, PORT));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
}
