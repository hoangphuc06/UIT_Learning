package com.example.uit_learning;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        btn_flt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewPDFActivity.this,LoadQuestionActivity.class);
                startActivity(intent);
            }
        });

        pdfview=(WebView)findViewById(R.id.viewpdf);
        pdfview.getSettings().setJavaScriptEnabled(true);
        pdfview.setWebViewClient(new Callback());
        pdfview.getSettings().setBuiltInZoomControls(true);

        String filename=getIntent().getStringExtra("filename");
        String fileurl=getIntent().getStringExtra("fileurl");
        String id=getIntent().getStringExtra("id");

        actionBar.setTitle(filename);

        final ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle(filename);
        pd.setMessage("Opening....!!!");


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