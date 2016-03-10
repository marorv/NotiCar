package com.example.birathepan.bluetoothtester;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class status extends AppCompatActivity {

    //Vibrasjon funker #bratti
    public void vibrate(View view){
        Vibrator vibrator =(Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(1000); //10 seconds

    }
    // YOYO





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


}
