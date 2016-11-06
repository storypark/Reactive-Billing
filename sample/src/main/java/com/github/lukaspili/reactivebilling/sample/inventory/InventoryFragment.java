package com.github.lukaspili.reactivebilling.sample.inventory;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.lukaspili.reactivebilling.Purchase;
import com.github.lukaspili.reactivebilling.Purchases;
import com.github.lukaspili.reactivebilling.RxBilling;
import com.github.lukaspili.reactivebilling.sample.R;
import com.github.lukaspili.reactivebilling.sample.TabsAdapter;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lukasz on 06/05/16.
 */
// TODO: 7/11/16 Update sample for new api
public class InventoryFragment extends Fragment implements TabsAdapter.Tab {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private InventoryAdapter adapter = new InventoryAdapter();

    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        refreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment, container, false);
        recyclerView = (RecyclerView) refreshLayout.findViewById(R.id.recyclerview);

        return refreshLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        adapter.bind(new InventoryAdapter.DidClickItem() {
            @Override
            public void onClick(final Purchase purchase) {
                dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Consume item")
                        .setMessage(String.format("Do you want to consume the %s?", "TODO"))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                consume(purchase);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(true)
                        .show();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        load();
    }

    @Override
    public void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        super.onDestroy();
    }

    private void load() {
        Log.d(getClass().getName(), "Load inventory");

        RxBilling.purchases(getContext(), RxBilling.PURCHASE_TYPE_MANAGED_PRODUCT, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Purchases>() {
                    @Override
                    public void call(Purchases purchases) {
                        if (getActivity() == null) return;
                        refreshLayout.setRefreshing(false);
                        didSucceedGetPurchases(purchases);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (getActivity() == null) return;
                        refreshLayout.setRefreshing(false);
                        didFailGetPurchases();
                    }
                });
    }

    private void didSucceedGetPurchases(Purchases purchases) {

//        Observable.from(purchases.)
//                .map(new Func1<String, Purchase>() {
//                    @Override
//                    public Purchase call(GetPurchasesResponse.PurchaseResponse purchaseResponse) {
//                        return purchaseResponse.getPurchase();
//                    }
//                })
//                .toList()
//                .subscribe(new Action1<List<Purchase>>() {
//                    @Override
//                    public void call(List<Purchase> purchases) {
//                        adapter.bind(purchases);
//                    }
//                });
    }

    private void didFailGetPurchases() {

    }


    // Consume

    private void consume(Purchase purchase) {
//        RxBilling.consumePurchase(getContext(), purchase.getPurchaseToken())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Response>() {
//                    @Override
//                    public void call(Response response) {
//                        didSucceedConsumePurchase(response);
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        didFailConsumePurchase();
//                    }
//                });
    }

    private void didSucceedConsumePurchase() {
        // reload the list once the product is consumed
        load();

        String title;
        String message;
//        if (response.isSuccess()) {
            title = "Product consumed";
            message = "Hope you enjoyed it";
//        } else {
//            title = "Failed to consume";
//            message = Utils.getMessage(response.getResponseCode());
//        }

        dialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }

    private void didFailConsumePurchase() {

    }

    @Override
    public void didFocus() {
        Log.d(getClass().getName(), "Inventory did focus");
        load();
    }
}
