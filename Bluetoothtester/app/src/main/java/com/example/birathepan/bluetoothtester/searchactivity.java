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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

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
        statusText = (TextView) findViewById(R.id.statusText);
        deviceList = (ListView) findViewById(R.id.deviceList);
        okButton = (Button) findViewById(R.id.okButton);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        okButton.setVisibility(View.GONE);
        statusText.setVisibility(View.VISIBLE);
        deviceList.setVisibility(View.VISIBLE);
        deviceListAdapter = new ArrayAdapter<>(this, R.layout.content_searchactivity, 0);
        deviceList.setAdapter(deviceListAdapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        discoverPairedDevices();
        //discoverNonPairedDevices();
    }

    private void discoverPairedDevices() {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        statusText.setText("Looking for paired devices...");
        if(pairedDevices.size()>0) {
            for (BluetoothDevice device: pairedDevices) {
                deviceListAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
        else {
            statusText.setText("No paired devices found...");
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
    }


    @Override
    protected void onStop() {
        super.onStop();
        startActivity(new Intent(getApplicationContext(), Mainpage.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btAdapter.cancelDiscovery();
        unregisterReceiver(bReceiver);
    }
}

