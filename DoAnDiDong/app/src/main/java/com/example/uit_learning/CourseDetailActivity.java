package com.example.uit_learning;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.TextView;


import com.example.uit_learning.Common.NetworkChangeListener;
import com.example.uit_learning.adapter.AdapterUnits;
import com.example.uit_learning.model.Unit;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class CourseDetailActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

    TextView titleCourseTv;
    RecyclerView recview;
    AdapterUnits adapter;
    String titltCourse,typeCourse,idCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
        broadcastReceiver = new NetworkChangeListener();
        CheckInternet();

        Intent intent = getIntent();

        titltCourse = intent.getStringExtra("title");
        typeCourse = intent.getStringExtra("type");
        idCourse = intent.getStringExtra("id");

        //actionBar.setTitle(titltCourse);

        recview = findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Unit> options = new FirebaseRecyclerOptions.Builder<Unit>().setQuery(FirebaseDatabase.getInstance().getReference("Courses").child(typeCourse).child(idCourse).child("Documents"), Unit.class).build();

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}