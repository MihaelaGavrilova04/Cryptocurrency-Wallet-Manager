package model;

import com.google.gson.annotations.SerializedName;

public record Asset(@SerializedName("asset_id")
                    String id,
                    String name,
                    @SerializedName("type_is_crypto")
                    int isCrypto,
                    @SerializedName("price_usd")
                    double price) {
}
