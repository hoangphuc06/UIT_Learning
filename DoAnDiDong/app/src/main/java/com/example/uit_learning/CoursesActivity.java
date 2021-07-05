package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.uit_learning.adapter.AdapterSlider;
import com.example.uit_learning.adapter.CourseRecyclerAdapter;
import com.example.uit_learning.model.Course;
import com.example.uit_learning.model.Slider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CoursesActivity extends AppCompatActivity {

    private static final FirebaseDatabase root = FirebaseDatabase.getInstance();

    private List<Course> courseList;

    RecyclerView recyclerView;
    CourseRecyclerAdapter adapter;

    Toolbar toolbar;
    TextView textToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textToolbar = findViewById(R.id.textTollbar);

        recyclerView = findViewById(R.id.courseRcv);

        courseList = new ArrayList<>();

        Intent intent = getIntent();
        String type  = intent.getStringExtra("type");
        String title = intent.getStringExtra("title");

        textToolbar.setText(title);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);

        root.getReference("Courses").child(type).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseList.clear();
                for (DataSnapshot ds: snapshot.getChildren())
                {
                    Course course = ds.getValue(Course.class);

                    courseList.add(course);

                    adapter = new CourseRecyclerAdapter(courseList);

                    recyclerView.setAdapter(adapter);
                }
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