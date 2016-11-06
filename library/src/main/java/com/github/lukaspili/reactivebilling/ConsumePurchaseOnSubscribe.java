package com.github.lukaspili.reactivebilling;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import rx.Observer;

/*package*/ final class ConsumePurchaseOnSubscribe extends BaseObservable<Void> {

    private final String purchaseToken;

    /*package*/ ConsumePurchaseOnSubscribe(@NonNull Context context, @NonNull String purchaseToken) {
        super(context);
        this.purchaseToken = purchaseToken;
    }

    @Override
    protected void onBillingServiceReady(@NonNull BillingService billingService, @NonNull Observer<? super Void> observer) {
        try {
            if (billingService.consumePurchase(purchaseToken)) {
                observer.onNext(null);
                observer.onCompleted();
            } else {
                observer.onError(new BillingRequestFailedException("Failed to consume purchase (token: " + purchaseToken + ')'));
            }
        } catch (RemoteException e) {
            observer.onError(e);
        }
    }

}
