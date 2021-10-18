package com.example.splash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity implements View.OnClickListener {

    private EditText  signInEmailEditText,signInPassEditText;
    private TextView signUpTextView;
    private Button signInButton;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        this.setTitle("Sign in here");
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        signInEmailEditText = findViewById(R.id.signinEmailEditTextId);
        signInPassEditText = findViewById(R.id.signInPassEditTextId);
        signInButton = findViewById(R.id.signInButtonId);
        signUpTextView = findViewById(R.id.signUpTextviewId);
        progressBar = findViewById(R.id.progressbarId);

        signUpTextView.setOnClickListener(this);
        signInButton.setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signInButtonId:
                    userLogin();
                break;
            case R.id.signUpTextviewId:
                Intent intent = new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
                break;
        }

    }

    private void userLogin()
    {
        String email = signInEmailEditText.getText().toString().trim();
        String pass = signInPassEditText.getText().toString().trim();

        //checking the validity of the email
        if(email.isEmpty())
        {
            signInEmailEditText.setError("Enter an email address");
            signInEmailEditText.requestFocus();
            return;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            signInEmailEditText.setError("Enter a valid Email address");
            signInEmailEditText.requestFocus();
            return;
        }

        //checking the validity of the password
        if(pass.isEmpty())
        {
            signInPassEditText.setError("Enter a password");
            signInPassEditText.requestFocus();
            return;
        }
        if(pass.length()<6)
        {
            signInPassEditText.setError("Your password should be 6 digit or more longer!");
            signInPassEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull  Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful())
                {
                    finish();
                    Intent intent = new Intent(getApplicationContext(),EditProfile.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"Login failed! ",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}