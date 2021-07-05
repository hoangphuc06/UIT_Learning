package com.example.uit_learning.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.uit_learning.R;
import com.example.uit_learning.model.Slider;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterSlider extends SliderViewAdapter<AdapterSlider.MyHolder> {

    Context context;
    List<Slider> sliders;

    public AdapterSlider(Context context, List<Slider> sliders) {
        this.context = context;
        this.sliders = sliders;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_slider_layout_item,parent,false);

        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(MyHolder viewHolder, int position) {

        viewHolder.setImage(sliders.get(position));

        viewHolder.textView.setText(sliders.get(position).getTitle());

        String des = sliders.get(position).getDescription();

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!des.equals("0"))
                {
                    Activity activity = (Activity)context;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(des));
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }

            }
        });

    }

    @Override
    public int getCount() {
        return sliders.size();
    }

    public class MyHolder extends SliderViewAdapter.ViewHolder {

        ImageView imageView;
        TextView textView;

        public MyHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.titleSliderTv);
        }

        void setImage(Slider slider)
        {
            Picasso.get().load(slider.getImage()).into(imageView);
        }
    }
}
