package com.example.uit_learning.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uit_learning.CourseDetailActivity;
import com.example.uit_learning.R;

import java.util.ArrayList;
import java.util.List;

import com.example.uit_learning.model.Course;
import com.squareup.picasso.Picasso;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder> {

    private List<Course> courseList;

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
        notifyDataSetChanged();
    }

    public CourseRecyclerAdapter(List<Course> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseRecyclerAdapter.ViewHolder holder, int position) {
        if (courseList.get(position) == null) {
            return;
        }

        holder.itemTitle.setText(courseList.get(position).getTitle());

        try {
            Picasso.get().load(courseList.get(position).getImage()).into(holder.itemImage);
        }
        catch (Exception e)
        {

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity)holder.itemView.getContext();
                Intent intent = new Intent(holder.itemImage.getContext(), CourseDetailActivity.class);
                intent.putExtra("title",courseList.get(position).getTitle());
                intent.putExtra("type",courseList.get(position).getType());
                intent.putExtra("id", courseList.get(position).getId());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (courseList != null) {
            return courseList.size();
        }
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            itemImage = itemView.findViewById(R.id.image_card);
            itemTitle = itemView.findViewById(R.id.title_card);
        }
    }
}
