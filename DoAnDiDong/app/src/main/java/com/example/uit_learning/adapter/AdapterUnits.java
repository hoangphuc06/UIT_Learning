package com.example.uit_learning.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uit_learning.R;
import com.example.uit_learning.ViewPDFActivity;
import com.example.uit_learning.model.Unit;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.StringTokenizer;

public class AdapterUnits extends RecyclerView.Adapter<AdapterUnits.myviewholder>{


    Context context;
    List<Unit> unitList;

    public AdapterUnits(Context context, List<Unit> unitList) {
        this.context = context;
        this.unitList = unitList;
    }

    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_unit,parent,false);
        return new myviewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.header.setText(unitList.get(position).getFilename());

        String idUnit = unitList.get(position).getIdUnit();
        String typeUnit = unitList.get(position).getTypeUnit();
        String id = unitList.get(position).getId();

        int ammountQues;



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("IsCompleted").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(idUnit).child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String completed = "" + snapshot.child("completed").getValue();

                if (completed.equals("true"))
                {
                    holder.status.setText("Completed");
                    holder.iconStatus.setImageResource(R.drawable.ic_check_completed);
                }

                String score = null;
                boolean check = false;
                if (snapshot.hasChild("maxScorse"))
                {
                    score = snapshot.child("maxScorse").getValue(String.class);
                    check = true;
                }

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Courses").child(typeUnit).child(idUnit).child("Documents").child(id).child("Question");
                final boolean Check = check;
                final String sco = score;
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 0;
                        for (DataSnapshot ds : snapshot.getChildren())
                        {
                            count++;
                        }

                        if (Check == false)
                        {


                            holder.score.setText("Max score: 0/" + count);
                        }
                        else
                        {
                            holder.score.setText("Max score: " + sco + "/" + count);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(), ViewPDFActivity.class);
                intent.putExtra("filename",unitList.get(position).getFilename());
                intent.putExtra("fileurl",unitList.get(position).getFileurl());
                intent.putExtra("id",unitList.get(position).getId());
                intent.putExtra("idUnit",unitList.get(position).getIdUnit());
                intent.putExtra("typeUnit",unitList.get(position).getTypeUnit());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return unitList.size();
    }

    public class myviewholder extends RecyclerView.ViewHolder
    {
        TextView header, status, score;
        ImageView iconStatus;
        View itemView;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            header = itemView.findViewById(R.id.header);
            status = itemView.findViewById(R.id.status);
            iconStatus = itemView.findViewById(R.id.iconStatus);
            score = itemView.findViewById(R.id.score);
        }
    }
}
