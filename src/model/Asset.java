package model;

import com.google.gson.annotations.SerializedName;
import exception.CoinAPIException;

public record Asset(@SerializedName("asset_id")
                    String id,
                    String name,
                    @SerializedName("type_is_crypto")
                    Integer isCrypto,
                    @SerializedName("price_usd")
                    Double price) {

    public Asset {
        if (isCrypto == null) {
            throw new CoinAPIException("API documentation states that 'type_is_crypto' is not nullable!");
        }
    }

    private static final int IS_CRYPTO = 1;

    public boolean isCryptoAsset() {
        return isCrypto == IS_CRYPTO;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) — $%.2f %n", name, id, (price != null ? price : 0.0));
    }
}
