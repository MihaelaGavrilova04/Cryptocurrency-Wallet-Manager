package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class CryptocurrencyWalletManagerClient {
    private static final String HOST = "localhost";
    private static final int PORT = 7777;

    private static final String STOP_WORD = "STOP";

    private static final String ENCODING = "UTF-8";
    private static final String EXIT_MESSAGE = "exit";

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader bufferedReader = new BufferedReader(Channels.newReader(socketChannel, ENCODING));
             PrintWriter printWriter = new PrintWriter(Channels.newWriter(socketChannel, ENCODING), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(HOST, PORT));

            while (true) {
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase(EXIT_MESSAGE)) {
                    break;
                }

                printWriter.println(input);

                String reply;
                while ((reply = bufferedReader.readLine()) != null) {
                    if (reply.equals(STOP_WORD)) {
                        break;
                    }
                    System.out.println(reply);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Some Network error occurred!", e);
        }
    }
}
