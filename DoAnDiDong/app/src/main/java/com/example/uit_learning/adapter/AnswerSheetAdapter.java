package com.example.uit_learning.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uit_learning.Common.Common;
import com.example.uit_learning.R;
import com.example.uit_learning.model.CurrentQuestion;

import java.util.List;

public class AnswerSheetAdapter extends RecyclerView.Adapter<AnswerSheetAdapter.MyViewHolder> {
    Context context;
    List<CurrentQuestion> currentQuestions;

    public AnswerSheetAdapter(Context context, List<CurrentQuestion> currentQuestions) {
        this.context = context;
        this.currentQuestions = currentQuestions;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_answer_sheet_item,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull  MyViewHolder holder, int position) {
        if(currentQuestions.get(position).getType()== Common.ANSWER_TYPE.ANSWER)
        {
            holder.question_item.setBackgroundResource(R.drawable.grid_question_answer);
        }
        else
        {
            if(currentQuestions.get(position).getType()== Common.ANSWER_TYPE.WRONG_ANSWER)
            {
                holder.question_item.setBackgroundResource(R.drawable.grid_question_wrong_answer);
            }
            else
            {
                if(currentQuestions.get(position).getType()== Common.ANSWER_TYPE.RIGHT_ANSWER)
                {
                    holder.question_item.setBackgroundResource(R.drawable.grid_question_right_answer);
                }
                else
                {
                    holder.question_item.setBackgroundResource(R.drawable.grid_question_no_answer);
                }
            }

        }

    }

    @Override
    public int getItemCount() {
        return currentQuestions.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
      View question_item;

        public MyViewHolder(@NonNull  View itemView) {
            super(itemView);
            question_item=itemView.findViewById(R.id.question_item);

        }
    }
}
