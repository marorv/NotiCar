package com.example.birathepan.bluetoothtester;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

public class Loginpage extends AppCompatActivity {

    EditText username;
    EditText password;


    //validerer input og sender til neste side hvis riktig #bratti
    public void login(View view) {
        if (username.getText().toString().replaceAll("\\s+","").equals("Birathepan") && password.getText().toString().replaceAll("\\s+","").equals("bratti")) {

            //Går til neste activity for å se etter tilgjengelige enheter
            startActivity(new Intent(getApplicationContext(),searchactivity.class));
        } else {
            //Sender til neste vindu
            Toast.makeText(getApplicationContext(),"Wrong password or username, please try again", Toast.LENGTH_LONG).show();
        }
    }

    //leave the app #bratti
    public void exit(View view) {
        finish();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        username = (EditText)findViewById(R.id.brukernavn);
        password = (EditText)findViewById(R.id.passord);

    }


}