package com.example.uit_learning;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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

public class SettingsFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    ProgressDialog progressDialog;

    TextView nameTv;
    ImageView avatarIv;
    LinearLayout layoutProfile, layoutLogpout, layoutChangePassword;

    RecyclerView recyclerView;

    Switch notificationSwitch;
    SharedPreferences preferences;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setHasOptionsMenu(true);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        nameTv = view.findViewById(R.id.nameTv);
        avatarIv = view.findViewById(R.id.avatarIv);
        layoutProfile = view.findViewById(R.id.layoutProfile);

        progressDialog = new ProgressDialog(getActivity());

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

                    try
                    {
                        Picasso.get().load(image).into(avatarIv);
                    }
                    catch (Exception e)
                    {
                        //Picasso.get().load(ic_add_image).into(avatarIv);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MyProfileActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        recyclerView = view.findViewById(R.id.recycle_result);

        layoutLogpout = view.findViewById(R.id.layoutLogout);

        layoutLogpout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_logout,null);

                TextView OK = view.findViewById(R.id.OK);
                TextView CANCEL = view.findViewById(R.id.CANCEL);


                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(view);

                AlertDialog dialog = builder.create();
                dialog.show();

                OK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        firebaseAuth.signOut();
                        getActivity().startActivity(new Intent(getActivity(),LoginActivity.class));
                        getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        getActivity().finish();
                    }
                });

                CANCEL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);

        layoutChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(),ChangePasswordActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        notificationSwitch = view.findViewById(R.id.notification_switch);
        preferences = getContext().getApplicationContext().getSharedPreferences("NOTIFICATION_PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("NOTIFICATION_ENABLE", false);
        editor.commit();

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                if (isChecked) {
                    editor.putBoolean("NOTIFICATION_ENABLE", true);
                } else {
                    editor.putBoolean("NOTIFICATION_ENABLE", false);
                }
                editor.commit();
            }
        });

        return view;
    }


}