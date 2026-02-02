package model;

import com.google.gson.annotations.SerializedName;

public record Asset(@SerializedName("asset_id")
                    String id,
                    String name,
                    @SerializedName("type_is_crypto")
                    int isCrypto,
                    @SerializedName("data_symbols_count")
                    long data_symbols_count,
                    @SerializedName("volume_1day_usd")
                    double volume1DayUsd,
                    @SerializedName("price_usd")
                    double price) {
}
