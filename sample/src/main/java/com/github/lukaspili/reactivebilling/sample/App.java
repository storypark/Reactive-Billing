package com.github.lukaspili.reactivebilling.sample;

import android.app.Application;

import com.github.lukaspili.reactivebilling.RxBilling;
import com.github.lukaspili.reactivebilling.RxBillingLogger;

/**
 * Created by lukasz on 08/05/16.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RxBilling.setLogger(RxBillingLogger.DebugLogger.INSTANCE);
    }

}
