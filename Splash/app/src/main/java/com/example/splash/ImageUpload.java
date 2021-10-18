package com.example.splash;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class ImageUpload extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    Button upload,choose;
    TextView alert;
    ArrayList<Uri> imageList = new ArrayList<Uri>();
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private int uploadCount = 0;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        this.setTitle("Upload Image");

        alert = findViewById(R.id.alertTextviewId);
        upload = findViewById(R.id.uploadImageButtonId);
        choose = findViewById(R.id.chooseImageButtonId);

        //init
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Please wait...");

        choose.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                Intent intent  =new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                progressDialog.show();
                alert.setText("If loading takes too long please press the button again");

                //Creating a Folder Images to Firebase Storage
                StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("Images");

                for (uploadCount = 0; uploadCount < imageList.size();uploadCount++)
                {
                    Uri individualImage = imageList.get(uploadCount);
                    //StorageReference imageName = ImageFolder.child("Image"+ individualImage.getLastPathSegment());
                    //StorageReference imageName =  ImageFolder.child(FirebaseAuth.getInstance().getUid() + individualImage.getLastPathSegment());
                    //StorageReference imageName = ImageFolder.child(mAuth.getCurrentUser().getUid()+individualImage );
                    StorageReference imageName = ImageFolder.child(mAuth.getCurrentUser().getUid()+individualImage.getPath() );

                    imageName.putFile(individualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = String.valueOf(uri);

                                    storeLink(url);
                                    /*Intent intent = new Intent(getApplicationContext(),EditProfile.class);
                                    startActivity(intent);*/

                                }
                            });
                        }
                    });
                }
                Intent intent = new Intent(getApplicationContext(),EditProfile.class);
                startActivity(intent);
            }
        });


    }

    private void storeLink(String url){

        for (uploadCount = 0; uploadCount < imageList.size();uploadCount++) {
            Uri individualImage = imageList.get(uploadCount);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Train_Images/" + mAuth.getCurrentUser().getUid());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("Imglink", url);
            databaseReference.push().setValue(hashMap);

        }





//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Image Link"+individualImage.getLastPathSegment());
//        HashMap<String,String> hashMap = new HashMap<>() ;
//       hashMap.put("Imglink",url);
//        databaseReference.push().setValue(hashMap);

        progressDialog.dismiss();
        alert.setText("Images uploaded successfully!");
        upload.setVisibility(View.GONE);
        imageList.clear();


    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE)
        {
            if (resultCode == RESULT_OK)
            {
                if (data.getClipData() != null)
                {
                    //To count how many images
                    int countClipData = data.getClipData().getItemCount();

                    int currentImageSelect = 0;

                    while (currentImageSelect<countClipData)
                    {
                        imageUri = data.getClipData().getItemAt(currentImageSelect).getUri();
                        imageList.add(imageUri);



                        currentImageSelect = currentImageSelect+1;
                    }

                    alert.setVisibility(View.VISIBLE);
                    alert.setText("You have selected "+imageList.size()+" images");
                    choose.setVisibility(View.GONE);


                }else {
                    Toast.makeText(this,"Please select multiple images",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}