package com.example.uit_learning.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uit_learning.MyProfileActivity;
import com.example.uit_learning.PostDetailActivity;
import com.example.uit_learning.R;
import com.example.uit_learning.ThereProfileActivity;
import com.example.uit_learning.model.Post;
import com.example.uit_learning.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
        String uid = userList.get(position).getUid();

        //holder.nameTv.setText(name);
        holder.emailTv.setText(email);

        //Load hinh + ten
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String link = snapshot.child("image").getValue(String.class);
                try {
                    Picasso.get().load(link).placeholder(R.drawable.ic_image_default).into(holder.avatarIv);
                }
                catch (Exception e) { }

                String name = snapshot.child("name").getValue(String.class);
                holder.nameTv.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    Activity activity = (Activity)context;
                    Intent intent = new Intent(context, MyProfileActivity.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }
                else
                {
                    Activity activity = (Activity)context;
                    Intent intent = new Intent(context, ThereProfileActivity.class);
                    intent.putExtra("uid",uid);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }
            }
        });
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
