package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.uit_learning.Common.Common;
import com.example.uit_learning.Common.SpaceDecoration;
import com.example.uit_learning.adapter.ResultGridAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ResultActivity extends AppCompatActivity {

    TextView txt_timer,txt_result,txt_right_answer;
    Button btn_filter_total,btn_filter_right_answer,btn_filter_wrong_answer,btn_filter_no_answer;
    RecyclerView recycler_result;

    String id, idUnit, typeUnit;

    ResultGridAdapter adapter,filtered_adapter;

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

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        idUnit=getIntent().getStringExtra("idUnit");
        typeUnit=getIntent().getStringExtra("typeUnit");

        setTitle("Result");

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(backToQuestion,new IntentFilter(Common.KEY_BACK_FROM_RESULT));


        txt_result = (TextView) findViewById(R.id.txt_result);
        txt_timer = (TextView) findViewById(R.id.txt_time);
        txt_right_answer = (TextView) findViewById(R.id.txt_right_answer);

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

        int percent = (Common.right_answer_cout * 100 / Common.list.size());
        if (percent > 80) {
            txt_result.setText("EXCELLENT");

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            HashMap<Object,String> hashMap = new HashMap<>();
            hashMap.put("uid",uid);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("IsCompleted").child(idUnit).child(id).child(uid);
            reference.setValue(hashMap);

        } else {
            txt_result.setText("GÀ");
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
            case android.R.id.home:
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void viewQuizAnswer() {
        Intent returnIntent=new Intent();
        returnIntent.putExtra("action","viewquizanswer");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    private void doQuizAgain() {

        Dialog dialog=new Dialog(ResultActivity.this);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialog.setContentView(R.layout.do_quiz_again_dialog);

        dialog.findViewById(R.id.bt_no_gain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.bt_yes_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent returnIntent=new Intent();
                returnIntent.putExtra("action","doitagain");
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });


        dialog.show();
    }


}