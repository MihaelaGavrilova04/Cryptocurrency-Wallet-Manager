import api.ApiCall;
import api.AssetCache;
import model.Asset;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter apiKey");
            String apikey = scanner.nextLine();
            ApiCall call = new ApiCall(apikey, HttpClient.newBuilder().build());

            AssetCache cache = new AssetCache(call);

            List<Asset> fetched = cache.getCachedValues();
            System.out.println("hello");
            if (fetched.isEmpty()) {
                System.out.println("List is empty!");
            }
            for (Asset asset : fetched) {
                System.out.println(asset.id());
                System.out.println(asset.isCrypto());
            }

        }
    }
}