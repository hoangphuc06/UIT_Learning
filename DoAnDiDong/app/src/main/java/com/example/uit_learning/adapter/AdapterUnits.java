package com.example.uit_learning.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uit_learning.R;
import com.example.uit_learning.ViewPDFActivity;
import com.example.uit_learning.model.Unit;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class AdapterUnits extends FirebaseRecyclerAdapter<Unit,AdapterUnits.myviewholder>{


    public AdapterUnits(@NonNull FirebaseRecyclerOptions<Unit> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull  AdapterUnits.myviewholder holder, int position, @NonNull Unit model) {
        holder.header.setText(model.getFilename());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(), ViewPDFActivity.class);
                intent.putExtra("filename",model.getFilename());
                intent.putExtra("fileurl",model.getFileurl());
                intent.putExtra("id",model.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_unit,parent,false);
        return new myviewholder(view);
    }

    public class myviewholder extends RecyclerView.ViewHolder
    {
        TextView header;
        View itemView;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            header = itemView.findViewById(R.id.header);
        }
    }
}
