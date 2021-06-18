package com.example.uit_learning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ReadyActivity extends AppCompatActivity {

    Button btn_DoExercises;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);

        Intent intent=getIntent();
        String id=intent.getStringExtra("id");
        String idUnit=getIntent().getStringExtra("idUnit");
        String typeUnit=getIntent().getStringExtra("typeUnit");

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
}