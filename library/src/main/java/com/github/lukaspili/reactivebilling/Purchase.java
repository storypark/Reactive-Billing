package com.github.lukaspili.reactivebilling;

import android.os.Bundle;
import android.support.annotation.Nullable;

public final class Purchase {
    public final int responseCode;
    public final String purchase;
    public final String signature;
    public final Bundle extras;

    public Purchase(int responseCode, @Nullable String purchase, @Nullable String signature, @Nullable Bundle extras) {
        this.responseCode = responseCode;
        this.purchase = purchase;
        this.signature = signature;
        this.extras = extras;
    }
}
