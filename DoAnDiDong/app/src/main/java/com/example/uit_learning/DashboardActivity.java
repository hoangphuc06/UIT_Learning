package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.uit_learning.Common.NetworkChangeListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

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

        broadcastReceiver = new NetworkChangeListener();
        CheckInternet();

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
                    case R.id.nav_settings:
                        textToolbar.setText("Settings");
                        SettingsFragment fragment3 = new SettingsFragment();
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
    private void CheckInternet() {
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}