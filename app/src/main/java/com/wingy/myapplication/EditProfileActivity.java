package com.wingy.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private EditText etName, etCurrentPass, etNewPass;
    private Button btnSave;
    private DatabaseReference usersRef;
    private StorageReference mStorageRef;
    private Uri uri;
    private String imageURL;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        profileImage= findViewById(R.id.editProfile_image);
        etName= findViewById(R.id.editProfile_Name);
        etCurrentPass= findViewById(R.id.editProfile_CurrentPassword);
        etNewPass= findViewById(R.id.editProfile_newPassword);
        btnSave= findViewById(R.id.editProfile_btnSave);

        mStorageRef= FirebaseStorage.getInstance().getReference("Images");
        usersRef= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user= dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getImageURL()).centerCrop().fit().into(profileImage);
                etName.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 12);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(! imageURL.isEmpty())
                    user.setImageURL(imageURL);
                if(etCurrentPass.getText().toString().isEmpty())
                    Toast.makeText(EditProfileActivity.this, "Enter the current password", Toast.LENGTH_SHORT).show();
                else if(! etCurrentPass.getText().toString().equals(user.getPassword()))
                    Toast.makeText(EditProfileActivity.this, "wrong password", Toast.LENGTH_SHORT).show();
                else {
                    if(! etNewPass.getText().toString().isEmpty()){
                        if(etNewPass.getText().toString().length()<8)
                            Toast.makeText(EditProfileActivity.this, "enter a password 8 characters or more", Toast.LENGTH_SHORT).show();
                        else{

                            user.setPassword(etNewPass.getText().toString());
                            FirebaseAuth.getInstance().getCurrentUser().updatePassword(etNewPass.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    SaveData(user);
                                }
                            });

                        }
                    }else SaveData(user);


                }

            }
        });


    }

    private void SaveData(User user1){
        usersRef.setValue(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditProfileActivity.this, "Saved..", Toast.LENGTH_SHORT).show();
                EditProfileActivity.this.finish();
            }
        });

    }

    private String getExtension(Uri u){
        ContentResolver cr= getContentResolver();
        MimeTypeMap m= MimeTypeMap.getSingleton();
        return m.getExtensionFromMimeType(cr.getType(u));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri= data.getData();

            Picasso.get().load(uri).fit().centerCrop().into(profileImage);

            Toast.makeText(EditProfileActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();

            final StorageReference storageReference= mStorageRef.child(System.currentTimeMillis()+"."+ getExtension(uri));
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditProfileActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EditProfileActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
