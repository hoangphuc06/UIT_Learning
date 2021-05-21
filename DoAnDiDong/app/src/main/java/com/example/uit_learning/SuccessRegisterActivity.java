package com.example.uit_learning;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class SuccessRegisterActivity extends AppCompatActivity {

    TextView textViewToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_register);
        //Ẩn thanh actionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //kết thúc

        textViewToLogin = findViewById(R.id.txvToLogin);
        textViewToLogin.setOnClickListener((view)->{
            Intent intent = new Intent(SuccessRegisterActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }
}