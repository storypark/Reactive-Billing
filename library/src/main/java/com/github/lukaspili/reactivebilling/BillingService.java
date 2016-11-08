package com.github.lukaspili.reactivebilling;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.text.TextUtils;

import com.android.vending.billing.IInAppBillingService;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lukasz on 04/05/16.
 */
/*package*/ final class BillingService {

    private static final int API_VERSION = 3;

    private final String packageName;
    private final IInAppBillingService billingService;

    /*package*/ BillingService(@NonNull Context context, @NonNull IInAppBillingService billingService) {
        this.packageName = RxBilling.getPackageName(context);
        this.billingService = billingService;
    }

    /*package*/ boolean isBillingSupported(@NonNull @RxBilling.PurchaseType String purchaseType) throws RemoteException {
        RxBillingLogger.v("Is billing supported - request (thread %s)", Thread.currentThread().getName());

        final int response = billingService.isBillingSupported(BillingService.API_VERSION, packageName, purchaseType);
        RxBillingLogger.v("Is billing supported - response: %d", response);
        return response == 0;
    }

    /*package*/ boolean consumePurchase(String purchaseToken) throws RemoteException {
        RxBillingLogger.v("Consume purchase - request (thread %s)", Thread.currentThread().getName());

        final int response = billingService.consumePurchase(BillingService.API_VERSION, packageName, purchaseToken);
        RxBillingLogger.v("Consume purchase - response: %d", response);
        return response == 0;
    }

    @NonNull
    /*package*/ Purchases getPurchases(@NonNull @RxBilling.PurchaseType String purchaseType, @Nullable String continuationToken) throws RemoteException {
        RxBillingLogger.v("Get purchases - request (thread %s)", Thread.currentThread().getName());

        final Bundle bundle = billingService.getPurchases(BillingService.API_VERSION, packageName, purchaseType, continuationToken);
        final int response = bundle.getInt("RESPONSE_CODE", -1);

        RxBillingLogger.v("Get purchases - response code: %s", response);
        if (bundle == null) {
            return new Purchases(response, null, null, null, null);
        }

        return new Purchases(
                response,
                bundle.getStringArrayList("INAPP_PURCHASE_ITEM_LIST"),
                bundle.getStringArrayList("INAPP_PURCHASE_DATA_LIST"),
                bundle.getStringArrayList("INAPP_DATA_SIGNATURE_LIST"),
                bundle.getString("INAPP_CONTINUATION_TOKEN"));
    }

    @NonNull @SuppressWarnings("ConstantConditions")
    /*package*/ SkuDetails getSkuDetails(@NonNull @RxBilling.PurchaseType String purchaseType, @NonNull @Size(min = 1) String... productIds) throws RemoteException {
        if (productIds == null || productIds.length == 0) {
            throw new IllegalArgumentException("Product ids cannot be blank");
        }

        RxBillingLogger.v("Get sku details - request: %s (thread %s)", TextUtils.join(", ", productIds), Thread.currentThread().getName());

        final Bundle requestBundle = new Bundle();
        requestBundle.putStringArrayList("ITEM_ID_LIST", new ArrayList<>(Arrays.asList(productIds)));

        final Bundle resultBundle = billingService.getSkuDetails(BillingService.API_VERSION, packageName, purchaseType, requestBundle);
        final int response = resultBundle.getInt("RESPONSE_CODE", -1);

        RxBillingLogger.v("Get sku details - response code: %s", response);
        if (resultBundle == null) {
            return new SkuDetails(response, null);
        }

        return new SkuDetails(response, resultBundle.getStringArrayList("DETAILS_LIST"));
    }

    @NonNull
    /*package*/ BuyIntent getBuyIntent(@NonNull String productId, @NonNull @RxBilling.PurchaseType String purchaseType, @Nullable String developerPayload) throws RemoteException {
        RxBillingLogger.v("Get buy intent - request: %s (thread %s)", productId, Thread.currentThread().getName());

        final Bundle bundle = billingService.getBuyIntent(BillingService.API_VERSION, packageName, productId, purchaseType, developerPayload);
        final int response = bundle.getInt("RESPONSE_CODE", -1);

        RxBillingLogger.v("Get buy intent - response code: %s", response);
        if (bundle == null) {
            return new BuyIntent(response, null);
        }

        return new BuyIntent(response, (PendingIntent) bundle.getParcelable("BUY_INTENT"));
    }

}
