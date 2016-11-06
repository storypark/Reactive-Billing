package com.github.lukaspili.reactivebilling;

import android.support.annotation.Nullable;

import java.util.List;

public final class Purchases {
    public final List<String> productIds;
    public final List<String> purchaseData;
    public final List<String> dataSignatures;
    public final String continuationToken;

    public Purchases(@Nullable List<String> productIds, @Nullable List<String> purchaseData, @Nullable List<String> dataSignatures, @Nullable String continuationToken) {
        this.productIds = productIds;
        this.purchaseData = purchaseData;
        this.dataSignatures = dataSignatures;
        this.continuationToken = continuationToken;
    }
}
