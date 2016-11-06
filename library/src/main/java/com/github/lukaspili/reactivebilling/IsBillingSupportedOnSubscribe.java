package com.github.lukaspili.reactivebilling;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import rx.Observer;

/*package*/ final class IsBillingSupportedOnSubscribe extends BaseObservable<Boolean> {

    private final String purchaseType;

    /*package*/ IsBillingSupportedOnSubscribe(@NonNull Context context, @NonNull @RxBilling.PurchaseType String purchaseType) {
        super(context);
        this.purchaseType = purchaseType;
    }

    @Override
    protected void onBillingServiceReady(@NonNull BillingService billingService, @NonNull Observer<? super Boolean> observer) {
        try {
            observer.onNext(billingService.isBillingSupported(purchaseType));
            observer.onCompleted();
        } catch (RemoteException e) {
            observer.onError(e);
        }
    }

}
