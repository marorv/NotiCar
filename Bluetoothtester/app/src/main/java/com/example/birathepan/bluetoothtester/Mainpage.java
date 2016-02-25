package com.example.birathepan.bluetoothtester;

import android.bluetooth.BluetoothAdapter;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Mainpage extends AppCompatActivity {


    /** Kalt når activity er først laget **/
    private BluetoothAdapter btadapter;
    public TextView statusUpdate;
    public Button connect;
    public Button disconnect;
    public ImageView logo;



    /*oncreate*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        setupUI();
        };

    private void setupUI(){
        //henter referanser
        final TextView statusUpdate =(TextView) findViewById(R.id.result);
        final Button connect =(Button) findViewById(R.id.connectbutton);
        final Button disconnect= (Button) findViewById(R.id.disconnectbutton);
        final ImageView logo=(ImageView) findViewById(R.id.logo);
        // setter en displayview

        //connect.setVisibility(View.GONE);
        //disconnect.setVisibility(View.GONE);
        logo.setVisibility(View.GONE);


        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btadapter = BluetoothAdapter.getDefaultAdapter();
                if (btadapter.isEnabled()) {
                    String address = btadapter.getAddress();
                    String name = btadapter.getName();
                    String statusText = name + " : " + address;
                    statusUpdate.setText(statusText);

                } else {
                    statusUpdate.setText("Pls connect me :(");
                }


            }
        });// slutt på connect onclicklistener

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }); // slutt på disconnect onclicklistener








    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mainpage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
