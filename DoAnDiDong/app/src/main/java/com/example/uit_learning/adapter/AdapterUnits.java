package com.example.uit_learning.adapter;

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

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("IsCompleted").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(unitList.get(position).getIdUnit());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(unitList.get(position).getId()))
                {
                    holder.status.setText("Completed");
                    holder.iconStatus.setImageResource(R.drawable.ic_check_completed);
                }
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
        TextView header, status;
        ImageView iconStatus;
        View itemView;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            header = itemView.findViewById(R.id.header);
            status = itemView.findViewById(R.id.status);
            iconStatus = itemView.findViewById(R.id.iconStatus);
        }
    }
}
