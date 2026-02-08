package server;

import api.ApiCall;
import api.AssetCache;
import command.CommandFactory;
import repository.UserRepository;

import java.net.http.HttpClient;

public class CryptoServerMain {
    public static void main(String[] args) {

        ApiCall apiCall = new ApiCall("dummy", HttpClient.newBuilder().build());

        try (AssetCache cache = new AssetCache(apiCall)) {
            UserRepository repo = new UserRepository("myDatabaseFile");

            CryptocurrencyWalletManagerServer server = new CryptocurrencyWalletManagerServer(new CommandFactory(repo, cache));
            server.start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
