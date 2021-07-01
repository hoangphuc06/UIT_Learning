package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.uit_learning.Common.Common;
import com.example.uit_learning.Common.NetworkChangeListener;
import com.example.uit_learning.Common.SpaceDecoration;
import com.example.uit_learning.adapter.ResultGridAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ResultActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

    TextView txt_timer,txt_result,txt_right_answer, txt_maxScore;
    Button btn_filter_total,btn_filter_right_answer,btn_filter_wrong_answer,btn_filter_no_answer;
    RecyclerView recycler_result;
    LottieAnimationView lottieAnimationView;

    String id, idUnit, typeUnit;

    ResultGridAdapter adapter,filtered_adapter;

    Toolbar toolbar;
    TextView textToolbar;

    BroadcastReceiver backToQuestion=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().toString().equals(Common.KEY_BACK_FROM_RESULT))
            {
                int question=intent.getIntExtra(Common.KEY_BACK_FROM_RESULT,-1);
                goBackActivityWithQuestion(question);
            }
        }
    };

    private void goBackActivityWithQuestion(int question) {
        Intent returnIntent=new Intent();
        returnIntent.putExtra(Common.KEY_BACK_FROM_RESULT,question);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textToolbar = findViewById(R.id.textTollbar);
        textToolbar.setText("Result");

        broadcastReceiver = new NetworkChangeListener();
        CheckInternet();

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        idUnit=getIntent().getStringExtra("idUnit");
        typeUnit=getIntent().getStringExtra("typeUnit");

        setTitle("Result");

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(backToQuestion,new IntentFilter(Common.KEY_BACK_FROM_RESULT));

        lottieAnimationView = findViewById(R.id.lottieView);

        txt_result = (TextView) findViewById(R.id.txt_result);
        txt_timer = (TextView) findViewById(R.id.txt_time);
        txt_right_answer = (TextView) findViewById(R.id.txt_right_answer);
        txt_maxScore = findViewById(R.id.txt_maxScorse);

        btn_filter_no_answer = (Button) findViewById(R.id.btn_filter_no_answer);
        btn_filter_right_answer = (Button) findViewById(R.id.btn_filter_right_answer);
        btn_filter_wrong_answer = (Button) findViewById(R.id.btn_filter_wrong_answer);
        btn_filter_total = (Button) findViewById(R.id.btn_filter_total);

        recycler_result = (RecyclerView) findViewById(R.id.recycle_result);
        recycler_result.setHasFixedSize(true);
        recycler_result.setLayoutManager(new GridLayoutManager(this, 3));

        adapter = new ResultGridAdapter(this, Common.answerSheetList);
        recycler_result.addItemDecoration(new SpaceDecoration(4));
        recycler_result.setAdapter(adapter);

        txt_timer.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(Common.timer),
                TimeUnit.MILLISECONDS.toSeconds(Common.timer) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Common.timer))));
        txt_right_answer.setText(new StringBuilder("").append(Common.right_answer_cout).append("/")
                .append(Common.list.size()));
        btn_filter_total.setText(new StringBuilder("").append(Common.list.size()));
        btn_filter_right_answer.setText(new StringBuilder("").append(Common.right_answer_cout));
        btn_filter_wrong_answer.setText(new StringBuilder("").append(Common.wrong_answer_cout));
        btn_filter_no_answer.setText(new StringBuilder("").append(Common.no_answer_cout));

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        int percent = (Common.right_answer_cout * 100 / Common.list.size());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("IsCompleted").child(uid).child(idUnit).child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("maxScore"))
                {
                    String maxScore = "" + snapshot.child("maxScore").getValue();
                    int maxScoreInt = Integer.parseInt(maxScore);
                    if (maxScoreInt > Common.right_answer_cout)
                    {
                        txt_maxScore.setText("Max scorse: " + maxScore + "/" + Common.listanswer.size());
                    }
                    else
                    {
                        txt_maxScore.setText("Max scorse: " + Common.right_answer_cout + "/" + Common.listanswer.size());
                    }
                }
                else
                {
                    txt_maxScore.setText("Max scorse: " + Common.right_answer_cout + "/" + Common.listanswer.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (percent > 80) {
            txt_result.setText("Congratulations,\nyou pass the test.");
            lottieAnimationView.setAnimation(R.raw.smile);

            DatabaseReference refCompleted = FirebaseDatabase.getInstance().getReference("IsCompleted").child(uid).child(idUnit).child(id);
            refCompleted.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("maxScore"))
                    {
                        String maxScore = "" + snapshot.child("maxScore").getValue();
                        int maxScoreInt = Integer.parseInt(maxScore);
                        if (maxScoreInt < Common.right_answer_cout)
                        {
                            HashMap<Object,String> hashMap = new HashMap<>();
                            hashMap.put("maxScore",String.valueOf(Common.right_answer_cout));
                            hashMap.put("completed","true");
                            refCompleted.setValue(hashMap);
                        }
                    }
                    else
                    {
                        HashMap<Object,String> hashMap = new HashMap<>();
                        hashMap.put("maxScore",String.valueOf(Common.right_answer_cout));
                        hashMap.put("completed","true");
                        refCompleted.setValue(hashMap);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        } else {
            txt_result.setText("You don't pass the test,\nplease try again.");
            lottieAnimationView.setAnimation(R.raw.cry);

            DatabaseReference refCompleted = FirebaseDatabase.getInstance().getReference("IsCompleted").child(uid).child(idUnit).child(id);
            refCompleted.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("maxScore"))
                    {
                        String maxScore = "" + snapshot.child("maxScore").getValue();
                        int maxScoreInt = Integer.parseInt(maxScore);
                        if (maxScoreInt < Common.right_answer_cout)
                        {
                            HashMap<Object,String> hashMap = new HashMap<>();
                            hashMap.put("maxScore",String.valueOf(Common.right_answer_cout));
                            hashMap.put("completed","false");
                            refCompleted.setValue(hashMap);
                        }
                    }
                    else
                    {
                        HashMap<Object,String> hashMap = new HashMap<>();
                        hashMap.put("maxScore",String.valueOf(Common.right_answer_cout));
                        hashMap.put("completed","false");
                        refCompleted.setValue(hashMap);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        btn_filter_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter==null)
                {
                    adapter=new ResultGridAdapter(ResultActivity.this,Common.answerSheetList);
                    recycler_result.setAdapter(adapter);
                }
                else
                {

                    recycler_result.setAdapter(adapter);
                }
            }
        });

        btn_filter_no_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.answerSheetListFiltered.clear();
                for(int i=0;i<Common.answerSheetList.size();i++)
                {
                    if(Common.answerSheetList.get(i).getType()== Common.ANSWER_TYPE.NO_ANSWER)
                    {
                        Common.answerSheetListFiltered.add(Common.answerSheetList.get(i));
                    }
                }
                filtered_adapter=new ResultGridAdapter(ResultActivity.this,Common.answerSheetListFiltered);
                recycler_result.setAdapter(filtered_adapter);

            }
        });

        btn_filter_wrong_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common. answerSheetListFiltered.clear();
                for(int i=0;i<Common.answerSheetList.size();i++)
                {
                    if(Common.answerSheetList.get(i).getType()== Common.ANSWER_TYPE.WRONG_ANSWER)
                    {
                        Common.answerSheetListFiltered.add(Common.answerSheetList.get(i));
                    }
                }
                filtered_adapter=new ResultGridAdapter(ResultActivity.this,Common.answerSheetListFiltered);
                recycler_result.setAdapter(filtered_adapter);
            }
        });

        btn_filter_right_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.answerSheetListFiltered.clear();
                for(int i=0;i<Common.answerSheetList.size();i++)
                {
                    if(Common.answerSheetList.get(i).getType()== Common.ANSWER_TYPE.RIGHT_ANSWER)
                    {
                        Common.answerSheetListFiltered.add(Common.answerSheetList.get(i));
                    }
                }
                filtered_adapter=new ResultGridAdapter(ResultActivity.this,Common.answerSheetListFiltered);
                recycler_result.setAdapter(filtered_adapter);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.result_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_do_quiz_again:
                doQuizAgain();
                break;
            case R.id.menu_view_answer:
                viewQuizAnswer();
                break;
        }
        return true;
    }

    private void viewQuizAnswer() {
        Intent intent=new Intent(ResultActivity.this,ViewResultActivity.class);
        startActivity(intent);
    }

    private void doQuizAgain() {

        Common.TOTAL_TIME = 15*60*1000;

        View view = LayoutInflater.from(ResultActivity.this).inflate(R.layout.dialog_do_it_again,null);

        TextView btnOk = view.findViewById(R.id.btnOk);
        TextView btnCancel = view.findViewById(R.id.btnCancel);
        RadioButton mins10Btn = view.findViewById(R.id.mins10Rbtn);
        RadioButton mins15Btn = view.findViewById(R.id.mins15Rbtn);
        RadioButton mins20Btn = view.findViewById(R.id.mins20Rbtn);

        final AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent returnIntent=new Intent();
                returnIntent.putExtra("action","doitagain");
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        mins10Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.TOTAL_TIME = 10*60*1000;
            }
        });
        mins15Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.TOTAL_TIME = 15*60*1000;
            }
        });
        mins20Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.TOTAL_TIME = 20*60*1000;
            }
        });

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
    public void onBackPressed() {
        View view = LayoutInflater.from(ResultActivity.this).inflate(R.layout.dialog_tests,null);

        TextView btnOK = view.findViewById(R.id.btnOk);
        TextView btnCancel = view.findViewById(R.id.btnCancel);
        TextView titleTv = view.findViewById(R.id.textTitle);
        TextView descrTv = view.findViewById(R.id.textDesCription);
        LottieAnimationView lottieView = view.findViewById(R.id.lottieView);

        //titleTv.setText("Do you want to return the course menu ?");
        descrTv.setText("Do you want to return the course menu ?");
        titleTv.setVisibility(View.GONE);
        lottieView.setAnimation(R.raw.bookmenu);

        final AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent returnIntent=new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}