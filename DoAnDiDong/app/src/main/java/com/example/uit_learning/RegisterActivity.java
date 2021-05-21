package com.example.uit_learning;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {

    Button btnLoginActivity, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Ẩn thanh actionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //kết thúc

        btnLoginActivity = findViewById(R.id.btnLoginActivity);
        btnRegister = findViewById(R.id.btnRegister);

        btnLoginActivity.setOnClickListener((view)->{
            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnRegister.setOnClickListener((view)->{
            Intent intent = new Intent(RegisterActivity.this,SuccessRegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}