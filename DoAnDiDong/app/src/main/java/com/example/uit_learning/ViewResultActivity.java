package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uit_learning.Common.Common;
import com.example.uit_learning.Common.NetworkChangeListener;
import com.example.uit_learning.adapter.AnswerSheetAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

public class ViewResultActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

    Button bt_next,bt_pre;
    TextView answer;
    TextView txt_question_text,txt_timer,txt_question_count;
    CheckBox ckbA,ckbB,ckbC,ckbD;
    FrameLayout layout_image;
    ImageView answerImage;
    ProgressBar progressBar;
    int Index=0;
    int time_play = Common.TOTAL_TIME;

    RecyclerView answer_sheet_view;
    AnswerSheetAdapter answerSheetAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result);

        broadcastReceiver = new NetworkChangeListener();
        CheckInternet();

        Intent intent=getIntent();
        boolean check=intent.getBooleanExtra("check",false);
        int subIndex=intent.getIntExtra("index",-1);

        if(Common.list.size()>0)
        {
            answer=findViewById(R.id.answer);
            answerImage=findViewById(R.id.img_answer);

            answer_sheet_view = findViewById(R.id.grid_answer);
            answer_sheet_view.setHasFixedSize(true);
            if (Common.list.size() > 5) {
                answer_sheet_view.setLayoutManager(new GridLayoutManager(this, Common.list.size() / 2));
            }
            else
            {
                answer_sheet_view.setLayoutManager(new GridLayoutManager(this, Common.list.size()));
            }
            answerSheetAdapter = new AnswerSheetAdapter(this,Common.answerSheetList);
            answer_sheet_view.setAdapter(answerSheetAdapter);

            txt_timer=findViewById(R.id.txt_timer);
            txt_timer.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(Common.timer),
                    TimeUnit.MILLISECONDS.toSeconds(Common.timer) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Common.timer))));
            txt_question_count=findViewById(R.id.txt_question_cout);
            layout_image=(FrameLayout)findViewById(R.id.layout_image);
            progressBar=(ProgressBar)findViewById(R.id.progress_bar);
            txt_question_text=(TextView)findViewById(R.id.txt_question_text);
            ckbA=(CheckBox)findViewById(R.id.ckbA);
            ckbB=(CheckBox)findViewById(R.id.ckbB);
            ckbC=(CheckBox)findViewById(R.id.ckbC);
            ckbD=(CheckBox)findViewById(R.id.ckbD);
            if(check)
                Index=subIndex;
            SetData();
            disableAnswer();

            bt_next=findViewById(R.id.bt_next);
            bt_pre=findViewById(R.id.bt_pre);

            bt_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Index<Common.list.size()-1)
                    {
                        Index++;
                        SetData();
                        answerImage.setVisibility(View.GONE);
                    }
                }
            });
            bt_pre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Index>0)
                    {
                        Index--;
                        SetData();
                        answerImage.setVisibility(View.GONE);
                    }
                }
            });

            answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.list.get(Index).getAnswer() != "nulll")
                    {
                        answerImage.setVisibility(View.VISIBLE);
                        Picasso.get().load(Common.list.get(Index).getAnswer()).into(answerImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(ViewResultActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else
                    {
                        Toast.makeText(ViewResultActivity.this, "No answer for this question", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else
        {
            Dialog dialog=new Dialog(ViewResultActivity.this);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            dialog.setContentView(R.layout.no_question_dialog);

            dialog.findViewById(R.id.bt_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });
            dialog.findViewById(R.id.bt_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

    }

    public void SetData()
    {
        txt_question_count.setText(new StringBuilder(String.format("%d",Index+1))
                .append("/")
                .append(String.format("%d",Common.list.size())).toString());

        if(Common.list.get(Index).getIsImageQuestion()==1)
        {
            layout_image.setVisibility(View.VISIBLE);
            ImageView img_question=(ImageView)findViewById(R.id.img_question);
            Picasso.get().load(Common.list.get(Index).getQuestionImage()).into(img_question, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(ViewResultActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
        else
        {
            layout_image.setVisibility(View.GONE);
        }

        txt_question_text.setText(Common.list.get(Index).getQues());


        if(!Common.listanswer.get(Index).equals("null"))
        {
            if(Common.listanswer.get(Index).equals("A"))
            {
                clearCkb();
                ckbA.setChecked(true);
            }
            else
            {
                if(Common.listanswer.get(Index).equals("B"))
                {
                    clearCkb();
                    ckbB.setChecked(true);
                }
                else
                {
                    if(Common.listanswer.get(Index).equals("C"))
                    {
                        clearCkb();
                        ckbC.setChecked(true);
                    }
                    else
                    {
                        if(Common.listanswer.get(Index).equals("D"))
                        {
                            clearCkb();
                            ckbD.setChecked(true);
                        }
                    }
                }

            }

        }
        else
        {
            clearCkb();
        }


        ckbA.setText(Common.list.get(Index).getoA());
        ckbB.setText(Common.list.get(Index).getoB());
        ckbC.setText(Common.list.get(Index).getoC());
        ckbD.setText(Common.list.get(Index).getoD());
        resetAnswer();
        showCorrectAnswer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();

        if(id==R.id.menu_finish_game)
        {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void showCorrectAnswer() {
        String answer= Common.list.get(Index).getAsd();
        if(answer.equals("A"))
        {
            ckbA.setTypeface(null, Typeface.BOLD);
            if(Common.listanswer.get(Index).equals("A"))
                ckbA.setTextColor(Color.GREEN);
            else
                ckbA.setTextColor(Color.RED);
        }
        else
        {
            if(answer.equals("B"))
            {
                ckbB.setTypeface(null, Typeface.BOLD);
                if(Common.listanswer.get(Index).equals("B"))
                    ckbB.setTextColor(Color.GREEN);
                else
                    ckbB.setTextColor(Color.RED);
            }
            else
            {
                if(answer.equals("C"))
                {
                    ckbC.setTypeface(null, Typeface.BOLD);
                    if(Common.listanswer.get(Index).equals("C"))
                        ckbC.setTextColor(Color.GREEN);
                    else
                        ckbC.setTextColor(Color.RED);
                }
                else
                {
                    if(answer.equals("D")) {
                        ckbD.setTypeface(null, Typeface.BOLD);
                        if(Common.listanswer.get(Index).equals("D"))
                            ckbD.setTextColor(Color.GREEN);
                        else
                            ckbD.setTextColor(Color.RED);
                    }
                }
            }
        }

    }

    public void resetAnswer()
    {
        ckbA.setTypeface(null, Typeface.NORMAL);
        ckbA.setTextColor(Color.BLACK);
        ckbB.setTypeface(null, Typeface.NORMAL);
        ckbB.setTextColor(Color.BLACK);
        ckbC.setTypeface(null, Typeface.NORMAL);
        ckbC.setTextColor(Color.BLACK);
        ckbD.setTypeface(null, Typeface.NORMAL);
        ckbD.setTextColor(Color.BLACK);
    }

    public void disableAnswer() {
        ckbA.setEnabled(false);
        ckbB.setEnabled(false);
        ckbC.setEnabled(false);
        ckbD.setEnabled(false);
    }

    public void clearCkb()
    {
        ckbA.setChecked(false);
        ckbB.setChecked(false);
        ckbC.setChecked(false);
        ckbD.setChecked(false);
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