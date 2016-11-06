package com.github.lukaspili.reactivebilling;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.android.vending.billing.IInAppBillingService;

import java.util.concurrent.Semaphore;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/*package*/ abstract class BaseObservable<T> implements Observable.OnSubscribe<T> {

    protected final Context context;
    private final Semaphore semaphore = new Semaphore(0);

    private BillingService billingService;

    /*package*/ BaseObservable(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        final Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");

        // don't bother with semaphores in the originating thread is the main thread
        final boolean useSemaphore = Looper.myLooper() != Looper.getMainLooper();
        final Connection connection = new Connection(subscriber, useSemaphore);

        RxBillingLogger.v("Bind service (thread %s)", Thread.currentThread().getName());
        try {
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        } catch (SecurityException e) {
            RxBillingLogger.e(e, "Bind service error");
            subscriber.onError(e);
        }

        subscriber.add(Subscriptions.create(new Action0() {
            @Override
            public void call() {
                RxBillingLogger.v("Unbind service (thread %s)", Thread.currentThread().getName());
                context.unbindService(connection);
            }
        }));

        if (useSemaphore) {
            // freeze the current RX thread until service is connected
            // because bindService() will call the connection callback on the main thread
            // we want to get back on the current RX thread
            RxBillingLogger.v("Acquire semaphore until service is ready (thread %s)", Thread.currentThread().getName());
            semaphore.acquireUninterruptibly();

            // once the semaphore is released
            // it means that the service is connected and available
            //TODO: what happens if the service is never connected?
            deliverBillingService(subscriber);
        }
    }

    private void deliverBillingService(Observer observer) {
        RxBillingLogger.v("Billing service ready (thread %s)", Thread.currentThread().getName());
        onBillingServiceReady(billingService, observer);
    }

    protected abstract void onBillingServiceReady(@NonNull BillingService billingService, @NonNull Observer<? super T> observer);

    private class Connection implements ServiceConnection {

        private final Observer observer;
        private final boolean useSemaphore;

        public Connection(Observer observer, boolean useSemaphore) {
            this.observer = observer;
            this.useSemaphore = useSemaphore;
        }

        /**
         * For some reason, that method is always called on the main thread
         * Regardless of the originating thread executing bindService()
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RxBillingLogger.v("Service connected (thread %s)", Thread.currentThread().getName());

            IInAppBillingService inAppBillingService = IInAppBillingService.Stub.asInterface(service);
            billingService = new BillingService(context, inAppBillingService);

            if (useSemaphore) {
                // once the service is available, release the semaphore
                // that is blocking the originating thread
                RxBillingLogger.v("Release semaphore (thread %s)", Thread.currentThread().getName());
                semaphore.release();
            } else {
                deliverBillingService(observer);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            RxBillingLogger.v("Service disconnected (thread %s)", Thread.currentThread().getName());
            billingService = null;
        }
    }

}
