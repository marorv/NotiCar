package com.example.birathepan.bluetoothtester;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class searchactivity extends AppCompatActivity {

    BluetoothAdapter btAdapter;
    BroadcastReceiver bReceiver;

    ArrayAdapter<String> deviceListAdapter;

    TextView statusText;
    ListView deviceList;
    Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchactivity);
        final TextView statusText = (TextView) findViewById(R.id.statusText);
        final ListView deviceList = (ListView) findViewById(R.id.deviceList);
        final Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setVisibility(View.GONE);
        statusText.setVisibility(View.GONE);
        deviceList.setVisibility(View.GONE);
        deviceListAdapter = new ArrayAdapter<String>(this, R.layout.content_searchactivity, 0);
        deviceList.setAdapter(deviceListAdapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void discoverPairedDevices() {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        statusText.setText("Looking for paired devices...");
        if(pairedDevices.size()>0) {
            for (BluetoothDevice device: pairedDevices) {
                deviceListAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    private void discoverNonPairedDevices() {
        btAdapter.startDiscovery();
        if(btAdapter.isDiscovering()){
            statusText.setText("Looking for unpaired devices...");
        }
        bReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    deviceListAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bReceiver, filter);
        btAdapter.cancelDiscovery();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(btAdapter.isEnabled()) {
            statusText.setText("Ready to start discovery");
            discoverPairedDevices();
            discoverNonPairedDevices();
        }
        else {
            statusText.setText("Bluetooth not enabled?");
            onStop();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        startActivity(new Intent(getApplicationContext(), Mainpage.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }
}

