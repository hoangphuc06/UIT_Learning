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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_question);

        Intent intent=getIntent();
        String id=intent.getStringExtra("id");
        String idUnit=getIntent().getStringExtra("idUnit");
        String typeUnit=getIntent().getStringExtra("typeUnit");


        Common.list = new ArrayList<>();
        Common.listanswer=new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Courses").child(typeUnit).child(idUnit).child("Documents").child(id).child("Question");


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
                Intent intent = new Intent(LoadQuestionActivity.this, QuestionActivity.class);
                intent.putExtra("idUnit",idUnit);
                intent.putExtra("typeUnit",typeUnit);
                intent.putExtra("id",id);
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