package server;

import api.ApiCall;
import api.AssetCache;
import command.CommandFactory;
import repository.UserRepository;

import java.net.http.HttpClient;
import java.util.Scanner;

public class CryptoServerMain {
    public static void main(String[] args) {
        String apiKey = readApiKey();
        HttpClient httpClient = HttpClient.newBuilder().build();
        ApiCall apiCall = new ApiCall(apiKey, httpClient);
        UserRepository repo = new UserRepository("myDatabaseFile");

        try (AssetCache cache = new AssetCache(apiCall)) {
            CommandFactory factory = new CommandFactory(repo, cache);
            CryptocurrencyWalletManagerServer server = new CryptocurrencyWalletManagerServer(factory);
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String readApiKey() {
        System.out.println("Please enter your API key:");
        try (Scanner scanner = new Scanner(System.in)) {
            return scanner.nextLine();
        }
    }
}