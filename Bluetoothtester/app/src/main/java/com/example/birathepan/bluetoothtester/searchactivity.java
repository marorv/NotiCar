package com.example.birathepan.bluetoothtester;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class searchactivity extends AppCompatActivity {

    BluetoothAdapter btAdapter;
    BroadcastReceiver bReceiver;

    ArrayAdapter<String> deviceListAdapter;

    TextView statusText;
    ListView deviceListView;
    Button okButton;
    TextView deviceText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchactivity);
        statusText = (TextView) findViewById(R.id.statusText);
        deviceListView = (ListView) findViewById(R.id.deviceList);
        okButton = (Button) findViewById(R.id.okButton);
        deviceText = (TextView) findViewById(R.id.deviceText);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        okButton.setVisibility(View.GONE);
        statusText.setVisibility(View.VISIBLE);
        deviceListView.setVisibility(View.VISIBLE);
        deviceListAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item);
        deviceListView.setAdapter(deviceListAdapter);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //Går til neste activity for å se etter tilgjengelige enheter
                startActivity(new Intent(getApplicationContext(),status.class));

                Toast.makeText(searchactivity.this, "Found this..", Toast.LENGTH_LONG).show();
            }
        });
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //discoverPairedDevices();
        discoverNonPairedDevices();
    }

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
        }
        else {
            statusText.setText("No paired devices found...");
        }
    }

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
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //startActivity(new Intent(getApplicationContext(), Mainpage.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btAdapter.cancelDiscovery();
        unregisterReceiver(bReceiver);
    }
}

