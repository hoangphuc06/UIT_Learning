package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uit_learning.Common.NetworkChangeListener;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.uit_learning.adapter.AdapterComments;
import com.example.uit_learning.model.Comment;
import com.example.uit_learning.model.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PostDetailActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

    String hisUid, myUid, myEmail, myName, myDp, postId, pLikes, hisDp, hisName, pImage, pTitle, pDescr;

    ImageView uPictureIv, pImageIv;
    TextView uNameTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv, pCommentsTv;
    ImageButton moreBtn, likeBtn;
    LinearLayout profileLayout;

    EditText commentEt;
    ImageButton sendBtn;
    ImageView cAvatarIv;

    ProgressDialog progressDialog;

    RecyclerView recyclerView;

    List<Comment> commentList;
    AdapterComments adapterComments;

    NestedScrollView mainLayout;
    RelativeLayout commentsLayout;

    boolean mProcessComment = false;
    boolean mProcessLike = false;

    Toolbar toolbar;
    TextView textToolbar;

    private ClipboardManager myClipboard;
    private ClipData myClip;

    private void sendNotification(String title, String message, String postId, String token) {
        try {
            RequestQueue queue = Volley.newRequestQueue(PostDetailActivity.this);

            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title", title);
            data.put("body", message);
            data.put("postId", postId);
            JSONObject notification_data = new JSONObject();
            notification_data.put("data", data);
            notification_data.put("to", token);

            JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    String api_key_header_value = "Key=AAAAvxcNqbc:APA91bFRDNIPGgLZm5T0TCKCoaozWOiywjxvow1aPcZW2bXdaXynoNHgJsDlJ2H_b5Jx3NaG_V3LqefhToaS6jGf1uLxYEq5wWbTp3OeSffp_fBWaaYZgcDnCYp4v0jl4xO9KxGRs2QJ";
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", api_key_header_value);
                    return headers;
                }
            };

            queue.add(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        broadcastReceiver = new NetworkChangeListener();
        CheckInternet();
        
        mainLayout = findViewById(R.id.layout_main);
        commentsLayout = findViewById(R.id.commentsLayout);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(postId)) {
                    mainLayout.setVisibility(View.GONE);
                    commentsLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textToolbar = findViewById(R.id.textTollbar);

        myClipboard = (ClipboardManager)PostDetailActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);

        uPictureIv = findViewById(R.id.uPictureIv);
        pImageIv = findViewById(R.id.pImageIv);
        uNameTv = findViewById(R.id.uNameTv);
        pTimeTv = findViewById(R.id.pTimeTv);
        pTitleTv = findViewById(R.id.pTitleTv);
        pDescriptionTv = findViewById(R.id.pDescriptionTv);
        pLikesTv = findViewById(R.id.pLikeTv);
        pCommentsTv = findViewById(R.id.pCommentsTv);
        moreBtn = findViewById(R.id.moreBtn);
        likeBtn = findViewById(R.id.likeBtn);

        profileLayout = findViewById(R.id.profileLayout);
        recyclerView = findViewById(R.id.recyclerView);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myEmail = user.getEmail();
        myUid = user.getUid();

        commentEt = findViewById(R.id.commentEt);
        sendBtn = findViewById(R.id.sendBtn);
        cAvatarIv = findViewById(R.id.cAvatarIv);
        
        loadPostInfo();

        loadUserInfo();

        setLikes();

        loadComments();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost();
            }
        });

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                showMoreOptions();
            }
        });

        pLikesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Posts");
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(postId).exists())
                        {
                            Intent intent = new Intent(PostDetailActivity.this, PostLikedByActivity.class);
                            intent.putExtra("postId",postId);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        }
                        else
                        {
                            Toast.makeText(PostDetailActivity.this,"Post not exist",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        uNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Posts");
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(postId).exists())
                        {
                            if (hisUid.equals(myUid))
                            {
                                Intent intent = new Intent(PostDetailActivity.this, MyProfileActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            }
                            else
                            {
                                Intent intent = new Intent(PostDetailActivity.this, ThereProfileActivity.class);
                                intent.putExtra("uid",hisUid);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            }
                        }
                        else
                        {
                            Toast.makeText(PostDetailActivity.this,"Post not exist",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        pImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this, ViewImageActivity.class);
                intent.putExtra("pImage",pImage);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
    }

    private void addToHisNotifications (String hisUid, String pId, String notification) {


        if (!hisUid.equals(myUid))
        {
            String timeStamp = "" + System.currentTimeMillis();

            HashMap<Object,String> hashMap = new HashMap<>();
            hashMap.put("pId",pId);
            hashMap.put("timestamp",timeStamp);
            hashMap.put("pUid",hisUid);
            hashMap.put("notification",notification);
            hashMap.put("sUid",myUid);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(hisUid).child("Notifications").child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }


    }

    private void loadComments() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);

        commentList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    Comment comment = ds.getValue(Comment.class);

                    commentList.add(comment);

                    adapterComments = new AdapterComments(PostDetailActivity.this, commentList, myUid, postId, hisUid);

                    recyclerView.setAdapter(adapterComments);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showMoreOptions() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Posts");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists())
                {
                    PopupMenu popupMenu = new PopupMenu(PostDetailActivity.this,moreBtn, Gravity.END);

                    if (hisUid.equals(myUid))
                    {
                        popupMenu.getMenu().add(Menu.NONE,0,0,"Delete");
                        popupMenu.getMenu().add(Menu.NONE,1,0,"Edit");
                    }

                    popupMenu.getMenu().add(Menu.NONE,2,0,"Copy");

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == 0)
                            {
                                beginDelete();
                            }
                            else if (id == 1)
                            {
                                Intent intent = new Intent(PostDetailActivity.this, AddPostActivity.class);
                                intent.putExtra("key","editPost");
                                intent.putExtra("editPostId",postId);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            } if (id == 2)
                            {
                                String copyText= pTitle + "\n" + pDescr;
                                myClip = ClipData.newPlainText("text", copyText);
                                myClipboard.setPrimaryClip(myClip);
                                Toast.makeText(PostDetailActivity.this,"Coppied..",Toast.LENGTH_SHORT).show();
                            }
                            return false;
                        }
                    });

                    popupMenu.show();
                }
                else
                {
                    Toast.makeText(PostDetailActivity.this,"Post not exist",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void beginDelete() {
        if (pImage.equals("noImage"))
        {
            deleteWithoutImage();
        }
        else
        {
            deleteWithImage();
        }
    }

    private void deleteWithImage() {
        final ProgressDialog progressDialog = new ProgressDialog(PostDetailActivity.this);
        progressDialog.setMessage("Deleting...");
        progressDialog.show();

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren())
                                {
                                    ds.getRef().removeValue();
                                }
                                Toast.makeText(PostDetailActivity.this,"Deleted successfully",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                onBackPressed();
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
                        Toast.makeText(PostDetailActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteWithoutImage() {
        final ProgressDialog progressDialog = new ProgressDialog(PostDetailActivity.this);
        progressDialog.setMessage("Deleting...");
        progressDialog.show();

        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(postId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    ds.getRef().removeValue();
                }
                Toast.makeText(PostDetailActivity.this,"Deleted successfully",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                onBackPressed();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLikes() {
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).hasChild(myUid))
                {
                    likeBtn.setImageResource(R.drawable.ic_liked_24);
                }
                else
                {
                    likeBtn.setImageResource(R.drawable.ic_like_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void likePost() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Posts");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists())
                {
                    mProcessLike = true;
                    final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
                    final DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
                    likesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (mProcessLike) {
                                if (snapshot.child(postId).hasChild(myUid)) {
                                    postsRef.child(postId).child("pLikes").setValue("" + (Integer.parseInt(pLikes)-1));
                                    likesRef.child(postId).child(myUid).removeValue();
                                    mProcessLike = false;

                                    likeBtn.setImageResource(R.drawable.ic_like_24);
                                }
                                else {
                                    postsRef.child(postId).child("pLikes").setValue("" + (Integer.parseInt(pLikes)+1));
                                    likesRef.child(postId).child(myUid).setValue("Liked");
                                    mProcessLike = false;

                                    addToHisNotifications(""+hisUid,""+postId,"Liked your post");
                                    if(!myUid.equals(hisUid)) {
                                        FirebaseDatabase.getInstance().getReference("Tokens").child(hisUid).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String token = snapshot.getValue(String.class);
                                                sendNotification("UIT Learning", myName + " liked your post " + postId, postId, token);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(PostDetailActivity.this,"Post not exist",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void postComment() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Posts");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists())
                {
                    String comment = commentEt.getText().toString().trim();
                    if (TextUtils.isEmpty(comment))
                    {
                        Toast.makeText(PostDetailActivity.this,"Commment is empty...",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    progressDialog = new ProgressDialog(PostDetailActivity.this);
                    progressDialog.setMessage("Adding comment...");
                    progressDialog.show();

                    String timeStamp = String.valueOf(System.currentTimeMillis());

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");

                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("cId",timeStamp);
                    hashMap.put("comment",comment);
                    hashMap.put("timestamp",timeStamp);
                    hashMap.put("uid",myUid);
                    hashMap.put("uEmail",myEmail);
                    hashMap.put("pId",postId);

                    ref.child(timeStamp).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(PostDetailActivity.this,"Comment Added...",Toast.LENGTH_SHORT).show();
                                    commentEt.setText("");
                                    updateCommentCount();

                                    addToHisNotifications(""+hisUid,""+postId,"Commented on your post");
                                    if(!myUid.equals(hisUid)) {
                                        FirebaseDatabase.getInstance().getReference("Tokens").child(hisUid).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String token = snapshot.getValue(String.class);
                                                sendNotification("UIT Learning", myName + " commented your post " + postId, postId, token);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(PostDetailActivity.this,"" + e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else
                {
                    Toast.makeText(PostDetailActivity.this,"Post not exist",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateCommentCount() {
        mProcessComment = true;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mProcessComment) {
                    String comments = "" + snapshot.child("pComments").getValue();
                    int newCommentVal = Integer.parseInt(comments) + 1;
                    ref.child("pComments").setValue("" + newCommentVal);
                    mProcessComment = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadUserInfo() {
        Query myRef = FirebaseDatabase.getInstance().getReference("Users");
        myRef.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    myName = "" + ds.child("name").getValue();
                    myDp = "" + ds.child("image").getValue();

                    try {
                        Picasso.get().load(myDp).placeholder(R.drawable.ic_image_default).into(cAvatarIv);
                    }
                    catch (Exception e) { }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPostInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    pTitle = "" + ds.child("pTitle").getValue();
                    pDescr = "" + ds.child("pDescr").getValue();
                    pLikes = "" + ds.child("pLikes").getValue();
                    String pTimeStamp = "" + ds.child("pTime").getValue();
                    pImage = "" + ds.child("pImage").getValue();
                    hisUid = "" + ds.child("uid").getValue();
                    String uEmail = "" + ds.child("uEmail").getValue();
                    String commentCount = "" + ds.child("pComments").getValue();

                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

                    pTitleTv.setText("Topic: " + pTitle);
                    pDescriptionTv.setText(pDescr);
                    pLikesTv.setText(pLikes + " Likes");
                    pTimeTv.setText(pTime);
                    pCommentsTv.setText(commentCount + " Comments");
                    textToolbar.setText("Id: " + postId);


                    if (pImage.equals("noImage"))
                    {
                        pImageIv.setVisibility(View.GONE);
                    }
                    else
                    {
                        pImageIv.setVisibility(View.VISIBLE);

                        try {
                            Picasso.get().load(pImage).into(pImageIv);
                        }
                        catch (Exception e)
                        {

                        }
                    }

                    //Load hinh + ten
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(hisUid);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            hisDp = snapshot.child("image").getValue(String.class);
                            try {
                                Picasso.get().load(hisDp).placeholder(R.drawable.ic_image_default).into(uPictureIv);
                            }
                            catch (Exception e) { }

                            hisName = snapshot.child("name").getValue(String.class);
                            uNameTv.setText(hisName);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
    @Override
    public void onResume() {

        super.onResume();
        CheckInternet();
    }
    private void CheckInternet() {
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregistorNetwork(){
        try {
            unregisterReceiver(broadcastReceiver);
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregistorNetwork();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregistorNetwork();
    }
}