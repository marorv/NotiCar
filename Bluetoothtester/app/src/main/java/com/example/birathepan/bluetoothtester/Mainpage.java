package com.example.birathepan.bluetoothtester;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Mainpage extends AppCompatActivity {

    //#bratti
    /** Kalt når activity er først laget **/
    private BluetoothAdapter btadapter;
    protected static final int Discovery_request=1; //1 er for true, 0 false


    /*setter inn Broadcastreaciever informasjonen her nede */
    BroadcastReceiver bluetoothState= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String previousStateextra = BluetoothAdapter.EXTRA_PREVIOUS_STATE;
            String statextra = BluetoothAdapter.EXTRA_STATE;
            int state = intent.getIntExtra(previousStateextra, -1);
            //int previousstate = intent.getIntExtra(previousStateextra, -1);
            String nytekst="";
            switch (state) {
                case (BluetoothAdapter.STATE_TURNING_ON): {
                    nytekst = "Bluetooth is turning ON";
                    Toast.makeText(Mainpage.this,nytekst,Toast.LENGTH_SHORT).show();
                    setupUI();
                    break;
                }

                case (BluetoothAdapter.STATE_ON): {
                    nytekst = "Buetooth is ON";
                    Toast.makeText(Mainpage.this,nytekst,Toast.LENGTH_SHORT).show();
                    setupUI();
                    break;
                }
                case (BluetoothAdapter.STATE_TURNING_OFF): {
                    nytekst = "Bluetooth is turning OFF";
                    Toast.makeText(Mainpage.this,nytekst,Toast.LENGTH_SHORT).show();
                    break;
                }
                case (BluetoothAdapter.STATE_OFF): {
                    nytekst = "Bluetooth is OFF";
                    Toast.makeText(Mainpage.this,nytekst,Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };
    /*oncreate*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        setupUI();
        }

    //#bratti
    private void setupUI(){
        //henter referanser
        final TextView statusUpdate =(TextView) findViewById(R.id.result);
        final Button disconnect= (Button) findViewById(R.id.disconnectbutton);
        final Button connect2=(Button) findViewById(R.id.connectbutton2);
        final ImageView bilde=(ImageView) findViewById(R.id.imageView);

        disconnect.setVisibility(View.GONE);

        btadapter = BluetoothAdapter.getDefaultAdapter();
        if (btadapter.isEnabled()) {
            String address = btadapter.getAddress();
            String name = btadapter.getName();
            String statusText = "Connected unit: \n" + name + "addrese:" +address;
            //shows the connected unit.Skriv inn address til slutt for å få addressen til enheten på statustext.
            statusUpdate.setText(statusText);
            disconnect.setVisibility(View.VISIBLE);
            connect2.setVisibility(View.INVISIBLE);
            bilde.setVisibility(View.INVISIBLE);


            //Går til neste activity for å se etter tilgjengelige enheter
            startActivity(new Intent(getApplicationContext(), searchactivity.class));


        } else {
            connect2.setVisibility(View.VISIBLE);
            statusUpdate.setText("Make sure Bluetooth is turned on");

        }


        connect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Denne koden slår bare bluetooth av og på
                String actionstatechanged = BluetoothAdapter.ACTION_STATE_CHANGED;
                String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
                IntentFilter filter = new IntentFilter(actionstatechanged);
                registerReceiver(bluetoothState, filter);
                startActivityForResult(new Intent(actionRequestEnable), 0);


                //refresh
                Intent intent = getIntent();
                startActivity(intent);


            }

        });

        //denne trengs egentlig ikke siden vi trenger bare å slå på bluetooth
        //funksjonen her er å slå av bluetooth og gå ut av appen
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btadapter.disable();
                disconnect.setVisibility(View.VISIBLE);
                statusUpdate.setText("Bluetooth is off");


                finish();
            }
        });

    }//endsetupUI

    protected void onActivityResult(int requestcode,int resulcode,Intent data){
        if(requestcode == Discovery_request){

            //requestcode=1;
            Toast.makeText(Mainpage.this, "Discovery on temp..", Toast.LENGTH_LONG).show();
            setupUI();
            //findDevices();

        }
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
