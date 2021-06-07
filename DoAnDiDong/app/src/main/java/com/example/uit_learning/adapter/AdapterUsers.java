package com.example.uit_learning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uit_learning.R;
import com.example.uit_learning.model.Post;
import com.example.uit_learning.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {
    Context context;
    List<User> userList;

    public AdapterUsers(Context context, List<User> users) {
        this.context = context;
        this.userList = users;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String name = userList.get(position).getName();
        String email = userList.get(position).getEmail();
        String image = userList.get(position).getImage();

        holder.nameTv.setText(name);
        holder.emailTv.setText(email);

        try
        {
            Picasso.get().load(image).placeholder(R.drawable.ic_image_default).into(holder.avatarIv);
        }
        catch (Exception e)
        {
            //Picasso.get().load(R.drawable.ic_image_default).into(holder.avatarIv);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView avatarIv;
        TextView nameTv, emailTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatarIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            emailTv = itemView.findViewById(R.id.emailTv);
        }
    }
}
