/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.lukaspili.reactivebilling;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.annotation.StringDef;

import java.util.List;

import rx.Observable;

public final class RxBilling {

    @NonNull @CheckResult @MainThread
    public static Observable<Boolean> isBillingSupported(@NonNull Context context, @NonNull @PurchaseType String purchaseType) {
        return Observable.create(new IsBillingSupportedOnSubscribe(context, purchaseType));
    }

    @NonNull @CheckResult @MainThread
    public static Observable<Void> consumePurchase(@NonNull Context context, @NonNull String purchaseToken) {
        return Observable.create(new ConsumePurchaseOnSubscribe(context, purchaseToken));
    }

    @NonNull @CheckResult @MainThread
    public static Observable<List<String>> skuDetails(@NonNull Context context, @NonNull @PurchaseType String purchaseType, @NonNull @Size(min = 1) String... productIds) {
        return Observable.create(new SkuDetailsOnSubscribe(context, purchaseType, productIds));
    }

    @NonNull @CheckResult @MainThread
    public static Observable<Purchases> purchases(@NonNull Context context, @NonNull @PurchaseType String purchaseType, @Nullable String continuationToken) {
        return Observable.create(new PurchasesOnSubscribe(context, purchaseType, continuationToken));
    }

    @NonNull @CheckResult @MainThread
    public static Observable<Void> startPurchase(@NonNull Context context, @NonNull String productId, @NonNull @PurchaseType String purchaseType, @Nullable String developerPayload, @Nullable Bundle extras) {
        return Observable.create(new StartPurchaseOnSubscribe(context, getPurchaseFlowService(context), productId, purchaseType, developerPayload, extras));
    }

    @NonNull @CheckResult @MainThread
    public static Observable<Purchase> purchaseFlow(@NonNull Context context) {
        return getPurchaseFlowService(context).asObservable();
    }

    public static void setLogger(@NonNull RxBilling.Logger logger) {
        RxBillingLogger.setLogger(logger);
    }

    public static void setPackageName(@NonNull Context context, @NonNull String packageName) {
        RxBilling.get(context).packageName = packageName;
    }

    public static final String PURCHASE_TYPE_MANAGED_PRODUCT = "inapp";
    public static final String PURCHASE_TYPE_SUBSCRIPTION = "subs";
    @StringDef({
            PURCHASE_TYPE_MANAGED_PRODUCT, PURCHASE_TYPE_SUBSCRIPTION
    })
    public @interface PurchaseType {}

    @NonNull @CheckResult @MainThread
    /*package*/ static PurchaseFlowService getPurchaseFlowService(@NonNull Context context) {
        return RxBilling.get(context).purchaseFlowService;
    }

    @NonNull @CheckResult @MainThread
    /*package*/ static String getPackageName(@NonNull Context context) {
        return RxBilling.get(context).packageName;
    }

    private static RxBilling instance;

    private final PurchaseFlowService purchaseFlowService;
    private String packageName;

    @MainThread
    private static RxBilling get(@NonNull Context context) {
        if (instance == null) {
            instance = new RxBilling(context.getApplicationContext(),
                    new PurchaseFlowService(context.getApplicationContext()));
        }
        return instance;
    }

    private RxBilling(@NonNull Context context, @NonNull PurchaseFlowService purchaseFlowService) {
        this.purchaseFlowService = purchaseFlowService;
        this.packageName = context.getPackageName();
    }

    public interface Logger {
        void log(int priority, String message, Throwable t);
    }

}
