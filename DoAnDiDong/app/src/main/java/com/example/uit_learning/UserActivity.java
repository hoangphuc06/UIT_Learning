package com.example.uit_learning;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class UserActivity extends AppCompatActivity {

    Button btnLogout, btnNMLT, btnCTDLGT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        //Ẩn thanh actionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //kết thúc

        btnLogout  =findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener((view)->{
            Intent intent = new Intent(UserActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnNMLT  =findViewById(R.id.btnNMLT);
        btnNMLT.setOnClickListener((view)->{
            Intent intent = new Intent(UserActivity.this,NMLTActivity.class);
            startActivity(intent);
            //finish();
        });

        btnCTDLGT  =findViewById(R.id.btnCTDLGT);
        btnCTDLGT.setOnClickListener((view)->{
            Intent intent = new Intent(UserActivity.this,CTDLGTActivity.class);
            startActivity(intent);
            //finish();
        });
    }
}