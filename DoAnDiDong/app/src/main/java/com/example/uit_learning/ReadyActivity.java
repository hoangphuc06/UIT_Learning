package com.example.uit_learning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.example.uit_learning.Common.Common;

import com.example.uit_learning.Common.NetworkChangeListener;

public class ReadyActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

    Button btn_DoExercises;

    RadioButton mins10, mins15, mins20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);

        broadcastReceiver = new NetworkChangeListener();
        CheckInternet();

        Intent intent=getIntent();
        String id=intent.getStringExtra("id");
        String idUnit=getIntent().getStringExtra("idUnit");
        String typeUnit=getIntent().getStringExtra("typeUnit");

        mins10 = findViewById(R.id.mins10Rbtn);
        mins15 = findViewById(R.id.mins15Rbtn);
        mins20 = findViewById(R.id.mins20Rbtn);

        Common.TOTAL_TIME = 15*60*1000;

        mins10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true)
                {
                    Common.TOTAL_TIME = 10*60*1000;
                }
            }
        });

        mins15.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true)
                {
                    Common.TOTAL_TIME = 15*60*1000;
                }
            }
        });

        mins20.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true)
                {
                    Common.TOTAL_TIME = 20*60*1000;
                }
            }
        });

        btn_DoExercises=findViewById(R.id.btn_DoExercises);
        btn_DoExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ReadyActivity.this,LoadQuestionActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("idUnit",idUnit);
                intent.putExtra("typeUnit",typeUnit);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onResume() {

        super.onResume();
        CheckInternet();
    }
    private void CheckInternet() {
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregistorNetwork(){
        try {
            unregisterReceiver(broadcastReceiver);
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregistorNetwork();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregistorNetwork();
    }
}