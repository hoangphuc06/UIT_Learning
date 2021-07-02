package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.BroadcastReceiver;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.uit_learning.Common.Common;
import com.example.uit_learning.model.CurrentQuestion;

import com.example.uit_learning.Common.NetworkChangeListener;
import com.example.uit_learning.model.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReadyActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;
    private static final int CODE_GET_RESULT = 9999;

    Button btn_DoExercises;

    RadioButton mins10, mins15, mins20;

    Toolbar toolbar;
    TextView textToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);

        broadcastReceiver = new NetworkChangeListener();
        CheckInternet();
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
//                Intent intent=new Intent(ReadyActivity.this,LoadQuestionActivity.class);
//                intent.putExtra("id",id);
//                intent.putExtra("idUnit",idUnit);
//                intent.putExtra("typeUnit",typeUnit);
//                startActivityForResult(intent,CODE_GET_RESULT);
//                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                Common.list = new ArrayList<>();
                Common.listanswer=new ArrayList<>();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Courses").child(typeUnit).child(idUnit).child("Documents").child(id).child("Question");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Common.list.clear();
                        Common.listanswer.clear();
                        Common.answerSheetList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Question question = dataSnapshot.getValue(Question.class);
                            Common.list.add(question);
                        }

                        for(int i=0;i<Common.list.size();i++)
                        {
                            Common.listanswer.add("null");
                            Common.answerSheetList.add(new CurrentQuestion(i, Common.ANSWER_TYPE.NO_ANSWER));
                        }
                        Intent intent = new Intent(ReadyActivity.this, QuestionActivity.class);
                        intent.putExtra("idUnit",idUnit);
                        intent.putExtra("typeUnit",typeUnit);
                        intent.putExtra("id",id);
                        startActivityForResult(intent,CODE_GET_RESULT);
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        //finish();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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

//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(broadcastReceiver);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_GET_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                onBackPressed();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
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