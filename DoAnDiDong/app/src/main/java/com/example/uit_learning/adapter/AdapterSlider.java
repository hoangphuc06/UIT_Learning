package com.example.uit_learning.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.uit_learning.R;
import com.example.uit_learning.model.Slider;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterSlider extends SliderViewAdapter<AdapterSlider.MyHolder> {

    List<Slider> sliders;

    public AdapterSlider(List<Slider> sliders) {
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

    }

    @Override
    public int getCount() {
        return sliders.size();
    }

    public class MyHolder extends SliderViewAdapter.ViewHolder {

        ImageView imageView;

        public MyHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }

        void setImage(Slider slider)
        {
            Picasso.get().load(slider.getImage()).into(imageView);
        }
    }
}
