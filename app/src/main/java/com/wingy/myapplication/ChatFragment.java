package com.wingy.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

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


public class ChatFragment extends Fragment implements ChatListAdapter.OnItemClickListener{
    private DatabaseReference contactsRef, usersRef;
    private RecyclerView mRecyclerView;
    private ChatListAdapter mAdapter;
    private List<User> mUsers;
    private ArrayList<String> IDs;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);


        contactsRef= FirebaseDatabase.getInstance().getReference("Contacts");
        usersRef= FirebaseDatabase.getInstance().getReference("Users");

        mRecyclerView = view.findViewById(R.id.chats_list_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers= new ArrayList<>();
        IDs= new ArrayList<>();

            contactsRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    IDs.clear();
                    for(DataSnapshot d:dataSnapshot.getChildren()){
                       // if(d.child("Contacts").getValue().toString().equals("Saved"))
                            IDs.add(d.getKey());
                    }

                    mUsers.clear();
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot d: dataSnapshot.getChildren()){
                                if(IDs.contains(d.getKey())){
                                    User user= d.getValue(User.class);
                                    mUsers.add(user);
                                }
                            }

                            mAdapter= new ChatListAdapter(getContext(), mUsers);
                            mAdapter.setOnClickListener(ChatFragment.this);
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
        Intent intent= new Intent(getContext(), ChatActivity.class);
        User user= mUsers.get(position);
        intent.putExtra("visit_user_id",user.getID());
        intent.putExtra("visit_user_name",user.getName());
        intent.putExtra("visit_image",user.getImageURL());
        startActivity(intent);

    }
}