package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GsonProvider {

    private GsonProvider() {
    }

    public static Gson getGson() {
        return GsonHolder.INSTANCE;
    }

    private static class GsonHolder {
        static final Gson INSTANCE = new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create();
    }
}