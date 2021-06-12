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
import com.example.uit_learning.model.PDF;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class AdapterUnits extends FirebaseRecyclerAdapter<PDF,AdapterUnits.myviewholder>{


    public AdapterUnits(@NonNull FirebaseRecyclerOptions<PDF> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull  AdapterUnits.myviewholder holder, int position, @NonNull  PDF model) {
        holder.header.setText(model.getFilename());
        holder.imgopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.imgopen.getContext(), ViewPDFActivity.class);
                intent.putExtra("filename",model.getFilename());
                intent.putExtra("fileurl",model.getFileurl());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.imgopen.getContext().startActivity(intent);
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
        ImageView imgopen;
        TextView header;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            imgopen = itemView.findViewById(R.id.btnopen);
            header = itemView.findViewById(R.id.header);
        }
    }
}
