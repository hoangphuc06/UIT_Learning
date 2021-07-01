package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.example.uit_learning.Common.NetworkChangeListener;
import com.example.uit_learning.adapter.AdapterNotifications;
import com.example.uit_learning.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

    RecyclerView notificationsRv;

    FirebaseAuth firebaseAuth;

    ArrayList<Notification> notificationArrayList;
    AdapterNotifications adapterNotifications;

    Toolbar toolbar;
    TextView textToolbar;

    LayoutAnimationController layoutAnimationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        broadcastReceiver = new NetworkChangeListener();
        CheckInternet();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textToolbar = findViewById(R.id.textTollbar);
        textToolbar.setText("Notifications");

        firebaseAuth = FirebaseAuth.getInstance();

        notificationsRv = findViewById(R.id.notificationsRv);
        notificationsRv.setItemViewCacheSize(10);

        notificationArrayList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationsActivity.this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);


        notificationsRv.setLayoutManager(layoutManager);

        getAllNotification();

    }

    private void getAllNotification() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notificationArrayList.clear();

                        for (DataSnapshot ds: snapshot.getChildren())
                        {

                            Notification model = ds.getValue(Notification.class);

                            notificationArrayList.add(model);

                            adapterNotifications = new AdapterNotifications(NotificationsActivity.this,notificationArrayList);

                            notificationsRv.setAdapter(adapterNotifications);

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
    private void CheckInternet() {
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(broadcastReceiver);
//    }
}