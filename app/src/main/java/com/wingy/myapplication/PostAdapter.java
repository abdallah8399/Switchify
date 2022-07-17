package com.wingy.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter <PostAdapter.PostViewHolder>{
    private Context mContext;
    private List<Product> mProducts;
    private PostAdapter.OnItemClickListener mListener;

    public PostAdapter(Context context, List<Product> products){
        mContext= context;
        mProducts = products;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.product_post_item_layout, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, final int position) {
        holder.tvPostSeller.setText(mProducts.get(position).getSeller().getName());
        if(mProducts.get(position).getDescription().length()<50)
        holder.tvDescription.setText(mProducts.get(position).getDescription());
        else{
            String d= (mProducts.get(position).getDescription());
            holder.tvDescription.setText(d.subSequence(0,49)+"....");
        }

        if(! mProducts.get(position).getImageURL().isEmpty())
            Picasso.get().load(mProducts.get(position).getImageURL()).placeholder(R.drawable.ic_photo_black_24dp).into(holder.ivProductImage);

        if(! mProducts.get(position).getSeller().getImageURL().isEmpty())
            Picasso.get().load(mProducts.get(position).getSeller().getImageURL()).placeholder(R.drawable.ic_account_circle_black_24dp).fit().centerCrop().into(holder.ivSellerImage);

        if(mProducts.get(position).getSeller().getID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.btnChat.setVisibility(View.INVISIBLE);
        }


        final DatabaseReference imageReference= FirebaseDatabase.getInstance().getReference("Product Images");
        final DatabaseReference desRef= FirebaseDatabase.getInstance().getReference("Product Descriptions");


        final DatabaseReference finalContactsRef = FirebaseDatabase.getInstance().getReference("Contacts");
        holder.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String receiverUserID = mProducts.get(position).getSeller().getID(),
                        senderUserID=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    finalContactsRef.child(senderUserID).child(receiverUserID)
                            .child("Contacts")
                            .setValue("Saved")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        finalContactsRef.child(receiverUserID).child(senderUserID)
                                                .child("Contacts")
                                                .setValue("Saved");
                                    }

                                }
                            });

                final Intent intent= new Intent(mContext, ChatActivity.class);
                intent.putExtra("visit_user_id",mProducts.get(position).getSeller().getID());
                intent.putExtra("visit_user_name",mProducts.get(position).getSeller().getName());
                intent.putExtra("visit_image",mProducts.get(position).getSeller().getImageURL());

                imageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(mProducts.get(position).getSeller().getID())
                        .setValue(mProducts.get(position).getImageURL()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        imageReference.child(mProducts.get(position).getSeller().getID()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(mProducts.get(position).getImageURL()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                desRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(mProducts.get(position).getSeller().getID())
                                        .setValue(mProducts.get(position).getDescription()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        desRef.child(mProducts.get(position).getSeller().getID()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(mProducts.get(position).getDescription()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mContext.startActivity(intent);

                                            }
                                        });
                                    }
                                });



                            }
                        });
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }


    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvPostSeller, tvDescription;
        public ImageView ivProductImage;
        public CircleImageView ivSellerImage;
        public Button  btnChat;

        public PostViewHolder(View itemView){
            super(itemView);
            tvPostSeller= itemView.findViewById(R.id.item_tvHomeUserName);
            tvDescription= itemView.findViewById(R.id.item_tvDescription);
            ivProductImage= itemView.findViewById(R.id.item_ivHomeProductImage);
            ivSellerImage= itemView.findViewById(R.id.item_circleUserImage);
            btnChat= itemView.findViewById(R.id.item_btnChat);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mListener!=null){
                int position= getAdapterPosition();
                if(position!= RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(PostAdapter.OnItemClickListener listener){
        mListener= listener;
    }


}
