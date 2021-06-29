package com.example.uit_learning.Common;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.example.uit_learning.R;

public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.getNetworkState(context);
        Dialog dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        dialog.setContentView(R.layout.check_internet_dialog);

        AppCompatButton btnRetry = dialog.findViewById(R.id.btnRetry);


        //retry click
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReceive(context, intent);
                dialog.dismiss();

            }
        });
        if (status.isEmpty() || status.equals("No internet")) {
            dialog.show();
        }


    }
}
