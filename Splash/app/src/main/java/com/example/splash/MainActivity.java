package com.example.splash;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


public class MainActivity extends AppCompatActivity {

    private Button connect,signOut;
    private EditText ipEditText,passEditText;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting a title for Action bar
        getSupportActionBar().setTitle("SHA");

        signOut = findViewById(R.id.signOutButtonId);
        connect = (Button) findViewById(R.id.connectButtonId);
        ipEditText = findViewById(R.id.ipEditTextId);
        passEditText = findViewById(R.id.passwordEditTextId);


        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Integer, Void, Void>() {
                    @Override
                    protected Void doInBackground(Integer... integers) {
                        try {
                            ExecuteMirrorOnCommand();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(1);
            }
        });






        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent  = new Intent(getApplicationContext(),SignIn.class);
                startActivity(intent);
            }
        });

    }

    private void ExecuteMirrorOnCommand()
    {
        //String host  = ipEditText.getText().toString();
        //String pass = passEditText.getText().toString();
        String host = "10.0.2.15";
        String pass = "6268";
        String user = "pi";
        int port = 22;

        try {

            JSch jsch=new JSch();
            Session session=jsch.getSession(user, host, 22);
            session.setPassword(pass);
            session.setConfig("StrictHostKeyChecking","no");
            session.setTimeout(10000);
            session.connect();

            ChannelExec channel = (ChannelExec)session.openChannel("exec");
            //channel.setCommand("cd MagicMirror && npm start");
            channel.setCommand("sudo halt");

            channel.connect();
            try{Thread.sleep(1000);}catch (Exception ee){}
            channel.disconnect();





        }catch (JSchException e){

        }
    }










    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

       //  Converting menu_layout.xml file into a java file
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_layout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.settingId){
            Toast.makeText(MainActivity.this,"Setting is selected",Toast.LENGTH_SHORT).show();
            return true;
        }
        if(item.getItemId()==R.id.shareId){
            Toast.makeText(MainActivity.this,"Share is selected",Toast.LENGTH_SHORT).show();
            return true;
        }
        if(item.getItemId()==R.id.feedbackId){
            Toast.makeText(MainActivity.this,"Feedback is selected",Toast.LENGTH_SHORT).show();
            return true;
        }
        if(item.getItemId()==R.id.faqId){
            Toast.makeText(MainActivity.this,"FAQ is selected",Toast.LENGTH_SHORT).show();
            return true;
        }
        if(item.getItemId()==R.id.signOutId){
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