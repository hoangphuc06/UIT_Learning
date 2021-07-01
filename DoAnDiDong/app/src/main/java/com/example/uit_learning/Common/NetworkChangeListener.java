package com.example.uit_learning.Common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.example.uit_learning.R;

public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.getNetworkState(context);
//        Dialog dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
//        dialog.setContentView(R.layout.check_internet_dialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.check_internet_dialog,null);
        builder.setView(view);

        AppCompatButton btnRetry = view.findViewById(R.id.btnRetry);

        final Dialog dialog = builder.create();
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
            dialog.setCancelable(false);
        }


    }
}
