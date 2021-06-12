package com.example.uit_learning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class CourseDetailActivity extends AppCompatActivity {

    TextView titleCourseTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        Intent intent = getIntent();

        titleCourseTv = findViewById(R.id.titleCourseTv);

        titleCourseTv.setText(intent.getStringExtra("title"));
    }
}