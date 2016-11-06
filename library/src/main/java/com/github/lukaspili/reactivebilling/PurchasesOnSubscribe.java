package com.github.lukaspili.reactivebilling;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observer;

/*package*/ final class PurchasesOnSubscribe extends BaseObservable<Purchases> {

    private final String purchaseType;
    private final String continuationToken;

    /*package*/ PurchasesOnSubscribe(@NonNull Context context, @NonNull @RxBilling.PurchaseType String purchaseType, @Nullable String continuationToken) {
        super(context);
        this.purchaseType = purchaseType;
        this.continuationToken = continuationToken;
    }

    @Override
    protected void onBillingServiceReady(@NonNull BillingService billingService, @NonNull Observer<? super Purchases> observer) {
        try {
            final Purchases purchases = billingService.getPurchases(purchaseType, continuationToken);
            if (purchases != null) {
                observer.onNext(purchases);
                observer.onCompleted();
            } else {
                observer.onError(new BillingRequestFailedException("Failed to get purchases (type: " + purchaseType + ", continuationToken: " + continuationToken + ')'));
            }
        } catch (RemoteException e) {
            observer.onError(e);
        }
    }

}
