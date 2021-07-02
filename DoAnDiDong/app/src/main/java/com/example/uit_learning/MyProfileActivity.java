package com.example.uit_learning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uit_learning.Common.NetworkChangeListener;
import com.example.uit_learning.adapter.AdapterPosts;
import com.example.uit_learning.model.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.theartofdev.edmodo.cropper.CropImage.*;

public class MyProfileActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = null;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    String storagePath = "Users_Profile_Cover_Imgs/";

    ImageView avatarIv, coverIv;
    TextView nameTv, emailTv;
    FloatingActionButton fab;

    ProgressDialog progressDialog;

    private static final int STORAGE_REQUEST_CODE = 100;
    private static final int IMAGGE_PICK_GALLERY_CODE = 200;

    String storagePermissions[];

    String profileOrCoverPhoto;

    Uri image_uri;

    RecyclerView recyclerView;
    List<Post> postList;
    AdapterPosts adapterPosts;

    Toolbar toolbar;
    TextView textToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        broadcastReceiver = new NetworkChangeListener();
        CheckInternet();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textToolbar = findViewById(R.id.textTollbar);
        textToolbar.setText("My profile");

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        avatarIv = findViewById(R.id.avatarIv);
        coverIv = findViewById(R.id.coverIv);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        fab = findViewById(R.id.fap);

        recyclerView = findViewById(R.id.postRecycleViewProfile);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyProfileActivity.this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);

        postList = new ArrayList<>();

        loadPosts();

        progressDialog = new ProgressDialog(MyProfileActivity.this);

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();

                    nameTv.setText(name);
                    emailTv.setText(email);
                    try
                    {
                        Picasso.get().load(image).into(avatarIv);
                    }
                    catch (Exception e)
                    {
                        //Picasso.get().load(ic_add_image).into(avatarIv);
                    }
                    try
                    {
                        Picasso.get().load(cover).into(coverIv);
                    }
                    catch (Exception e)
                    {

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        //CropImage.activity().setAspectRatio(1,1).start(MyProfileActivity.this);

    }

    private boolean checkStoragePermission()
    {
        boolean result = ContextCompat.checkSelfPermission(MyProfileActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission()
    {
        requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);
    }

    private void showEditProfileDialog() {
//        String options[] = {"Edit Profile Picture","Edit Cover Photo", "Edit Name"};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
//        builder.setTitle("Choose Action");
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == 0)
//                {
//                    progressDialog.setMessage("Updating Profile Picture...");
//                    profileOrCoverPhoto = "image";
//                    if (!checkStoragePermission())
//                    {
//                        requestStoragePermission();
//                    }
//                    else
//                    {
//                        pickFromGallery();
//                    }
//                }
//                else if (which == 1)
//                {
//                    progressDialog.setMessage("Updating Cover Photo...");
//                    profileOrCoverPhoto = "cover";
//                    if (!checkStoragePermission())
//                    {
//                        requestStoragePermission();
//                    }
//                    else
//                    {
//                        pickFromGallery();
//                    }
//                }
//                else if (which == 2)
//                {
//                    progressDialog.setMessage("Updating Name...");
//                    showNameUpdateDialog();
//                }
//            }
//        });
//        builder.create().show();
        View view = LayoutInflater.from(MyProfileActivity.this).inflate(R.layout.dialog_profile,null);

        LinearLayout layoutAvatar = view.findViewById(R.id.layoutAvatar);
        LinearLayout layoutCover = view.findViewById(R.id.layoutCover);
        LinearLayout layoutName = view.findViewById(R.id.layoutName);

        final AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        layoutAvatar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Updating Profile Picture...");
                profileOrCoverPhoto = "image";
                if (!checkStoragePermission())
                {
                    requestStoragePermission();
                }
                else
                {
                    pickFromGallery();
                }
                dialog.dismiss();
            }
        });

        layoutCover.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Updating Cover Photo...");
                profileOrCoverPhoto = "cover";
                if (!checkStoragePermission())
                {
                    requestStoragePermission();
                }
                else
                {
                    pickFromGallery();
                }
                dialog.dismiss();
            }
        });

        layoutName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Updating Name...");
                EditName();
                dialog.dismiss();
            }
        });

    }

    private void EditName()
    {
        View view = LayoutInflater.from(MyProfileActivity.this).inflate(R.layout.dialog_editname,null);

        TextInputLayout textNewName = view.findViewById(R.id.textNewName);
        TextView Ok = view.findViewById(R.id.OK);
        TextView Cancel = view.findViewById(R.id.CANCEL);

        final AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textNewName.setErrorEnabled(false);
                String value = textNewName.getEditText().getText().toString().trim();
                if (!TextUtils.isEmpty(value))
                {
                    progressDialog.show();
                    HashMap<String,Object> result = new HashMap<>();
                    result.put("name",value);
                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    dialog.dismiss();
                                    Toast.makeText(MyProfileActivity.this,"Updated...",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    dialog.dismiss();
                                    Toast.makeText(MyProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else
                {
                    textNewName.setError("Please enter your new name");
                    textNewName.setFocusable(true);
                    textNewName.setErrorIconDrawable(null);
                }
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (writeStorageAccepted) {
                    pickFromGallery();
                } else {
                    Toast.makeText(MyProfileActivity.this, "Please enable storage permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK)
        {
            if (requestCode ==IMAGGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData())
                        .start(this);
            }

            if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                image_uri = result.getUri();

                if (profileOrCoverPhoto == "image")
                {
                    View view = LayoutInflater.from(MyProfileActivity.this).inflate(R.layout.dialog_avatar,null);

                    TextView OK = view.findViewById(R.id.OK);
                    TextView CANCEL = view.findViewById(R.id.CANCEL);
                    CircleImageView avatarIv = view.findViewById(R.id.avatarIv);

                    avatarIv.setImageURI(image_uri);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                    builder.setView(view);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    OK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            uploadProfileCoverPhoto(image_uri);
                        }
                    });

                    CANCEL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
                else
                {
                    View view = LayoutInflater.from(MyProfileActivity.this).inflate(R.layout.dialog_cover,null);

                    TextView OK = view.findViewById(R.id.OK);
                    TextView CANCEL = view.findViewById(R.id.CANCEL);
                    ImageView coverIv = view.findViewById(R.id.avatarIv);

                    coverIv.setImageURI(image_uri);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                    builder.setView(view);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    OK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            uploadProfileCoverPhoto(image_uri);
                        }
                    });

                    CANCEL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }


            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri) {
        progressDialog.show();

        String filePathAndName = storagePath + "" + profileOrCoverPhoto + "_" + user.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();

                if (uriTask.isSuccessful())
                {
                    HashMap<String,Object> results = new HashMap<>();
                    results.put(profileOrCoverPhoto,downloadUri.toString());

                    databaseReference.child(user.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(MyProfileActivity.this,"Image Updated...",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MyProfileActivity.this,"Error Updating Image...",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(MyProfileActivity.this,"Some error occured",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MyProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickFromGallery() {
       Intent galleryIntent = new Intent(Intent.ACTION_PICK);
       galleryIntent.setType("image/*");
       startActivityForResult(galleryIntent,IMAGGE_PICK_GALLERY_CODE);
//        CropImage.activity()
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .start(this);
    }

    private void loadPosts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        Query query = ref.orderByChild("uid").equalTo(user.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    Post post = ds.getValue(Post.class);

                    postList.add(post);

                    adapterPosts = new AdapterPosts(MyProfileActivity.this,postList);

                    recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyProfileActivity.this,"" + error.getMessage(),Toast.LENGTH_SHORT).show();
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