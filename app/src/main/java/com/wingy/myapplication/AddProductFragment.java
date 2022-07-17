package com.wingy.myapplication;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class AddProductFragment extends Fragment {

    private ImageView addImage;
    private EditText etName, etDescription;
    private Button btnAddPost;
    private Uri uri;
    private String imageURL, sellerID;
    private StorageReference mStorageRef;
    private DatabaseReference productsDataRef, usersRef, usersPosts;
    private User currentUser;
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> productTypeAdapter;
    private Spinner spinner;
    private ArrayList<String> typeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view= inflater.inflate(R.layout.fragment_add_product, container, false);

        spinner= view.findViewById(R.id.spProductType);
        addImage= view.findViewById(R.id.ivAddProductImage);
        etName= view.findViewById(R.id.etProductName);
        etDescription= view.findViewById(R.id.etDescription);
        btnAddPost= view.findViewById(R.id.btnAddProduct);
        progressDialog= new ProgressDialog(getContext());
        progressDialog.setMessage("posting...");
        mStorageRef= FirebaseStorage.getInstance().getReference("Images");
        productsDataRef= FirebaseDatabase.getInstance().getReference("Posts");
        usersRef= FirebaseDatabase.getInstance().getReference("Users");
        usersPosts= FirebaseDatabase.getInstance().getReference("Users Posts");

        typeList= new ArrayList<>();
        typeList.add("Mobiles");
        typeList.add("Laptops");
        typeList.add("Books");
        typeList.add("other");

        productTypeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, typeList);
        productTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(productTypeAdapter);

        sellerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        GetCurrentUser(sellerID);


        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 10);
            }
        });

        btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString(),
                        description = etDescription.getText().toString(),
                        typeString= spinner.getSelectedItem().toString();
                if(name.isEmpty())
                    Toast.makeText(getContext(), "Enter a name", Toast.LENGTH_LONG).show();
                else if(description.isEmpty())
                    Toast.makeText(getContext(), "Enter a description", Toast.LENGTH_LONG).show();
                else if (typeString.isEmpty())
                    Toast.makeText(getContext(), "Select the type", Toast.LENGTH_LONG).show();
                else if(imageURL.isEmpty())
                    Toast.makeText(getContext(), "Upload an image", Toast.LENGTH_SHORT).show();
                else {
                    final Product product= new Product(name, description, imageURL, typeString, currentUser);
                    progressDialog.show();
                    final String time= String.valueOf(System.currentTimeMillis())+"_"+sellerID;

                    productsDataRef.child(time).setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            usersPosts.child(sellerID).child(time).setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "posted successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                }
            }
        });

        return view;

    }

    private void GetCurrentUser(String id){
        usersRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              currentUser =  dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                currentUser= new User();
            }
        });
    }

    private String getExtension(Uri u){
        ContentResolver cr= getActivity().getContentResolver();
        MimeTypeMap m= MimeTypeMap.getSingleton();
        return m.getExtensionFromMimeType(cr.getType(u));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri= data.getData();

            Picasso.get().load(uri).fit().centerCrop().into(addImage);

            Toast.makeText(getContext(), "Uploading...", Toast.LENGTH_SHORT).show();

            final StorageReference storageReference= mStorageRef.child(System.currentTimeMillis()+"."+ getExtension(uri));
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Image Uploaded!", Toast.LENGTH_SHORT).show();
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageURL= uri.toString();
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
