package com.example.uit_learning;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.uit_learning.adapter.AdapterPosts;
import com.example.uit_learning.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ForumFragment extends Fragment {

    FirebaseAuth firebaseAuth;

    RecyclerView recyclerView;
    List<Post> postList;
    AdapterPosts adapterPosts;

    public ForumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forum, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.postRecycleView);
        recyclerView.setItemViewCacheSize(10);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        setHasOptionsMenu(true);

        recyclerView.setLayoutManager(layoutManager);

        postList = new ArrayList<>();

        loadPosts();

        return view;
    }

    private void loadPosts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    Post post = ds.getValue(Post.class);

                    postList.add(post);

                    adapterPosts = new AdapterPosts(getActivity(),postList);

                    recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPosts(String searchQuery)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    Post post = ds.getValue(Post.class);

                    if (post.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) || post.getpDescr().toLowerCase().contains(searchQuery.toLowerCase()))
                    {
                        postList.add(post);
                    }

                    adapterPosts = new AdapterPosts(getActivity(),postList);

                    recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main,menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query))
                {
                    searchPosts(query);
                }
                else
                {
                    loadPosts();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText))
                {
                    searchPosts(newText);
                }
                else
                {
                    loadPosts();
                }
                return true;
            }
        });

        MenuItem item1 = menu.findItem(R.id.action_add_post);
        item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getContext(),AddPostActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                return false;
            }
        });

        MenuItem item2 = menu.findItem(R.id.action_open_notification);
        item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getContext(), NotificationsActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

}