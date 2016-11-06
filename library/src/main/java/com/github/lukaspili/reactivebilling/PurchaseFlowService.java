package com.github.lukaspili.reactivebilling;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.github.lukaspili.reactivebilling.model.Purchase;
import com.github.lukaspili.reactivebilling.parser.PurchaseParser;
import com.github.lukaspili.reactivebilling.response.PurchaseResponse;

import rx.Observable;
import rx.functions.Action0;
import rx.subjects.PublishSubject;

/**
 * Created by lukasz on 06/05/16.
 */
public final class PurchaseFlowService {

    private final Context context;
    private final PublishSubject<PurchaseResponse> subject = PublishSubject.create();
    private final Observable<PurchaseResponse> observable = subject.doOnSubscribe(new Action0() {
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

    private boolean hasSubscription;

    PurchaseFlowService(Context context) {
        this.context = context;
    }

    Observable<PurchaseResponse> getObservable() {
        return observable;
    }

    public void startFlow(PendingIntent buyIntent, Bundle extras) {
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

    void onActivityResult(int resultCode, Intent data, Bundle extras) {
        if (!hasSubscription) {
            throw new IllegalStateException("Subject cannot be null when receiving purchase result");
        }

        if (resultCode == Activity.RESULT_OK) {
            RxBillingLogger.v("Purchase flow result - OK (thread %s)", Thread.currentThread().getName());

            int response = data.getIntExtra("RESPONSE_CODE", -1);
            RxBillingLogger.v("Purchase flow result - response: %d (thread %s)", response, Thread.currentThread().getName());

            if (response == 0) {
                Purchase purchase = PurchaseParser.parse(data.getStringExtra("INAPP_PURCHASE_DATA"));
                String signature = data.getStringExtra("INAPP_DATA_SIGNATURE");
                subject.onNext(new PurchaseResponse(response, purchase, signature, extras, false));
            } else {
                subject.onNext(new PurchaseResponse(response, null, null, extras, false));
            }
        } else {
            RxBillingLogger.v("Purchase flow result - CANCELED (thread %s)", Thread.currentThread().getName());
            subject.onNext(new PurchaseResponse(-1, null, null, extras, true));
        }
    }
}
