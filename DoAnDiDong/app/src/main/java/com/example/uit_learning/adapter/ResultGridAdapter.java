package com.example.uit_learning.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uit_learning.Common.Common;
import com.example.uit_learning.R;
import com.example.uit_learning.ViewResultActivity;
import com.example.uit_learning.model.CurrentQuestion;

import java.util.List;

public class ResultGridAdapter extends RecyclerView.Adapter<ResultGridAdapter.MyViewHolder>{

    Context context;
    List<CurrentQuestion> currentQuestionList;

    public ResultGridAdapter(Context context, List<CurrentQuestion> currentQuestionList) {
        this.context = context;
        this.currentQuestionList = currentQuestionList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.layout_result_item,parent,false);

        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull  MyViewHolder holder, int position) {

        final Drawable right = context.getDrawable(R.drawable.background_correct_ans);
        final Drawable wrong = context.getDrawable(R.drawable.background_wrong_ans);
        final Drawable miss = context.getDrawable(R.drawable.background_miss_ans);

        holder.textQuestion.setText(new StringBuilder("Question ").append(currentQuestionList.get(position)
                .getQuestionIndex()+1));
        if(currentQuestionList.get(position).getType()== Common.ANSWER_TYPE.RIGHT_ANSWER)
        {
            holder.itemView.setBackground(right);
            //holder.itemView.setBackgroundColor(Color.GREEN);
            holder.iconStatus.setImageResource(R.drawable.ic_baseline_check_24);
            //img=context.getResources().getDrawable(R.drawable.ic_baseline_check_24);
            //holder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null,null,null,img);
        }
        else
        {
            if(currentQuestionList.get(position).getType()== Common.ANSWER_TYPE.WRONG_ANSWER)
            {
                holder.itemView.setBackground(wrong);
                //holder.itemView.setBackgroundColor(Color.parseColor("#AA0000"));
//                img=context.getResources().getDrawable(R.drawable.ic_baseline_clear_24);
//                holder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null,null,null,img);
                holder.iconStatus.setImageResource(R.drawable.ic_baseline_clear_24);
            }
            else
            {
                holder.itemView.setBackground(miss);
//                img=context.getResources().getDrawable(R.drawable.ic_baseline_error_outline_24);
//                holder.btn_question.setCompoundDrawablesWithIntrinsicBounds(null,null,null,img);
                holder.iconStatus.setImageResource(R.drawable.ic_baseline_error_outline_24);

            }
        }
    }

    @Override
    public int getItemCount() {
        return currentQuestionList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textQuestion;
        ImageView iconStatus;
        View itemView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textQuestion = itemView.findViewById(R.id.textQuestion);
            iconStatus = itemView.findViewById(R.id.iconStatus);
            this.itemView = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(itemView.getContext(), ViewResultActivity.class);
                    intent.putExtra("check",true);
                    intent.putExtra("index",currentQuestionList.get(getAdapterPosition()).getQuestionIndex());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
