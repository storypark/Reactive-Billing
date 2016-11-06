package com.github.lukaspili.reactivebilling;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import rx.Observable;
import rx.functions.Action0;
import rx.subjects.PublishSubject;

/**
 * Created by lukasz on 06/05/16.
 */
/*package*/ final class PurchaseFlowService {

    private final Context context;
    private final PublishSubject<Purchase> purchaseSubject = PublishSubject.create();

    private boolean hasSubscription;
    private final Observable<Purchase> purchaseObservable =
            purchaseSubject.doOnSubscribe(new Action0() {
                @Override
                public void call() {
                    if (hasSubscription) {
                        throw new IllegalStateException("Already has subscription");
                    }

                    RxBillingLogger.v("Purchase flow - subscribe (thread %s)", Thread.currentThread().getName());
                    hasSubscription = true;
                }
            }).doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
                    if (!hasSubscription) {
                        throw new IllegalStateException("Doesn't have any subscription");
                    }

                    RxBillingLogger.v("Purchase flow - unsubscribe (thread %s)", Thread.currentThread().getName());
                    hasSubscription = false;
                }
            });

    /*package*/ PurchaseFlowService(Context context) {
        this.context = context;
    }

    @NonNull @CheckResult
    /*package*/ Observable<Purchase> asObservable() {
        return purchaseObservable;
    }

    /*package*/ void startFlow(PendingIntent buyIntent, Bundle extras) {
        if (!hasSubscription) {
            throw new IllegalStateException("Cannot start flow without subscribers");
        }

        RxBillingLogger.v("Start flow (thread %s)", Thread.currentThread().getName());

        Intent intent = new Intent(context, RxBillingShadowActivity.class);
        intent.putExtra("BUY_INTENT", buyIntent);

        if (extras != null) {
            intent.putExtra("BUY_EXTRAS", extras);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /*package*/ void onActivityResult(int resultCode, Intent data, Bundle extras) {
        if (!hasSubscription) {
            throw new IllegalStateException("Subject cannot be unsubscribed when receiving purchase result");
        }

        if (resultCode == Activity.RESULT_OK) {
            RxBillingLogger.v("Purchase flow result - OK (thread %s)", Thread.currentThread().getName());

            final int response = data.getIntExtra("RESPONSE_CODE", -1);
            RxBillingLogger.v("Purchase flow result - response: %d (thread %s)", response, Thread.currentThread().getName());

            if (response == 0) {
                final String purchase = data.getStringExtra("INAPP_PURCHASE_DATA");
                final String signature = data.getStringExtra("INAPP_DATA_SIGNATURE");
                purchaseSubject.onNext(new Purchase(response, purchase, signature, extras));
            } else {
                purchaseSubject.onNext(new Purchase(response, null, null, extras));
            }
        } else {
            RxBillingLogger.v("Purchase flow result - CANCELED (thread %s)", Thread.currentThread().getName());
            purchaseSubject.onNext(new Purchase(-1, null, null, extras));
        }
    }

}
