package com.example.uit_learning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


import com.example.uit_learning.adapter.AdapterUnits;
import com.example.uit_learning.model.Course;
import com.example.uit_learning.model.PDF;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class CourseDetailActivity extends AppCompatActivity {

    TextView titleCourseTv;
    RecyclerView recview;
    AdapterUnits adapter;
    String titltCourse,typeCourse,idCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        Intent intent = getIntent();

        titltCourse = intent.getStringExtra("title");
        typeCourse = intent.getStringExtra("type");
        idCourse = intent.getStringExtra("id");

        titleCourseTv = findViewById(R.id.titleCourseTv);
        titleCourseTv.setText(intent.getStringExtra("title"));


        recview = findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<PDF> options = new FirebaseRecyclerOptions.Builder<PDF>().setQuery(FirebaseDatabase.getInstance().getReference("Courses").child(typeCourse).child(idCourse).child("Documents"), PDF.class).build();

        adapter = new AdapterUnits(options);
        recview.setAdapter(adapter);


    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}