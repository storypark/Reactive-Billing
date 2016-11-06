package com.github.lukaspili.reactivebilling.sample.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lukaspili.reactivebilling.Purchase;
import com.github.lukaspili.reactivebilling.sample.R;

import java.text.SimpleDateFormat;

/**
 * Created by lukasz on 06/05/16.
 */
public class InventoryRowView extends LinearLayout {

    private TextView titleTextView;
    private TextView descriptionTextView;

    public InventoryRowView(Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.row_inventory, this);
        titleTextView = (TextView) view.findViewById(R.id.title);
        descriptionTextView = (TextView) view.findViewById(R.id.description);
    }

    public void bind(Purchase purchase, SimpleDateFormat dateFormat) {
        // TODO: 7/11/16 Update sample for new api
        titleTextView.setText("TODO");

        String date = "Bought on: TODO";
        String state = "State: TODO";

        descriptionTextView.setText(String.format("%s\n%s", date, state));
    }
}
