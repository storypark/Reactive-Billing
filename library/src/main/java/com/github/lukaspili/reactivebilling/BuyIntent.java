package com.github.lukaspili.reactivebilling;

import android.app.PendingIntent;
import android.support.annotation.Nullable;

public final class BuyIntent extends BaseResponse {
    public final PendingIntent buyIntent;

    /*package*/ BuyIntent(int responseCode, @Nullable PendingIntent buyIntent) {
        super(responseCode);
        this.buyIntent = buyIntent;
    }
}
