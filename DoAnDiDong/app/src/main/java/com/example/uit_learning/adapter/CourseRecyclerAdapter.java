package com.example.uit_learning.adapter;

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
        holder.itemImage.setImageResource(R.drawable.background_avt_img);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemImage.getContext(), CourseDetailActivity.class);
                intent.putExtra("title",courseList.get(position).getTitle());
                intent.putExtra("type",courseList.get(position).getType());
                intent.putExtra("id", courseList.get(position).getId());
                holder.itemImage.getContext().startActivity(intent);
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
