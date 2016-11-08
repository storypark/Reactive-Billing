package com.github.lukaspili.reactivebilling;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observer;

/*package*/ final class StartPurchaseOnSubscribe extends BaseObservable<Integer> {

    private final PurchaseFlowService purchaseFlowService;
    private final String productId;
    private final String purchaseType;
    private final String developerPayload;
    private final Bundle extras;

    /*package*/ StartPurchaseOnSubscribe(@NonNull Context context, @NonNull PurchaseFlowService purchaseFlowService, @NonNull String productId, @NonNull @RxBilling.PurchaseType String purchaseType, @Nullable String developerPayload, @Nullable Bundle extras) {
        super(context);
        this.purchaseFlowService = purchaseFlowService;
        this.productId = productId;
        this.purchaseType = purchaseType;
        this.developerPayload = developerPayload;
        this.extras = extras;
    }

    @Override
    protected void onBillingServiceReady(@NonNull BillingService billingService, @NonNull Observer<? super Integer> observer) {
        try {
            final BuyIntent buyIntent = billingService.getBuyIntent(productId, purchaseType, developerPayload);
            if (buyIntent != null) {
                // TODO: Should this throw an onError or continue to onNext the response code?
                observer.onNext(buyIntent.responseCode);
                observer.onCompleted();
                if (buyIntent.buyIntent != null) {
                    purchaseFlowService.startFlow(buyIntent.buyIntent, extras);
                }
            } else {
                observer.onError(new BillingRequestFailedException(
                        "Failed to start purchase (" +
                                "purchaseFlowService: " + purchaseFlowService +
                                ", productId: " + productId +
                                ", purchaseType: " + purchaseType +
                                ", developerPayload: " + developerPayload +
                                ", extras: " + extras +
                                ')'));
            }
        } catch (RemoteException e) {
            observer.onError(e);
        }
    }

}
