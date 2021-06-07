package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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

    RecyclerView notificationsRv;

    FirebaseAuth firebaseAuth;

    ArrayList<Notification> notificationArrayList;
    AdapterNotifications adapterNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Notifications");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        notificationsRv = findViewById(R.id.notificationsRv);

        getAllNotification();
    }

    private void getAllNotification() {
        notificationArrayList = new ArrayList<>();

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
                        }

                        adapterNotifications = new AdapterNotifications(NotificationsActivity.this,notificationArrayList);

                        notificationsRv.setAdapter(adapterNotifications);
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
}