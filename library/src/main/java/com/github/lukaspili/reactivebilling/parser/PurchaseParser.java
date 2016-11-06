package com.github.lukaspili.reactivebilling.parser;

import com.github.lukaspili.reactivebilling.RxBillingLogger;
import com.github.lukaspili.reactivebilling.model.Purchase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lukasz on 06/05/16.
 */
public class PurchaseParser {

    public static Purchase parse(String value) {
//        RxBillingLogger.v("purchase json: %s", value);

        JSONObject json;
        try {
            json = new JSONObject(value);
        } catch (JSONException e) {
            RxBillingLogger.e(e, "Cannot parse purchase json");
            return null;
        }

        return new Purchase(
                json.optString("orderId"),
                json.optString("packageName"),
                json.optString("productId"),
                json.optString("developerPayload"),
                json.optString("purchaseToken"),
                PurchaseStateParser.parse(json.optInt("purchaseState")),
                json.optLong("purchaseTime"),
                json.optBoolean("autoRenewing")
        );
    }
}
