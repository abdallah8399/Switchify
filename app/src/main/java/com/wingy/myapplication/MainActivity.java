package com.wingy.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private ImageView userImage;
    private TextView btnMainSignUp, informativeTV, btnResetPassword;
    private EditText etLoginEmail, etLoginPassword, etUserName;
    private Button btnSignUp, btnLogin, btnReset;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private StorageReference mStorageRef;
    private Boolean imageUploaded= true;
    private DatabaseReference UsersRef;
    private Uri uri;
    private String imageURL;
    private Boolean home=true;
    public static int height, width;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        setContentView(R.layout.activity_main);
        InitializeTools();

        btnSignUp.setVisibility(View.INVISIBLE);
        etUserName.setVisibility(View.INVISIBLE);
        userImage.setVisibility(View.INVISIBLE);
        informativeTV.setVisibility(View.INVISIBLE);
        btnReset.setVisibility(View.INVISIBLE);


        progressDialog= new ProgressDialog(this);
        mAuth= FirebaseAuth.getInstance();
        mStorageRef= FirebaseStorage.getInstance().getReference("Images");
        UsersRef= FirebaseDatabase.getInstance().getReference("Users");

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 11);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUploaded){
                    String username= etUserName.getText().toString(),
                           userEmail= etLoginEmail.getText().toString().trim(),
                           userPassword= etLoginPassword.getText().toString();
                    if(!userEmail.contains("@")|| !userEmail.contains("."))
                        Toast.makeText(MainActivity.this, "Enter the email address correctly", Toast.LENGTH_SHORT).show();
                    else if(username.isEmpty())
                        Toast.makeText(MainActivity.this, "Enter your name", Toast.LENGTH_SHORT).show();
                    else if(userPassword.isEmpty()||userPassword.length()<8)
                        Toast.makeText(MainActivity.this, "Enter a password with a length of 8 or more ", Toast.LENGTH_SHORT).show();
                    else if(imageURL.isEmpty())
                        Toast.makeText(MainActivity.this, "Add a picture", Toast.LENGTH_SHORT).show();
                    else {
                        progressDialog.setMessage("Creating Account");
                        progressDialog.show();
                        SignUp(userEmail, userPassword, username);
                    }
                }else Toast.makeText(MainActivity.this, "Image Uploading", Toast.LENGTH_LONG).show();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  userEmail= etLoginEmail.getText().toString().trim(),
                        userPassword= etLoginPassword.getText().toString();
                //SignIn("abdallah@gmail.com", "123456789");

                if(userEmail.isEmpty())
                    Toast.makeText(MainActivity.this, "Enter your e-mail", Toast.LENGTH_SHORT).show();
                else if(userPassword.isEmpty())
                    Toast.makeText(MainActivity.this, "Enter your password", Toast.LENGTH_SHORT).show();
                else{
                    progressDialog.setMessage("logging in...");
                    progressDialog.show();
                    SignIn(userEmail, userPassword);
                }

            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLogin.setVisibility(View.INVISIBLE);
                btnSignUp.setVisibility(View.INVISIBLE);
                btnMainSignUp.setVisibility(View.INVISIBLE);
                etUserName.setVisibility(View.INVISIBLE);
                userImage.setVisibility(View.INVISIBLE);
                informativeTV.setVisibility(View.INVISIBLE);
                btnResetPassword.setVisibility(View.INVISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                home=false;
            }
        });
        btnMainSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLogin.setVisibility(View.INVISIBLE);
                btnSignUp.setVisibility(View.VISIBLE);
                btnMainSignUp.setVisibility(View.INVISIBLE);
                etUserName.setVisibility(View.VISIBLE);
                userImage.setVisibility(View.VISIBLE);
                informativeTV.setVisibility(View.VISIBLE);
                btnResetPassword.setVisibility(View.INVISIBLE);
                btnReset.setVisibility(View.INVISIBLE);

                home=false;
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Instructions sent to: \n"+etLoginEmail.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (home)
            super.onBackPressed();
        else {
            btnLogin.setVisibility(View.VISIBLE);
            btnResetPassword.setVisibility(View.VISIBLE);
            btnSignUp.setVisibility(View.INVISIBLE);
            btnMainSignUp.setVisibility(View.VISIBLE);
            etUserName.setVisibility(View.INVISIBLE);
            userImage.setVisibility(View.INVISIBLE);
            informativeTV.setVisibility(View.INVISIBLE);
            btnReset.setVisibility(View.INVISIBLE);
            home=true;
        }
    }

    private void InitializeTools(){
        userImage = findViewById(R.id.ivAddUserImage);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        etUserName = findViewById(R.id.etUserName);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        btnMainSignUp = findViewById(R.id.btnMainSignUp);
        informativeTV = findViewById(R.id.textView);
        btnResetPassword = findViewById(R.id.tvForgotPassword);
        btnReset= findViewById(R.id.btnReset);
    }

    private String getExtension(Uri u){
        ContentResolver cr= getContentResolver();
        MimeTypeMap m= MimeTypeMap.getSingleton();
        return m.getExtensionFromMimeType(cr.getType(u));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11 && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri= data.getData();

            Picasso.get().load(uri).fit().centerCrop().into(userImage);

            Toast.makeText(MainActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();
            imageUploaded= false;

            final StorageReference storageReference= mStorageRef.child(System.currentTimeMillis()+"."+ getExtension(uri));
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                    imageUploaded= true;
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
                    Toast.makeText(MainActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
                    imageUploaded= true;
                }
            });
        }
    }

    private void SignIn(final String mUsername, String mPassword){
        mAuth.signInWithEmailAndPassword(mUsername, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent= new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();

                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void SignUp(final String email, final String password, final String name){

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User newUser= new User(email, password,name,0,imageURL, task.getResult().getUser().getUid());
                                UsersRef.child(task.getResult().getUser().getUid()).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "Account was created successfully", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        SignIn(email,password);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "connection error", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
    }

}
