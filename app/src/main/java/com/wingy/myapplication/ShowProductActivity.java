package com.wingy.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ShowProductActivity extends AppCompatActivity {
    private DatabaseReference productRef, userProductRef;
    private ImageView image;
    private TextView name, description;
    private ImageButton deletePost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);
        DisplayMetrics displayMetrics= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width= displayMetrics.widthPixels;
        int height= displayMetrics.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.8));

        image= findViewById(R.id.ivShowProduct);
        name= findViewById(R.id.tvShowProductName);
        deletePost= findViewById(R.id.deletePost);
        description= findViewById(R.id.tvShowProductDescription);

        final String positionKey= getIntent().getStringExtra("positionKey");
        productRef= FirebaseDatabase.getInstance().getReference("Posts").child(positionKey);
        userProductRef= FirebaseDatabase.getInstance().getReference("Users Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(positionKey);

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Product product= dataSnapshot.getValue(Product.class);
                    Picasso.get().load(product.getImageURL()).fit().into(image);
                    User u= product.getSeller();
                    if(u.getID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        deletePost.setVisibility(View.VISIBLE);
                    else
                        deletePost.setVisibility(View.INVISIBLE);
                    name.setText(product.getName());
                    description.setText(product.getDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        userProductRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ShowProductActivity.this.finish();

                            }
                        });
                    }
                });
            }
        });

    }
}
