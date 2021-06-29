package com.example.uit_learning.adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uit_learning.AddPostActivity;
import com.example.uit_learning.DashboardActivity;
import com.example.uit_learning.MyProfileActivity;
import com.example.uit_learning.PostDetailActivity;
import com.example.uit_learning.PostLikedByActivity;
import com.example.uit_learning.R;
import com.example.uit_learning.ThereProfileActivity;
import com.example.uit_learning.model.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder>{

    android.content.Context context;
    List<Post> postList;

    String myUid;

    private ClipboardManager myClipboard;
    private ClipData myClip;

    public AdapterPosts(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String pId = postList.get(position).getpId();
        String pTitle = postList.get(position).getpTitle();
        String pDescription = postList.get(position).getpDescr();
        String pImage = postList.get(position).getpImage();
        String pTimeStamp = postList.get(position).getpTime();
        String pLikes = postList.get(position).getpLikes();
        String pComments = postList.get(position).getpComments();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        myClipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);

        holder.pIdTv.setText("Id: " + pId);

//        Random rnd = new Random();
//        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//        holder.layoutColor.setBackgroundColor(color);


        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText("Topic: " + pTitle);
        holder.pLikesTv.setText(pLikes);
        holder.pCommentsTv.setText(pComments);
        if (pDescription.length() > 30)
        {
            holder.pDescriptionTv.setText(pDescription.substring(0,31).trim() + "...");
        }
        else
        {
            holder.pDescriptionTv.setText(pDescription);
        }

        // Load hinh + ten
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uDp = snapshot.child("image").getValue(String.class);
                try {
                    Picasso.get().load(uDp).placeholder(R.drawable.ic_image_default).into(holder.uPictureIv);
                }
                catch (Exception e) { }

                String uName = snapshot.child("name").getValue(String.class);
                holder.uNameTv.setText(uName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        holder.btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Posts");
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(pId).exists())
                        {
                            Activity activity = (Activity)context;
                            Intent intent = new Intent(context, PostDetailActivity.class);
                            intent.putExtra("postId",pId);
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
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

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                showMoreOptions(holder.moreBtn,uid,myUid,pId,pImage,pTitle,pDescription);
            }
        });


        holder.uNameTv.setOnClickListener(new View.OnClickListener() {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showMoreOptions(ImageButton moreBtn, String uid, String myUid, String pId, String pImage, String pTitle, String pDescription) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Posts");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(pId).exists())
                {
                    PopupMenu popupMenu = new PopupMenu(context,moreBtn, Gravity.END);

                    if (uid.equals(myUid))
                    {
                        popupMenu.getMenu().add(Menu.NONE,0,0,"Delete");
                        popupMenu.getMenu().add(Menu.NONE,1,0,"Edit");
                    }

                    popupMenu.getMenu().add(Menu.NONE,2,0,"View Detail");
                    popupMenu.getMenu().add(Menu.NONE,3,0,"Copy");

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == 0)
                            {
                                beginDelete(pId,pImage);
                            }
                            else if (id == 1)
                            {
                                Activity activity = (Activity)context;
                                Intent intent = new Intent(context, AddPostActivity.class);
                                intent.putExtra("key","editPost");
                                intent.putExtra("editPostId",pId);
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            }
                            else if (id == 2)
                            {
                                Activity activity1 = (Activity)context;
                                Intent intent = new Intent(context, PostDetailActivity.class);
                                intent.putExtra("postId",pId);
                                activity1.startActivity(intent);
                                activity1.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                            }
                            else if (id == 3)
                            {
                                String copyText= pTitle + "\n" + pDescription;
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

    private void beginDelete(String pId, String pImage) {
        if (pImage.equals("noImage"))
        {
            deleteWithoutImage(pId);
        }
        else
        {
            deleteWithImage(pId,pImage);
        }
    }

    private void deleteWithImage(String pId, String pImage) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");
        progressDialog.show();

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren())
                                {
                                    ds.getRef().removeValue();
                                }
                                Toast.makeText(context,"Deleted successfully",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteWithoutImage(String pId) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");
        progressDialog.show();

        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    ds.getRef().removeValue();
                }
                Toast.makeText(context,"Deleted successfully",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView uPictureIv;
        TextView uNameTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv, pCommentsTv, pIdTv;
        ImageButton moreBtn, btnGo;
        View itemView;
        //LinearLayout layoutColor;

        public MyHolder(@NonNull View itemView)
        {
            super(itemView);

            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv = itemView.findViewById(R.id.pLikeTv);
            pCommentsTv = itemView.findViewById(R.id.pCommentsTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            pIdTv = itemView.findViewById(R.id.pIdTv);
            btnGo = itemView.findViewById(R.id.btnGo);
            this.itemView = itemView;
        }
    }

}
