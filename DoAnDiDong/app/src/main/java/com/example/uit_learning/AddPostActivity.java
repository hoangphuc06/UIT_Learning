package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uit_learning.Common.NetworkChangeListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

    FirebaseAuth firebaseAuth;
    DatabaseReference userDbRef;
    FirebaseUser user;

    EditText titleEd, descriptionEd;
    ImageView imageIv;
    Button uploadBtn;

    ImageButton pDeleteImageBtn, pAddImageBtn;

    Toolbar toolbar;
    TextView textToolbar;

    private static final int STORAGE_REQUEST_CODE = 100;
    private static final int IMAGGE_PICK_GALLERY_CODE = 200;

    String storagePermissions[];

    Uri image_uri = null;

    String name, email, uid, dp;

    String editTitle, editDescription, editImage, editTimestamp;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        broadcastReceiver = new NetworkChangeListener();
        CheckInternet();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textToolbar = findViewById(R.id.textTollbar);

        firebaseAuth = FirebaseAuth.getInstance();

        titleEd = findViewById(R.id.pTitleEt);
        descriptionEd = findViewById(R.id.pDescriptionEt);
        imageIv = findViewById(R.id.pImageIv);
        uploadBtn = findViewById(R.id.pUploadBtn);
        pDeleteImageBtn = findViewById(R.id.pDeleteImageBtn);
        pAddImageBtn = findViewById(R.id.pAddImageBtn);

        user = firebaseAuth.getCurrentUser();
        email = user.getEmail();
        uid = user.getUid();

        userDbRef = FirebaseDatabase.getInstance().getReference("Users");

        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog = new ProgressDialog(this);

        Intent intent = getIntent();
        String isUpdateKey = "" + intent.getStringExtra("key");
        String editPostId = "" + intent.getStringExtra("editPostId");
        if (isUpdateKey.equals("editPost"))
        {
            //getSupportActionBar().setTitle("Update Post");
            textToolbar.setText("Update Post");
            uploadBtn.setText("Update");
            loadPostData(editPostId);
        }
        else
        {
            //getSupportActionBar().setTitle("Add New Post");
            textToolbar.setText("Add new post");
            uploadBtn.setText("Upload");
            pDeleteImageBtn.setVisibility(View.GONE);
            imageIv.setVisibility(View.GONE);
        }

        Query query = userDbRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    name = "" + ds.child("name").getValue();
                    email = "" + ds.child("email").getValue();
                    dp = "" + ds.child("image").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEd.getText().toString().trim();
                String description = descriptionEd.getText().toString().trim();

                if (TextUtils.isEmpty(title))
                {
                    Toast.makeText(AddPostActivity.this,"Enter title...",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(description))
                {
                    Toast.makeText(AddPostActivity.this,"Enter description...",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isUpdateKey.equals("editPost"))
                {
                    beginUpdate(title, description, editPostId);
                }
                else
                {
                    uploadData(title,description);
                }
            }
        });

        pDeleteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageIv.setImageDrawable(null);
                pDeleteImageBtn.setVisibility(View.GONE);
                imageIv.setVisibility(View.GONE);
            }
        });

        pAddImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkStoragePermission())
                {
                    requestStoragePermission();
                }
                else
                {
                    pickFromGallery();
                }
            }
        });
    }

    private void beginUpdate(String title, String description, String editPostId) {
        progressDialog.setMessage("Updating Post...");
        progressDialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(editPostId))
                {
                    if (!editImage.equals("noImage"))
                    {
                        if (imageIv.getDrawable() != null)
                        {
                            updateWasWithImage(title,description,editPostId);
                        }
                        else
                        {
                            updateWasWithImage_NowNoImage(title,description,editPostId,editImage);
                        }

                    }
                    else
                    {
                        if (imageIv.getDrawable() != null)
                        {
                            updateWithNowImage(title,description,editPostId);
                        }
                        else
                        {
                            updateWithoutImage(title,description,editPostId);
                        }

                    }
                }
                else
                {
                    progressDialog.dismiss();

                    View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_fail,null);

                    TextView OK = view.findViewById(R.id.OK);
                    TextView description = view.findViewById(R.id.textDesCription);

                    description.setText("Post not exist");

                    final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                    builder.setView(view);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    OK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            onBackPressed();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateWasWithImage_NowNoImage(String title, String description, String editPostId, String editImage) {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("uName",name);
        hashMap.put("uEmail",email);
        hashMap.put("uDp",dp);
        hashMap.put("pTitle",title);
        hashMap.put("pDescr",description);
        hashMap.put("pImage","noImage");

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(editImage);
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                reference.child(editPostId).updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();

                                View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_success,null);

                                TextView OK = view.findViewById(R.id.OK);
                                TextView description = view.findViewById(R.id.textDesCription);

                                description.setText("Your post already updated.");

                                final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                                builder.setView(view);

                                AlertDialog dialog = builder.create();
                                dialog.show();

                                OK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        onBackPressed();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();

                                View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_fail,null);

                                TextView OK = view.findViewById(R.id.OK);
                                TextView description = view.findViewById(R.id.textDesCription);

                                description.setText("Some error occur.");

                                final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                                builder.setView(view);

                                AlertDialog dialog = builder.create();
                                dialog.show();

                                OK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    private void updateWithoutImage(String title, String description, String editPostId) {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("uName",name);
        hashMap.put("uEmail",email);
        hashMap.put("uDp",dp);
        hashMap.put("pTitle",title);
        hashMap.put("pDescr",description);
        hashMap.put("pImage","noImage");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.child(editPostId).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();

                        View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_success,null);

                        TextView OK = view.findViewById(R.id.OK);
                        TextView description = view.findViewById(R.id.textDesCription);

                        description.setText("Your post already updated.");

                        final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                        builder.setView(view);

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        OK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                onBackPressed();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_fail,null);

                        TextView OK = view.findViewById(R.id.OK);
                        TextView description = view.findViewById(R.id.textDesCription);

                        description.setText("Some error occur.");

                        final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                        builder.setView(view);

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        OK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
    }

    private void updateWithNowImage(String title, String description, String editPostId) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Posts/" + "post_" + timeStamp;

        Bitmap bitmap = ((BitmapDrawable)imageIv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] data  =baos.toByteArray();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());

                        String dowmloadUri = uriTask.getResult().toString();
                        if (uriTask.isSuccessful())
                        {
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("uid",uid);
                            hashMap.put("uName",name);
                            hashMap.put("uEmail",email);
                            hashMap.put("uDp",dp);
                            hashMap.put("pTitle",title);
                            hashMap.put("pDescr",description);
                            hashMap.put("pImage",dowmloadUri);

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                            reference.child(editPostId).updateChildren(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();

                                            View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_success,null);

                                            TextView OK = view.findViewById(R.id.OK);
                                            TextView description = view.findViewById(R.id.textDesCription);

                                            description.setText("Your post already updated.");

                                            final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                                            builder.setView(view);

                                            AlertDialog dialog = builder.create();
                                            dialog.show();

                                            OK.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                    onBackPressed();
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();

                                            View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_fail,null);

                                            TextView OK = view.findViewById(R.id.OK);
                                            TextView description = view.findViewById(R.id.textDesCription);

                                            description.setText("Some error occur.");

                                            final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                                            builder.setView(view);

                                            AlertDialog dialog = builder.create();
                                            dialog.show();

                                            OK.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        //Toast.makeText(AddPostActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateWasWithImage(String title, String description, String editPostId) {
        StorageReference mPictureRef = FirebaseStorage.getInstance().getReferenceFromUrl(editImage);
        mPictureRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        String filePathAndName = "Posts/" + "post_" + timeStamp;

                        Bitmap bitmap = ((BitmapDrawable)imageIv.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
                        byte[] data  =baos.toByteArray();

                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                        ref.putBytes(data)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uriTask.isSuccessful());

                                        String dowmloadUri = uriTask.getResult().toString();
                                        if (uriTask.isSuccessful())
                                        {
                                            HashMap<String,Object> hashMap = new HashMap<>();
                                            hashMap.put("uid",uid);
                                            hashMap.put("uName",name);
                                            hashMap.put("uEmail",email);
                                            hashMap.put("uDp",dp);
                                            hashMap.put("pTitle",title);
                                            hashMap.put("pDescr",description);
                                            hashMap.put("pImage",dowmloadUri);

                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                                            reference.child(editPostId).updateChildren(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            progressDialog.dismiss();
                                                            View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_success,null);

                                                            TextView OK = view.findViewById(R.id.OK);
                                                            TextView description = view.findViewById(R.id.textDesCription);

                                                            description.setText("Your post already updated.");

                                                            final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                                                            builder.setView(view);

                                                            AlertDialog dialog = builder.create();
                                                            dialog.show();

                                                            OK.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    dialog.dismiss();
                                                                    onBackPressed();
                                                                }
                                                            });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog.dismiss();

                                                            View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_fail,null);

                                                            TextView OK = view.findViewById(R.id.OK);
                                                            TextView description = view.findViewById(R.id.textDesCription);

                                                            description.setText("Some error occur.");

                                                            final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                                                            builder.setView(view);

                                                            AlertDialog dialog = builder.create();
                                                            dialog.show();

                                                            OK.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                        }
                                                    });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_fail,null);

                                        TextView OK = view.findViewById(R.id.OK);
                                        TextView description = view.findViewById(R.id.textDesCription);

                                        description.setText("Some error occur.");

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                                        builder.setView(view);

                                        AlertDialog dialog = builder.create();
                                        dialog.show();

                                        OK.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_fail,null);

                        TextView OK = view.findViewById(R.id.OK);
                        TextView description = view.findViewById(R.id.textDesCription);

                        description.setText("Some error occur.");

                        final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                        builder.setView(view);

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        OK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
    }

    private void loadPostData(String editPostId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = reference.orderByChild("pId").equalTo(editPostId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    editTitle = "" + ds.child("pTitle").getValue();
                    editDescription = "" + ds.child("pDescr").getValue();
                    editImage = "" + ds.child("pImage").getValue();
                    editTimestamp ="" + ds.child("pTime").getValue();

                    titleEd.setText(editTitle);
                    descriptionEd.setText(editDescription);

                    if (!editImage.equals("noImage"))
                    {
                        try {
                            Picasso.get().load(editImage).into(imageIv);
                        }
                        catch (Exception e)
                        {

                        }
                    }
                    else
                    {
                        pDeleteImageBtn.setVisibility(View.GONE);
                        imageIv.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadData(String title, String description) {
        progressDialog.setMessage("Publishing post...");
        progressDialog.show();

        String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Posts/" + "post_" + timeStamp;

        if (imageIv.getDrawable() != null)
        {

            Bitmap bitmap = ((BitmapDrawable)imageIv.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
            byte[] data  =baos.toByteArray();

            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful())
                            {
                                HashMap<Object,String> hashMap = new HashMap<>();
                                hashMap.put("uid",uid);
                                hashMap.put("uName",name);
                                hashMap.put("uEmail",email);
                                hashMap.put("uDp",dp);
                                hashMap.put("pId",timeStamp);
                                hashMap.put("pTitle",title);
                                hashMap.put("pDescr",description);
                                hashMap.put("pImage",downloadUri);
                                hashMap.put("pLikes","0");
                                hashMap.put("pComments","0");
                                hashMap.put("pTime",timeStamp);


                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                                reference.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                //Toast.makeText(AddPostActivity.this,"Post published",Toast.LENGTH_SHORT).show();
                                                titleEd.setText("");
                                                descriptionEd.setText("");
                                                imageIv.setImageURI(null);
//                                                imageIv.setMinimumHeight(200);
//                                                imageIv.setMinimumHeight(200);
                                                image_uri = null;

                                                View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_success,null);

                                                TextView OK = view.findViewById(R.id.OK);
                                                TextView description = view.findViewById(R.id.textDesCription);

                                                description.setText("Your post published.");

                                                final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                                                builder.setView(view);

                                                AlertDialog dialog = builder.create();
                                                dialog.show();

                                                OK.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                        onBackPressed();
                                                    }
                                                });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_fail,null);

                                                TextView OK = view.findViewById(R.id.OK);
                                                TextView description = view.findViewById(R.id.textDesCription);

                                                description.setText("Some error occur.");

                                                final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                                                builder.setView(view);

                                                AlertDialog dialog = builder.create();
                                                dialog.show();

                                                OK.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_fail,null);

                            TextView OK = view.findViewById(R.id.OK);
                            TextView description = view.findViewById(R.id.textDesCription);

                            description.setText("Some error occur.");

                            final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                            builder.setView(view);

                            AlertDialog dialog = builder.create();
                            dialog.show();

                            OK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
        }
        else
        {
            HashMap<Object,String> hashMap = new HashMap<>();
            hashMap.put("uid",uid);
            hashMap.put("uName",name);
            hashMap.put("uEmail",email);
            hashMap.put("uDp",dp);
            hashMap.put("pId",timeStamp);
            hashMap.put("pTitle",title);
            hashMap.put("pDescr",description);
            hashMap.put("pImage","noImage");
            hashMap.put("pTime",timeStamp);
            hashMap.put("pLikes","0");
            hashMap.put("pComments","0");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
            reference.child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            //Toast.makeText(AddPostActivity.this,"Post published",Toast.LENGTH_SHORT).show();
                            titleEd.setText("");
                            descriptionEd.setText("");
                            imageIv.setImageURI(null);
                            image_uri = null;

                            View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_success,null);

                            TextView OK = view.findViewById(R.id.OK);
                            TextView description = view.findViewById(R.id.textDesCription);

                            description.setText("Your post published.");

                            final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                            builder.setView(view);

                            AlertDialog dialog = builder.create();
                            dialog.show();

                            OK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    onBackPressed();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            View view = LayoutInflater.from(AddPostActivity.this).inflate(R.layout.dialog_fail,null);

                            TextView OK = view.findViewById(R.id.OK);
                            TextView description = view.findViewById(R.id.textDesCription);

                            description.setText("Some error occur.");

                            final AlertDialog.Builder builder = new AlertDialog.Builder(AddPostActivity.this);
                            builder.setView(view);

                            AlertDialog dialog = builder.create();
                            dialog.show();

                            OK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
        }
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission()
    {
        boolean result = ContextCompat.checkSelfPermission(AddPostActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(AddPostActivity.this,storagePermissions,STORAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST_CODE)
        {
            if (grantResults.length > 0) {
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (writeStorageAccepted)
                {
                    pickFromGallery();
                }
                else
                {
                    Toast.makeText(AddPostActivity.this,"Please enable storage permission",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK)
        {

            if (requestCode == IMAGGE_PICK_GALLERY_CODE)
            {
                //pDeleteImageBtn.setVisibility(View.VISIBLE);
                //imageIv.setVisibility(View.VISIBLE);
                CropImage.activity(data.getData())
                        .start(this);
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                image_uri = result.getUri();

                if (image_uri!=null)
                {
                    imageIv.setImageURI(image_uri);
                    imageIv.setVisibility(View.VISIBLE);
                    pDeleteImageBtn.setVisibility(View.VISIBLE);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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