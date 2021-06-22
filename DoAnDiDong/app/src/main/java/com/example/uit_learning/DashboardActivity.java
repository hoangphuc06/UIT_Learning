package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import meow.bottomnavigation.MeowBottomNavigation;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    BottomNavigationView bottomNavigationView;

    Toolbar toolbar;
    TextView textToolbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout)
        {
            firebaseAuth.signOut();
            startActivity(new Intent(DashboardActivity.this,LoginActivity.class));
            finish();
        }
//        if (id == R.id.action_add_post)
//        {
//            startActivity(new Intent(DashboardActivity.this,AddPostActivity.class));
//        }
//        if (id == R.id.action_open_notification)
//        {
//            startActivity(new Intent(DashboardActivity.this, NotificationsActivity.class));
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("Home");

        textToolbar = findViewById(R.id.textTollbar);

        textToolbar.setText("Home");
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.container,fragment1,"");
        ft1.commit();

        firebaseAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        textToolbar.setText("Home");
                        HomeFragment fragment1 = new HomeFragment();
                        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                        ft1.replace(R.id.container,fragment1,"");
//                        ft1.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
//                        ft1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                        ft1.addToBackStack(null);
                        ft1.commit();
                        return true;
                    case R.id.nav_forum:
                        textToolbar.setText("Forum");
                        ForumFragment fragment2 = new ForumFragment();
                        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                        ft2.replace(R.id.container,fragment2,"");
//                        ft2.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
//                        ft2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                        ft2.addToBackStack(null);
                        ft2.commit();
                        return true;
                    case R.id.nav_profile:
                        textToolbar.setText("Profile");
                        ProfileFragment fragment3 = new ProfileFragment();
                        FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                        ft3.replace(R.id.container,fragment3,"");
//                        ft3.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
//                        ft3.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                        ft3.addToBackStack(null);
                        ft3.commit();
                        return true;
                }

                return false;
            }
        });

    }

    @Override
    public void onResume() {

        super.onResume();
    }

}