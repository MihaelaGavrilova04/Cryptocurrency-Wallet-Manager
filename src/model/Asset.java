package model;

import com.google.gson.annotations.SerializedName;

public record Asset(@SerializedName("asset_id")
                    String id,
                    String name,
                    @SerializedName("type_is_crypto")
                    int isCrypto,
                    @SerializedName("price_usd")
                    Double price) {

    private static final int IS_CRYPTO = 1;

    public boolean isCryptoAsset() {
        return isCrypto == IS_CRYPTO;
    }
}
