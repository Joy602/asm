package com.example.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private EditText  signUpEmailEditText,signUpPassEditText,signUpFullName;
    private TextView signInTextView,banner;
    private Button signUpButton;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.setTitle("Sign up here");
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        signUpFullName = findViewById(R.id.signupNameEditTextId);
        signUpEmailEditText = findViewById(R.id.signupEmailEditTextId);
        signUpPassEditText = findViewById(R.id.signupPassEditTextId);
        signUpButton = findViewById(R.id.signUpButtonId);
        signInTextView = findViewById(R.id.signInTextviewId);
        progressBar = findViewById(R.id.progressbarId);
        banner = findViewById(R.id.signupBannerTextId);



        signInTextView.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        banner.setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signupBannerTextId:
                Intent intent = new Intent(getApplicationContext(),SignIn.class);
                startActivity(intent);
                break;

            case R.id.signUpButtonId:
                userRegister();
                break;
            case R.id.signInTextviewId:
                  intent = new Intent(getApplicationContext(),SignIn.class);
                startActivity(intent);
                break;
        }
    }

    private void userRegister() {

        String fullName = signUpFullName.getText().toString().trim();
        String email = signUpEmailEditText.getText().toString().trim();
        String pass = signUpPassEditText.getText().toString().trim();

        //Checking if the full Name field is empty or not
        if (fullName.isEmpty())
        {
            signUpFullName.setError("Full name is required");
            signUpFullName.requestFocus();
            return;
        }

        //checking the validity of the email
        if(email.isEmpty())
        {
            signUpEmailEditText.setError("Enter an email address");
            signUpEmailEditText.requestFocus();
            return;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            signUpEmailEditText.setError("Enter a valid Email address");
            signUpEmailEditText.requestFocus();
            return;
        }

        //checking the validity of the password
        if(pass.isEmpty())
        {
            signUpPassEditText.setError("Password is required");
            signUpPassEditText.requestFocus();
            return;
        }
        if(pass.length()<6)
        {
            signUpPassEditText.setError("Minimum password length should be 6 characters!");
            signUpPassEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);


        //mAuth is an object of firebase authentication
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull  Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful())
                {
                    finish();
                    Intent intent = new Intent(getApplicationContext(),ImageUpload.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    //Creating object of dataholder class
                    dataholder user = new dataholder(fullName,email);

                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull   Task<Void> task) {
                            if (task.isSuccessful())
                            {

                                Toast.makeText(getApplicationContext(),"Successfully Registered! ",Toast.LENGTH_SHORT).show();
                            }else
                            {
                                Toast.makeText(getApplicationContext(),"Registration failed! ",Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });




                    //Toast.makeText(getApplicationContext(),"Successfully Registered! ",Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getApplicationContext(),"Registration failed ! ",Toast.LENGTH_SHORT).show();
                    if (task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(getApplicationContext(),"User Already Registered! ",Toast.LENGTH_SHORT).show();
                    }
                    else
                        {
                            Toast.makeText(getApplicationContext(),"Error: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                }
            }
        });
    }
}