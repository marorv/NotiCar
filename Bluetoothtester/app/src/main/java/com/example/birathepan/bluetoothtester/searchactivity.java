package com.example.birathepan.bluetoothtester;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Set;
import java.util.UUID;

public class searchactivity extends AppCompatActivity {

    BluetoothAdapter btAdapter;
    BroadcastReceiver bReceiver;
    ArrayAdapter<String> deviceListAdapter;

    //bratti test
    BluetoothSocket mmSocket;
    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID


    TextView statusText;
    ListView deviceListView;
    //TextView deviceText;
    BluetoothDevice device;


    // Kjøres idet searchactivity opprettes, initialiserer og setter opp klassen og view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_searchactivity);
        statusText = (TextView) findViewById(R.id.statusText);
        deviceListView = (ListView) findViewById(R.id.deviceList);
        //deviceText = (TextView) findViewById(R.id.deviceText);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        statusText.setVisibility(View.VISIBLE);
        deviceListView.setVisibility(View.VISIBLE);
        // Trenger en adapter for å kunne fylle listView med tekststrenger
        deviceListAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item);
        deviceListView.setAdapter(deviceListAdapter);
        // Hvis et element i lista klikkes...
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //TODO Skal prøve å connecte til den valgte enheten her.

                String object= deviceListAdapter.getItem(position);
                String address= object.substring((object.length()-17));
                device= btAdapter.getRemoteDevice(address);
                connect_to_device(device);










                // Går til neste activity --> vise status
                startActivity(new Intent(getApplicationContext(), status.class));

            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // start søk etter enheter
        discoverPairedDevices();
        discoverNonPairedDevices();
    }


    // Connect via bluetooth.socket, må finne ut om det er forskjell på paired og unpaired
    // Skal bare kobles til en enhet?
    private void connect_to_device(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Ser etter allerede paired enheter
    // Lager en mengde av eksisterende enheter og legger dem til i listView
    private void discoverPairedDevices() {
        deviceListAdapter.clear();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        statusText.setText("Looking for paired devices...");
        if(pairedDevices.size()>0) {
            for (BluetoothDevice device: pairedDevices) {
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                deviceListAdapter.add(deviceName + "\n" + deviceAddress);
                deviceListAdapter.notifyDataSetChanged();
            }
            statusText.setText("Found paired devices...");
        }
        else {
            statusText.setText("No paired devices found...");
        }
    }

    // Søker etter tilgjengelige enheter i nærheten
    // Må starte discovery først, og må avslutte i onDestroy
    // Hvis enheter blir funnet blir de lagt til i listView (hvis navn på enheten er ukjent settes
    // vises enheten som det)
    // bReceiver brukes for å søke etter enheter, må avsluttes i onDestroy
    private void discoverNonPairedDevices() {
        deviceListAdapter.clear();
        btAdapter.startDiscovery();
        statusText.setText("Looking for unpaired devices...");
        bReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = device.getName();
                   if (deviceName==null){
                        deviceName = "Unknown";
                    }
                    String deviceAddress = device.getAddress();
                    deviceListAdapter.add(deviceName + "\n" + deviceAddress);
                    deviceListAdapter.notifyDataSetChanged();
                    statusText.setText("Found unpaired devices...");
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bReceiver, filter);
    }

    // Kjøres når searchactivity avsluttes/går til neste activity
    @Override
    protected void onDestroy() {
        super.onDestroy();
        btAdapter.cancelDiscovery();
        unregisterReceiver(bReceiver);
    }













}


