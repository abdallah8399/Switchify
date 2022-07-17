package com.wingy.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements UserPostsAdapter.OnItemClickListener{
    private DatabaseReference projectsRef, usersRef;
    private RecyclerView mRecyclerView;
    private UserPostsAdapter mAdapter;
    private List<Product> mProducts;
    private String sellerID;
    private User currentUser;
    private TextView tvName, tvPostsNumber;
    private CircleImageView profileImage;
    private Button btnEditProfile,profile_btnLogOut;
    private ArrayList<String> positionKeys;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        projectsRef= FirebaseDatabase.getInstance().getReference("Users Posts").child(FirebaseAuth.getInstance().getUid());
        usersRef= FirebaseDatabase.getInstance().getReference("Users");

        tvName = view.findViewById(R.id.profile_userName);
        profileImage = view.findViewById(R.id.profile_circle_image);
        btnEditProfile = view.findViewById(R.id.profile_btnEditProfile);
        tvPostsNumber = view.findViewById(R.id.profile_tvPostsNumber);
        profile_btnLogOut = view.findViewById(R.id.profile_btnLogOut);


        mRecyclerView = view.findViewById(R.id.profile_recycler);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager= new GridLayoutManager(getContext(),3);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mProducts= new ArrayList<>();
        positionKeys= new ArrayList<>();

        sellerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        GetCurrentUser(sellerID);


        profile_btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Confirmation");
                builder.setMessage("Do you want to log out?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        dialog.dismiss();
                        FirebaseAuth.getInstance().signOut();
                        getActivity().finish();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);

            }
        });




        projectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tvPostsNumber.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Product product = d.getValue(Product.class);
                    mProducts.add(product);
                    positionKeys.add(d.getKey());
                }
                    mAdapter = new UserPostsAdapter(getContext(), mProducts);
                    mAdapter.setOnClickListener(ProfileFragment.this);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }

    private void GetCurrentUser(String id){
        usersRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser =  dataSnapshot.getValue(User.class);
                tvName.setText(currentUser.getName());
                Picasso.get().load(currentUser.getImageURL()).placeholder(R.drawable.ic_account_circle_black_24dp).fit().centerCrop().into(profileImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                currentUser= new User();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent= new Intent(getContext(), ShowProductActivity.class);
        intent.putExtra("positionKey", positionKeys.get(position));
        startActivity(intent);

    }
}
