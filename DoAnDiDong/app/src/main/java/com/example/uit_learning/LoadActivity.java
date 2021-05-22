package com.example.uit_learning;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.Circle;

public class LoadActivity extends AppCompatActivity {

    private static int SPLASH_SCREEEN = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        //Ẩn thanh actionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //kết thúc

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminateDrawable(new Circle());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadActivity.this,MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEEN);
    }
}