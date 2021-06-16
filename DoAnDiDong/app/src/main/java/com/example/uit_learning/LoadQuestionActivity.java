package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import com.example.uit_learning.Common.Common;
import com.example.uit_learning.model.CurrentQuestion;
import com.example.uit_learning.model.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoadQuestionActivity extends AppCompatActivity {


    DatabaseReference databaseReference;
    public static final String KEY_BACK_FROM_RESULT = "BACK_FROM_RESULT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Common.list = new ArrayList<>();
        Common.listanswer=new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Question");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Question question = dataSnapshot.getValue(Question.class);
                    Common.list.add(question);
                }

                for(int i=0;i<Common.list.size();i++)
                {
                    Common.listanswer.add("null");
                    Common.answerSheetList.add(new CurrentQuestion(i, Common.ANSWER_TYPE.NO_ANSWER));
                }
                Intent intent = new Intent(LoadQuestionActivity.this, QuestionActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent intent = new Intent(LoadQuestion.this, ContestActivity.class);
                //startActivity(intent);

            }
        }, 1500);
    }
}