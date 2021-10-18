package com.example.splash;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditProfile extends AppCompatActivity {

    private ImageView profileImage;
    private Button changeProfile,home;
    private ProgressDialog progressDialog;


    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;

    StorageReference storageReference ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        this.setTitle("Edit Profile");

        profileImage = findViewById(R.id.profileImageId);
        changeProfile = findViewById(R.id.changeProfilePicButtonId);
        home = findViewById(R.id.homeButtonId);

        //changeProfile.setVisibility(View.VISIBLE);
        //home.setVisibility(View.INVISIBLE);


        //init
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Please wait...");


        StorageReference profileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                //when ChangeProfile is clicked show Home button and hide ChangeProfile button
                /*changeProfile.setVisibility(View.GONE);
                home.setVisibility(View.VISIBLE);*/

                //Open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);


            }

        });


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);

            }
        });






    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable   Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == 1000)
        {
            if (resultCode == Activity.RESULT_OK  )
            {
                Uri imageUri = data.getData();
                //profileImage.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);

            }
        }

    }

    private void uploadImageToFirebase(Uri imageUri)
    {
        StorageReference fileRef = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(getApplicationContext(),"Uploaded Successfully!",Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }

                });
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                Toast.makeText(getApplicationContext(),"Failed to upload!",Toast.LENGTH_SHORT).show();
            }
        });
    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {

            //  Converting menu_layout.xml file into a java file
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.signout_layout,menu);

            return super.onCreateOptionsMenu(menu);
        }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.signOutMenuID){
            //Toast.makeText(MainActivity.this,"Sign Out is selected",Toast.LENGTH_SHORT).show();
            //return true;
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent intent  = new Intent(getApplicationContext(),SignIn.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}