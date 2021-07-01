package com.example.uit_learning;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uit_learning.Common.Common;
import com.example.uit_learning.adapter.AnswerSheetAdapter;
import com.example.uit_learning.model.CurrentQuestion;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

public class QuestionActivity extends AppCompatActivity {

    private static final int CODE_GET_RESULT = 9999;
    Button bt_finish;
    ImageButton bt_next,bt_pre;
    TextView txt_question_text,txt_timer;
    CheckBox ckbA,ckbB,ckbC,ckbD;
    FrameLayout layout_image;
    ProgressBar progressBar;
    int Index=0;
    int time_play = Common.TOTAL_TIME;

    RecyclerView answer_sheet_view;
    AnswerSheetAdapter answerSheetAdapter;


    String id, idUnit, typeUnit;

    Toolbar toolbar;
    TextView textToolbar;

    @Override
    protected void onDestroy() {
        if (Common.countDownTimer != null)
            Common.countDownTimer.cancel();
        super.onDestroy();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textToolbar = findViewById(R.id.textTollbar);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        idUnit=getIntent().getStringExtra("idUnit");
        typeUnit=getIntent().getStringExtra("typeUnit");

        if(Common.list.size()>0)
        {
            answer_sheet_view = findViewById(R.id.grid_answer);
            answer_sheet_view.setHasFixedSize(true);
            if (Common.list.size() > 5) {
                answer_sheet_view.setLayoutManager(new GridLayoutManager(this, Common.list.size() / 2));
            }
            else {
                answer_sheet_view.setLayoutManager(new GridLayoutManager(this, Common.list.size()));

            }
            answerSheetAdapter = new AnswerSheetAdapter(this,Common.answerSheetList);
            answer_sheet_view.setAdapter(answerSheetAdapter);

            txt_timer=findViewById(R.id.txt_timer);
            layout_image=(FrameLayout)findViewById(R.id.layout_image);
            progressBar=(ProgressBar)findViewById(R.id.progress_bar);
            txt_question_text=(TextView)findViewById(R.id.txt_question_text);
            ckbA=(CheckBox)findViewById(R.id.ckbA);
            ckbB=(CheckBox)findViewById(R.id.ckbB);
            ckbC=(CheckBox)findViewById(R.id.ckbC);
            ckbD=(CheckBox)findViewById(R.id.ckbD);

            countTimer();
            SetData();

            bt_next=findViewById(R.id.bt_next);
            bt_pre=findViewById(R.id.bt_pre);

            bt_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Index<Common.list.size()-1)
                    {
                        adapterChanged(Index);
                        Index++;
                        SetData();
                    }
                }
            });
            bt_pre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Index>0)
                    {
                        adapterChanged(Index);
                        Index--;
                        SetData();
                    }
                }
            });
        }
       else
        {
            Dialog dialog=new Dialog(QuestionActivity.this);
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

    private void countTimer() {
        if(Common.countDownTimer==null)
        {
            Common.countDownTimer=new CountDownTimer(Common.TOTAL_TIME,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    txt_timer.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)-
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                    time_play-=1000;
                }

                @Override
                public void onFinish() {
                    finishGame();

                }
            }.start();
        }
        else
        {
            Common.countDownTimer.cancel();
            Common.countDownTimer=new CountDownTimer(Common.TOTAL_TIME,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    txt_timer.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)-
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                    time_play-=1000;
                }

                @Override
                public void onFinish() {
                    finishGame();
                }

            }.start();

        }

    }

    public void SetData()
    {
        textToolbar.setText("Question: " + new StringBuilder(String.format("%d",Index+1))
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
                    Toast.makeText(QuestionActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
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
        ckbA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {

                    ckbB.setChecked(false);
                    ckbC.setChecked(false);
                    ckbD.setChecked(false);
                    Common.listanswer.set(Index,ckbA.getText().toString().substring(0,1));
                }
                else
                    Common.listanswer.set(Index,"null");
            }
        });

        ckbB.setText(Common.list.get(Index).getoB());
        ckbB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {

                    ckbA.setChecked(false);
                    ckbC.setChecked(false);
                    ckbD.setChecked(false);
                    Common.listanswer.set(Index,ckbB.getText().toString().substring(0,1));
                }
                else
                    Common.listanswer.set(Index,"null");

            }
        });

        ckbC.setText(Common.list.get(Index).getoC());
        ckbC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {

                    ckbA.setChecked(false);
                    ckbB.setChecked(false);
                    ckbD.setChecked(false);
                    Common.listanswer.set(Index,ckbC.getText().toString().substring(0,1));
                }
                else
                    Common.listanswer.set(Index,"null");

            }
        });

        ckbD.setText(Common.list.get(Index).getoD());
        ckbD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {

                    ckbA.setChecked(false);
                    ckbC.setChecked(false);
                    ckbB.setChecked(false);
                    Common.listanswer.set(Index,ckbD.getText().toString().substring(0,1));
                }
                else
                    Common.listanswer.set(Index,"null");

            }
        });

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
                Dialog dialog=new Dialog(QuestionActivity.this);
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                dialog.setContentView(R.layout.finish_dialog);

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
                        finishGame();
                    }
                });
                dialog.show();

            return true;

        }


        return super.onOptionsItemSelected(item);
    }

    private void finishGame() {


        Common.right_answer_cout=Common.wrong_answer_cout=Common.no_answer_cout=0;
        for (int i=0;i<Common.list.size();i++)
        {
            if(Common.listanswer.get(i).equals(Common.list.get(i).getAsd()))
            {
                Common.right_answer_cout++;
                Common.answerSheetList.get(i).setType(Common.ANSWER_TYPE.RIGHT_ANSWER);
            }
            else
            {
                if(Common.listanswer.get(i).equals("null"))
                {
                    Common.no_answer_cout++;
                }
                else
                {
                    Common.wrong_answer_cout++;
                    Common.answerSheetList.get(i).setType(Common.ANSWER_TYPE.WRONG_ANSWER);
                }
            }
        }
        Common.countDownTimer.cancel();
        Intent intent=new Intent(QuestionActivity.this,ResultActivity.class);
        intent.putExtra("idUnit",idUnit);
        intent.putExtra("typeUnit",typeUnit);
        intent.putExtra("id",id);
        startActivityForResult(intent,CODE_GET_RESULT);
        Common.timer=Common.TOTAL_TIME-time_play;
        //finish();
    }

    public void clearCkb()
    {
        ckbA.setChecked(false);
        ckbB.setChecked(false);
        ckbC.setChecked(false);
        ckbD.setChecked(false);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_GET_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                String action = data.getStringExtra("action");

                if (action.equals("doitagain")) {
                    Index = 0;
                    time_play = Common.TOTAL_TIME;
                    countTimer();
                    textToolbar.setVisibility(View.VISIBLE);
                    txt_timer.setVisibility(View.VISIBLE);
                    for (int i = 0; i < Common.list.size(); i++) {
                        Common.listanswer.set(i, "null");
                        Common.answerSheetList.set(i, new CurrentQuestion(i, Common.ANSWER_TYPE.NO_ANSWER));
                    }
                    answerSheetAdapter.notifyDataSetChanged();
                    resetQuestion();
                    SetData();
                }
            }
            else
            {
                Intent returnIntent=new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }

        }
    }


    public void adapterChanged(int i)
    {
        if(!Common.listanswer.get(i).equals("null"))
        {
            CurrentQuestion question=new CurrentQuestion(i, Common.ANSWER_TYPE.ANSWER);
            Common.answerSheetList.set(i,question);

        }
        else
        {
            CurrentQuestion question=new CurrentQuestion(i, Common.ANSWER_TYPE.NO_ANSWER);
            Common.answerSheetList.set(i,question);
        }
        answerSheetAdapter.notifyDataSetChanged();
    }


    public void resetQuestion() {
        ckbA.setEnabled(true);
        ckbB.setEnabled(true);
        ckbC.setEnabled(true);
        ckbD.setEnabled(true);

        ckbA.setChecked(false);
        ckbB.setChecked(false);
        ckbC.setChecked(false);
        ckbD.setChecked(false);

        ckbA.setTypeface(null, Typeface.NORMAL);
        ckbA.setTextColor(Color.BLACK);
        ckbB.setTypeface(null, Typeface.NORMAL);
        ckbB.setTextColor(Color.BLACK);
        ckbC.setTypeface(null, Typeface.NORMAL);
        ckbC.setTextColor(Color.BLACK);
        ckbD.setTypeface(null, Typeface.NORMAL);
        ckbD.setTextColor(Color.BLACK);
    }

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

}