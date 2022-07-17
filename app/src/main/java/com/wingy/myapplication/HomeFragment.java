package com.wingy.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements PostAdapter.OnItemClickListener{
    private DatabaseReference postsRef, userPostsRef;
    private RecyclerView mRecyclerView;
    private PostAdapter mAdapter;
    private List<Product> mProducts;
    private ArrayList<String> userTypes, positionKeys;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        postsRef= FirebaseDatabase.getInstance().getReference("Posts");
        userPostsRef= FirebaseDatabase.getInstance().getReference("Users Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mRecyclerView = view.findViewById(R.id.home_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mProducts= new ArrayList<>();
        userTypes= new ArrayList<>();
        positionKeys= new ArrayList<>();


        userPostsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    Product product= d.getValue(Product.class);
                    userTypes.add(product.getType());
                }

                postsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mProducts.clear();
                        for (DataSnapshot d: dataSnapshot.getChildren()){
                            Product product= d.getValue(Product.class);
                            if(userTypes.contains(product.getType())) {
                                mProducts.add(product);
                                positionKeys.add(d.getKey());
                            }
                            }
                        Collections.reverse(mProducts);
                        Collections.reverse(positionKeys);
                        mAdapter= new PostAdapter(getContext(), mProducts);
                        mAdapter.setOnClickListener(HomeFragment.this);
                        mRecyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onItemClick(int position) {
        String s= positionKeys.get(position);
        Intent intent= new Intent(getContext(), ShowProductActivity.class);
        intent.putExtra("positionKey", s);
        startActivity(intent);
    }
}
