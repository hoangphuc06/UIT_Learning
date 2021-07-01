package com.example.uit_learning;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.uit_learning.Common.Common;
import com.example.uit_learning.model.CurrentQuestion;

public class ReadyActivity extends AppCompatActivity {

    private static final int CODE_GET_RESULT = 9999;

    Button btn_DoExercises;

    RadioButton mins10, mins15, mins20;

    Toolbar toolbar;
    TextView textToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textToolbar = findViewById(R.id.textTollbar);
        textToolbar.setText("Are you ready?");

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
                startActivityForResult(intent,CODE_GET_RESULT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_GET_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                onBackPressed();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}