package com.github.lukaspili.reactivebilling.sample.shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lukaspili.reactivebilling.sample.R;

/**
 * Created by lukasz on 06/05/16.
 */
// TODO: 7/11/16 Update sample to new api
public class ShopRowView extends LinearLayout {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView priceTextView;

    public ShopRowView(Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.row_shop, this);
        titleTextView = (TextView) view.findViewById(R.id.title);
        descriptionTextView = (TextView) view.findViewById(R.id.description);
        priceTextView = (TextView) view.findViewById(R.id.price);
    }

    public void bind(String string) {
        titleTextView.setText(string);
        descriptionTextView.setText(string);
        priceTextView.setText(string);
    }
}
