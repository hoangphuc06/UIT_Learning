package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.TextView;

import com.example.uit_learning.Common.Constants;
import com.example.uit_learning.Common.NetworkChangeListener;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewPDFActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

    PDFView pdfview;
    Toolbar toolbar;
    TextView textToolbar,textPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        broadcastReceiver = new NetworkChangeListener();
        CheckInternet();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        textToolbar = findViewById(R.id.textTollbar);
        textPage = findViewById(R.id.tvpage);

        pdfview= findViewById(R.id.pdfview);
        pdfview.zoomWithAnimation(2);

        String filename=getIntent().getStringExtra("filename");
        String fileurl=getIntent().getStringExtra("fileurl");
        String id=getIntent().getStringExtra("id");
        String idUnit=getIntent().getStringExtra("idUnit");
        String typeUnit=getIntent().getStringExtra("typeUnit");

        textToolbar.setText(filename);

        //actionBar.setTitle(filename);

        final ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle(filename);
        pd.setMessage("Opening....!!!");
        pd.show();

//        pdfview.setWebViewClient(new WebViewClient()
//        {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                pd.show();
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                pd.dismiss();
//            }
//        });

//        String url="";
//        try {
//            url= URLEncoder.encode(fileurl,"utf-8");
//        }catch (Exception ex)
//        {}
//
//        //pdfview.loadUrl("https://vntalking.com/cac-loai-layout-trong-android.html");
//
////        pdfview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(fileurl);
        reference.getBytes(Constants.MAX_BYTE_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        pd.dismiss();
                        pdfview.fromBytes(bytes).swipeHorizontal(false)
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        int currentPage = (page + 1);
                                        textPage.setText(currentPage + "/"+ pageCount);
                                    }
                                }).load();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


//    private class Callback extends WebViewClient {
//        @Override
//        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
//            return false;
//        }
//    }

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

    @Override
    public void onResume() {

        super.onResume();
        CheckInternet();
    }
    private void CheckInternet() {
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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