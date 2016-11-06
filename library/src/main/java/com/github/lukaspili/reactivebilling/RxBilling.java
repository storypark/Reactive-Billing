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

import com.github.lukaspili.reactivebilling.model.PurchaseType;
import com.github.lukaspili.reactivebilling.observable.BillingServiceObservable;
import com.github.lukaspili.reactivebilling.observable.ConsumePurchaseObservable;
import com.github.lukaspili.reactivebilling.observable.GetBuyIntentObservable;
import com.github.lukaspili.reactivebilling.observable.GetPurchasesObservable;
import com.github.lukaspili.reactivebilling.observable.GetSkuDetailsObservable;
import com.github.lukaspili.reactivebilling.observable.IsBillingSupportedObservable;
import com.github.lukaspili.reactivebilling.response.GetPurchasesResponse;
import com.github.lukaspili.reactivebilling.response.GetSkuDetailsResponse;
import com.github.lukaspili.reactivebilling.response.PurchaseResponse;
import com.github.lukaspili.reactivebilling.response.Response;

import rx.Observable;

public final class RxBilling {

    @NonNull @CheckResult @MainThread
    public static Observable<BillingService> billingService(@NonNull Context context) {
        return BillingServiceObservable.create(context);
    }

    @NonNull @CheckResult @MainThread
    public static Observable<Response> isBillingSupported(@NonNull Context context, @NonNull PurchaseType purchaseType) {
        return IsBillingSupportedObservable.create(context, purchaseType);
    }

    @NonNull @CheckResult @MainThread
    public static Observable<Response> consumePurchase(@NonNull Context context, String purchaseToken) {
        return ConsumePurchaseObservable.create(context, purchaseToken);
    }

    @NonNull @CheckResult @MainThread
    public static Observable<GetSkuDetailsResponse> skuDetails(@NonNull Context context, @NonNull PurchaseType purchaseType, @Nullable String... productIds) {
        return GetSkuDetailsObservable.create(context, purchaseType, productIds);
    }

    @NonNull @CheckResult @MainThread
    public static Observable<GetPurchasesResponse> purchases(@NonNull Context context, @NonNull PurchaseType purchaseType, @Nullable String continuationToken) {
        return GetPurchasesObservable.create(context, purchaseType, continuationToken);
    }

    @NonNull @CheckResult @MainThread
    public static Observable<Response> startPurchase(@NonNull Context context, @NonNull String productId, @NonNull PurchaseType purchaseType, @Nullable String developerPayload, @Nullable Bundle extras) {
        return GetBuyIntentObservable.create(context, getPurchaseFlowService(context), productId, purchaseType, developerPayload, extras);
    }

    @NonNull @CheckResult @MainThread
    public static Observable<PurchaseResponse> purchaseFlow(@NonNull Context context) {
        return getPurchaseFlowService(context).getObservable();
    }

    public static void setLogger(@NonNull RxBilling.Logger logger) {
        RxBillingLogger.setLogger(logger);
    }

    @NonNull @CheckResult @MainThread
    /*package*/ static PurchaseFlowService getPurchaseFlowService(@NonNull Context context) {
        return RxBilling.get(context).purchaseFlowService;
    }

    private static RxBilling instance;

    private final Context context;
    private final PurchaseFlowService purchaseFlowService;

    @MainThread
    private static RxBilling get(@NonNull Context context) {
        if (instance == null) {
            instance = new RxBilling(
                    context.getApplicationContext(),
                    new PurchaseFlowService(context.getApplicationContext()));
        }
        return instance;
    }

    private RxBilling(@NonNull Context context, @NonNull PurchaseFlowService purchaseFlowService) {
        this.context = context;
        this.purchaseFlowService = purchaseFlowService;
    }

    public interface Logger {
        void log(int priority, String message, Throwable t);
    }

}
