package com.github.lukaspili.reactivebilling;

import android.support.annotation.Nullable;

import java.util.List;

public final class SkuDetails extends BaseResponse {
    public final List<String> skuDetails;

    /*package*/ SkuDetails(int responseCode, @Nullable List<String> skuDetails) {
        super(responseCode);
        this.skuDetails = skuDetails;
    }
}
