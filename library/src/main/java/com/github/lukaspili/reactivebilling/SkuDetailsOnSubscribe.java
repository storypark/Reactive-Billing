package com.github.lukaspili.reactivebilling;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import java.util.Arrays;

import rx.Observer;

/*package*/ final class SkuDetailsOnSubscribe extends BaseObservable<SkuDetails> {

    private final String purchaseType;
    private final String[] productIds;

    /*package*/ SkuDetailsOnSubscribe(@NonNull Context context, @NonNull @RxBilling.PurchaseType String purchaseType, @NonNull @Size(min = 1) String... productIds) {
        super(context);
        this.purchaseType = purchaseType;
        this.productIds = productIds;
    }

    @Override
    protected void onBillingServiceReady(@NonNull BillingService billingService, @NonNull Observer<? super SkuDetails> observer) {
        try {
            final SkuDetails skuDetails = billingService.getSkuDetails(purchaseType, productIds);
            if (skuDetails != null) {
                observer.onNext(skuDetails);
                observer.onCompleted();
            } else {
                observer.onError(new BillingRequestFailedException("Failed to get sku details (purchaseType: " + purchaseType + ", productIds: " + Arrays.toString(productIds) + ')'));
            }
        } catch (RemoteException e) {
            observer.onError(e);
        }
    }

}
