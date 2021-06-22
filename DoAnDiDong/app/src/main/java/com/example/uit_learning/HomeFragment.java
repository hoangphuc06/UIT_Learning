package com.example.uit_learning;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uit_learning.adapter.AdapterSlider;
import com.example.uit_learning.adapter.CourseRecyclerAdapter;
import com.example.uit_learning.model.Course;
import com.example.uit_learning.model.Slider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.example.uit_learning.utils.*;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

public class HomeFragment extends Fragment {

    private static final FirebaseDatabase root = FirebaseDatabase.getInstance();

    private List<Course> courses_101;
    private DatabaseReference reference_101;

    private List<Course> courses_foundation;
    private DatabaseReference reference_foundation;

    // RecyclerView manager
    RecyclerView  recyclerView_101;
    RecyclerView recyclerView_foundation;
    RecyclerView.Adapter adapter;

    SliderView sliderView;
    List<Slider> sliderList;
    AdapterSlider adapterSlider;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reference_101 = root.getReference("Courses/Outline");
        courses_101 = new ArrayList<>();

        reference_foundation = root.getReference("Courses/Foundation");
        courses_foundation = new ArrayList<>();

        importDataCourses101();
        importDataCourseFoundation();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        // 101
        recyclerView_101 = view.findViewById(R.id.recycler_outline_courses);
        recyclerView_101.setLayoutManager(new LinearLayoutManager(view.getContext(),
                RecyclerView.HORIZONTAL, false));
        recyclerView_101.addItemDecoration(new SpacingItemDecorator(40));
        // Foundation
        recyclerView_foundation = view.findViewById(R.id.recycler_foundation_courses);
        recyclerView_foundation.setLayoutManager(new LinearLayoutManager(view.getContext(),
                RecyclerView.HORIZONTAL, false));
        recyclerView_foundation.addItemDecoration(new SpacingItemDecorator(40));

        // Load Slider
        sliderView = view.findViewById(R.id.imageSlider);
        sliderList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Slider");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sliderList.clear();
                for (DataSnapshot ds: snapshot.getChildren())
                {
                    Slider slider = ds.getValue(Slider.class);

                    sliderList.add(slider);

                    adapterSlider = new AdapterSlider(sliderList);

                    sliderView.setSliderAdapter(adapterSlider);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        AdapterSlider adapterSlider = new AdapterSlider(sliderList);

        sliderView.setSliderAdapter(adapterSlider);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();

        return view;
    }

    private void importDataCourses101() {
        reference_101.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courses_101.clear();
                for(DataSnapshot snap : snapshot.getChildren()) {
                    courses_101.add(snap.getValue(Course.class));
                }
                adapter = new CourseRecyclerAdapter(courses_101);
                recyclerView_101.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void importDataCourseFoundation() {
        reference_foundation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courses_foundation.clear();
                for(DataSnapshot snap : snapshot.getChildren()) {
                    courses_foundation.add(snap.getValue(Course.class));
                }
                adapter = new CourseRecyclerAdapter(courses_foundation);
                recyclerView_foundation.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}