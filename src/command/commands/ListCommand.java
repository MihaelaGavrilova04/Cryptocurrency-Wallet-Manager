package command.commands;

import api.AssetCache;
import model.Asset;

import java.util.List;

public final class ListCommand implements PublicCommand {
    private static final String LIST_OFFERINGS_MESSAGE = "All the latest offerings:" + System.lineSeparator();
    private final AssetCache assetCache;

    public ListCommand(AssetCache assetCache) {

        validateObjectConstruction(assetCache);

        this.assetCache = assetCache;
    }

    @Override
    public String execute() {

        List<Asset> cached =  assetCache.getCachedValues();

        if (cached == null || cached.isEmpty()) {
            return "No information available!";
        }

        StringBuilder listOfferings = new StringBuilder();
        listOfferings.append(LIST_OFFERINGS_MESSAGE);

        for (Asset each : cached) {
            listOfferings.append(each.toString());
        }

        return listOfferings.toString();
    }

    private static void validateObjectConstruction(AssetCache assetCache) {
        if (assetCache == null) {
            throw new IllegalArgumentException("AssetCache is null");
        }
    }
}
