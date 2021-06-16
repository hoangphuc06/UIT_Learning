package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URLEncoder;

public class ViewPDFActivity extends AppCompatActivity {

    FloatingActionButton btn_flt;
    WebView pdfview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdfactivity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        btn_flt=findViewById(R.id.flt_btn);

        pdfview=(WebView)findViewById(R.id.viewpdf);
        pdfview.getSettings().setJavaScriptEnabled(true);
        pdfview.setWebViewClient(new Callback());
        pdfview.getSettings().setBuiltInZoomControls(true);
        pdfview.getSettings().setDisplayZoomControls(false);

        String filename=getIntent().getStringExtra("filename");
        String fileurl=getIntent().getStringExtra("fileurl");
        String id=getIntent().getStringExtra("id");
        String idUnit=getIntent().getStringExtra("idUnit");
        String typeUnit=getIntent().getStringExtra("typeUnit");

        actionBar.setTitle(filename);

        final ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle(filename);
        pd.setMessage("Opening....!!!");

        btn_flt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Courses").child(typeUnit).child(idUnit).child("Documents").child(id);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("Question"))
                        {
                            View view = LayoutInflater.from(ViewPDFActivity.this).inflate(R.layout.dialog_ready_do_exercises,null);

                            Button godoExercises = view.findViewById(R.id.btnGoDoExercise);

                            final AlertDialog.Builder builder = new AlertDialog.Builder(ViewPDFActivity.this);
                            builder.setView(view);

                            AlertDialog dialog = builder.create();
                            dialog.show();

                            godoExercises.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(ViewPDFActivity.this,LoadQuestionActivity.class);
                                    intent.putExtra("id",id);
                                    intent.putExtra("idUnit",idUnit);
                                    intent.putExtra("typeUnit",typeUnit);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                        }
                        else
                        {
                            View view = LayoutInflater.from(ViewPDFActivity.this).inflate(R.layout.no_question_dialog,null);

                            TextView bt_ok = view.findViewById(R.id.bt_ok);

                            final AlertDialog.Builder builder = new AlertDialog.Builder(ViewPDFActivity.this);
                            builder.setView(view);

                            AlertDialog dialog = builder.create();
                            dialog.show();

                            bt_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        pdfview.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pd.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pd.dismiss();
            }
        });

        String url="";
        try {
            url= URLEncoder.encode(fileurl,"utf-8");
        }catch (Exception ex)
        {}

        //pdfview.loadUrl("https://vntalking.com/cac-loai-layout-trong-android.html");

        pdfview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);

    }


    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}