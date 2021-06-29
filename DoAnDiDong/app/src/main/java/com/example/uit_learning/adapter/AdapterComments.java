package com.example.uit_learning.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uit_learning.AddPostActivity;
import com.example.uit_learning.MyProfileActivity;
import com.example.uit_learning.PostDetailActivity;
import com.example.uit_learning.R;
import com.example.uit_learning.ThereProfileActivity;
import com.example.uit_learning.model.Comment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.MyHolder>{

    Context context;
    List<Comment> commentList;

    String myUid, postId;

    private ClipboardManager myClipboard;
    private ClipData myClip;

    public AdapterComments(Context context, List<Comment> commentList, String myUid, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.myUid = myUid;
        this.postId = postId;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String uid = commentList.get(position).getUid();
        String email = commentList.get(position).getuEmail();
        String cid = commentList.get(position).getcId();
        String comment = commentList.get(position).getComment();
        String timestamp = commentList.get(position).getTimestamp();
        String pId = commentList.get(position).getpId();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        myClipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);

        holder.commentTv.setText(comment);
        holder.timeTv.setText(pTime);
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

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Posts");
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(pId).exists())
                        {
                            PopupMenu popupMenu  = new PopupMenu(context,holder.moreBtn,Gravity.END);

                            if (uid.equals(myUid))
                            {
                                popupMenu.getMenu().add(Menu.NONE,0,0,"Delete");
                            }

                            popupMenu.getMenu().add(Menu.NONE,1,0,"Copy");

                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    int id = item.getItemId();
                                    if (id == 0)
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                                        builder.setTitle("Delete");
                                        builder.setTitle("Are you sure to delete this comment?");
                                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteComment(cid);
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.create().show();
                                    }
                                    else if (id == 1)
                                    {
                                        String copyText=  comment;
                                        myClip = ClipData.newPlainText("text", copyText);
                                        myClipboard.setPrimaryClip(myClip);
                                        Toast.makeText(context,"Coppied..",Toast.LENGTH_SHORT).show();
                                    }
                                    return false;
                                }
                            });

                            popupMenu.show();
                        }
                        else
                        {
                            Toast.makeText(context,"Post not exist",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        holder.nameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Posts");
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(pId).exists())
                        {
                            if (uid.equals(myUid))
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
                        else
                        {
                            Toast.makeText(context,"Post not exist",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void deleteComment(String cid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.child("Comments").child(cid).removeValue();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String comments = "" + snapshot.child("pComments").getValue();
                int newCommentVal = Integer.parseInt(comments) - 1;
                ref.child("pComments").setValue("" + newCommentVal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {
        ImageView avatarIv;
        TextView nameTv, commentTv, timeTv;
        ImageButton moreBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatarIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            commentTv = itemView.findViewById(R.id.commentTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
        }
    }

}
