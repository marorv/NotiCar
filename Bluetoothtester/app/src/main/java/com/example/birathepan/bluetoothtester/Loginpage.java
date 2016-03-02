package com.example.birathepan.bluetoothtester;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Loginpage extends AppCompatActivity {


    //validerer input og sender til neste side hvis riktig #bratti

    /*
    EditText username = (EditText)findViewById(R.id.brukernavn);
    EditText password = (EditText)findViewById(R.id.passord);


    public void login(View view) {
        if (username.getText().toString().equals("Birathepan") && password.getText().toString().equals("bratti")) {

            //Går til neste activity for å se etter tilgjengelige enheter
            startActivity(new Intent(getApplicationContext(),searchactivity.class));
        } else {
            //Sender til neste vindu
            Toast.makeText(getApplicationContext(),"Wrong password, please try again", Toast.LENGTH_LONG).show();
        }
    }
*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }


}