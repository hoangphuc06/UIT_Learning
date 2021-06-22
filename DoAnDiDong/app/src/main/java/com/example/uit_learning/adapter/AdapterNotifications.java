package com.example.uit_learning.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uit_learning.AddPostActivity;
import com.example.uit_learning.NotificationsActivity;
import com.example.uit_learning.PostDetailActivity;
import com.example.uit_learning.R;
import com.example.uit_learning.model.Notification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterNotifications extends RecyclerView.Adapter<AdapterNotifications.HolderNotification> {

    private Context context;
    private List<Notification> notificationList;

    FirebaseAuth firebaseAuth;

    public AdapterNotifications(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_notification,parent,false);
        return new HolderNotification(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderNotification holder, int position) {
        Notification model = notificationList.get(position);

        String name = model.getsName();
        String notification = model.getNotification();
        String image = model.getsImage();
        String timestamp = model.getTimestamp();
        String senderUid = model.getsUid();
        String pId = model.getpId();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(senderUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren())
                        {
                            String name = "" + ds.child("name").getValue();
                            String image = "" + ds.child("image").getValue();
                            String email = "" + ds.child("email").getValue();

                            holder.nameTv.setText(name);

                            try {
                                Picasso.get().load(image).placeholder(R.drawable.ic_image_default).into(holder.avatarIv);
                            }
                            catch (Exception e)
                            {
                                holder.avatarIv.setImageResource(R.drawable.ic_image_default);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.notificationTv.setText(notification);
        holder.timeTv.setText(pTime);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Posts");
                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(pId).exists())
                        {
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
                            reference1.child(firebaseAuth.getUid()).child("Notifications").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(timestamp))
                                    {
                                        Activity activity = (Activity)context;
                                        Intent intent = new Intent(context, PostDetailActivity.class);
                                        intent.putExtra("postId",pId);
                                        activity.startActivity(intent);
                                        activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                                    }
                                    else
                                    {
                                        View view = LayoutInflater.from(context).inflate(R.layout.dialog_error404,null);

                                        TextView OK = view.findViewById(R.id.OK);
                                        TextView description = view.findViewById(R.id.textDesCription);

                                        description.setText("Notification not exist.");

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        else
                        {
                            View view = LayoutInflater.from(context).inflate(R.layout.dialog_error404,null);

                            TextView OK = view.findViewById(R.id.OK);
                            TextView description = view.findViewById(R.id.textDesCription);

                            description.setText("Post not exist.");

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this notification?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
                        reference1.child(firebaseAuth.getUid()).child("Notifications").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(timestamp))
                                {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                    ref.child(firebaseAuth.getUid()).child("Notifications").child(timestamp).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(context,"Notification Deleted...",Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                                else
                                {
                                    View view = LayoutInflater.from(context).inflate(R.layout.dialog_error404,null);

                                    TextView OK = view.findViewById(R.id.OK);
                                    TextView description = view.findViewById(R.id.textDesCription);

                                    description.setText("Notification not exist.");

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class HolderNotification extends RecyclerView.ViewHolder {

        ImageView avatarIv;
        TextView nameTv, notificationTv, timeTv;

        public HolderNotification(@NonNull View itemView) {
            super(itemView);

            avatarIv = itemView.findViewById(R.id.avatarIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            notificationTv = itemView.findViewById(R.id.notificationTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }
}
