package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uit_learning.Common.NetworkChangeListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.zip.Inflater;

public class ViewImageActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

    ImageView download;
    PhotoView imageView;
    String pImage;
    LinearLayout top,bottom;
    boolean check=true;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    Bitmap bitmap;

    Toolbar toolbar;
    TextView textToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textToolbar = findViewById(R.id.textTollbar);
        textToolbar.setText("Image");
        broadcastReceiver = new NetworkChangeListener();
        CheckInternet();

        imageView=findViewById(R.id.Image);
        top=findViewById(R.id.top);
        bottom=findViewById(R.id.bottom);

        Intent intent=getIntent();
        pImage=intent.getStringExtra("pImage");

        try {
            Picasso.get().load(pImage).into(imageView);
        }
        catch (Exception e)
        {

        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check)
                {
                    //download.setVisibility(View.GONE);
                    check=false;
                }
                else
                {
                    //download.setVisibility(View.VISIBLE);
                    check=true;
                }
            }
        });


    }


    private void saveImage() {
        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(System.currentTimeMillis());
        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path + "/Download");
        dir.mkdir();
        String imagename = time + ".PNG";
        File file = new File(dir, imagename);
        OutputStream out;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            Toast.makeText(ViewImageActivity.this, "File Save In Download", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(ViewImageActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==WRITE_EXTERNAL_STORAGE_CODE)
        {
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {

            }
            else
            {
                Toast.makeText(ViewImageActivity.this,"Permission Enable",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
    private void CheckInternet() {
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_download_image,menu);

        MenuItem item = menu.findItem(R.id.action_download);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permisson = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permisson, WRITE_EXTERNAL_STORAGE_CODE);
                    } else {
                        saveImage();
                    }
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(broadcastReceiver);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
    @Override
    public void onResume() {

        super.onResume();
        CheckInternet();
    }


    protected void unregistorNetwork(){
        try {
            unregisterReceiver(broadcastReceiver);
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregistorNetwork();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregistorNetwork();
    }
}