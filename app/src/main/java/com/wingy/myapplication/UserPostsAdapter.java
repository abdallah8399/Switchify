package com.wingy.myapplication;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;


public class UserPostsAdapter  extends RecyclerView.Adapter <UserPostsAdapter.UserPostsViewHolder>{
    private Context mContext;
    private List<Product> mProducts;
    private UserPostsAdapter.OnItemClickListener mListener;



    public UserPostsAdapter(Context context, List<Product> products){

        mContext= context;
        mProducts = products;
    }

    @NonNull
    @Override
    public UserPostsAdapter.UserPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_my_projects, parent, false);
        return new UserPostsAdapter.UserPostsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostsAdapter.UserPostsViewHolder holder, int position) {

        if(! mProducts.get(position).getImageURL().isEmpty())
            Picasso.get().load(mProducts.get(position).getImageURL()).placeholder(R.drawable.ic_photo_black_24dp).fit().into(holder.ivProductImage);

    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public class UserPostsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivProductImage;

        public UserPostsViewHolder(View itemView){
            super(itemView);
            ivProductImage= itemView.findViewById(R.id.item_my_projects);
            ivProductImage.getLayoutParams().width= MainActivity.width/3;
            ivProductImage.getLayoutParams().height= MainActivity.width/3;

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

    public void setOnClickListener(UserPostsAdapter.OnItemClickListener listener){
        mListener= listener;
    }
}
