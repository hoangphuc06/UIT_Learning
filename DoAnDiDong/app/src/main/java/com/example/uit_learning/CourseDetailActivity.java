package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.uit_learning.adapter.AdapterUnits;
import com.example.uit_learning.model.Course;
import com.example.uit_learning.model.Unit;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CourseDetailActivity extends AppCompatActivity {

    TextView totalUnitTv, completedUnitTv;
    RecyclerView recview;
    AdapterUnits adapter;
    List<Unit> unitList;
    String titltCourse,typeCourse,idCourse;

    ProgressBar progressBar;

    Toolbar toolbar;
    TextView textToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textToolbar = findViewById(R.id.textTollbar);

        Intent intent = getIntent();

        titltCourse = intent.getStringExtra("title");
        typeCourse = intent.getStringExtra("type");
        idCourse = intent.getStringExtra("id");

        textToolbar.setText(titltCourse);

        totalUnitTv = findViewById(R.id.totalUnitTv);
        completedUnitTv = findViewById(R.id.completedUnitTv);

        recview = findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(this));

        unitList = new ArrayList<>();

//        FirebaseRecyclerOptions<Unit> options = new FirebaseRecyclerOptions
//                .Builder<Unit>()
//                .setQuery(FirebaseDatabase.getInstance()
//                .getReference("Courses")
//                .child(typeCourse)
//                .child(idCourse)
//                .child("Documents"), Unit.class)
//                .build();



        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Courses").child(typeCourse).child(idCourse).child("Documents");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                unitList.clear();
                int count = 0;
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    Unit unit = ds.getValue(Unit.class);

                    unitList.add(unit);

                    adapter = new AdapterUnits(CourseDetailActivity.this,unitList);

                    recview.setAdapter(adapter);

                    count++;
                }
                progressBar.setMax(count);
                totalUnitTv.setText("Total number of lessons: " + count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        progressBar = findViewById(R.id.progress_bar);

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("IsCompleted");
        reference2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(idCourse).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    count++;
                }
                progressBar.setProgress(count);
                completedUnitTv.setText("Number of lessons completed: " + count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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