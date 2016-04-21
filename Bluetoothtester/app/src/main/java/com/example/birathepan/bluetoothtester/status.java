package com.example.birathepan.bluetoothtester;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.Timer;
import java.util.UUID;

public class status extends AppCompatActivity {

    private Timer autoUpdate;
    ImageView laast;
    ImageView driver;
    ImageView passenger;
    ImageView back_left;
    ImageView back_right;

    TextView statustekst;
    TextView myLabel;
    TextView connornot2con;

    //TODO prøver å sette opp notification her

    /*
    NotificationCompat.Builder buider= new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.noticar)
            .setContentTitle("Tittel")
            .setContentText("tekst her");
    */

    //Vibrasjon
    /*
    public void vibrate(View view){
        Vibrator vibrator =(Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(1000); //10 seconds
    }
    */


    // Maren`s kode :
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID

    //Dette er EOL-symbolet!!! Hvis ikke linja slutter med "!" venter den på mer!
    final byte delimiter = 33;
    int readBufferPosition = 0;


    public void connectToBt(){
        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);

            if (!mmSocket.isConnected()){
                Log.e("Aquarium", "Not connected. Connecting");
                mmSocket.connect();
            } else {
                Log.e("Aquarium", "Already connected");
            }
            Toast.makeText(status.this, "Connection established", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Aquarium", "Failed to connect. Retrying");
            connectToBt();
        }
    }

    //TODO skal denne brukes? denne gjør foreløping ingenting
    public void sendBtMsg(String msg2send){
        //UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID

        try {
            String msg = msg2send;

            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg.getBytes());
            Log.e("Aquarium", "Sent msg");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Aquarium", "Failed to send msg");
            sendBtMsg(msg2send);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        laast=(ImageView) findViewById(R.id.bil_laast);
        laast.setVisibility(View.VISIBLE);
        driver=(ImageView) findViewById(R.id.driver);
        driver.setVisibility(View.GONE);
        passenger=(ImageView) findViewById(R.id.passenger);
        passenger.setVisibility(View.GONE);
        back_left=(ImageView) findViewById(R.id.backleft);
        back_left.setVisibility(View.GONE);
        back_right=(ImageView) findViewById(R.id.backright);
        back_right.setVisibility(View.GONE);

        final Handler handler = new Handler();
        statustekst = (TextView) findViewById(R.id.statustekstonscstreen);
        //myLabel = (TextView) findViewById(R.id.btResult);
        connornot2con= (TextView) findViewById(R.id.whoisconnected);

        //tekstfargene hvit
        statustekst.setTextColor(Color.WHITE);
        //myLabel.setTextColor(Color.WHITE);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        while(pairedDevices.size() == 0) {
            pairedDevices = mBluetoothAdapter.getBondedDevices();
        }

        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("raspberrypi"))
                {
                    Log.e("Aquarium",device.getName());

                    mmDevice = device;
                    final BluetoothDevice device1 = device;

                    handler.post(new Runnable() {
                        public void run() {
                            //String text = "Waiting for ";
                            //myLabel.setText(text + device1.getName());

                            connornot2con.setTextColor(Color.WHITE);
                            connornot2con.setText("Connected with \n "+ device1.getName() );
                        }
                    });
                    break;
                }
            }
        }

        final class workerThread implements Runnable {

            //private String btMsg;
            public workerThread() {
                //public workerThread(String msg) {
                //btMsg = msg;
            }

            public void run() {

                //sendBtMsg(btMsg);
                while (!Thread.currentThread().isInterrupted()) {
                    int bytesAvailable;
                    try {
                        final InputStream mmInputStream;
                        mmInputStream = mmSocket.getInputStream();
                        bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {

                            byte[] packetBytes = new byte[bytesAvailable];
                            Log.e("Aquarium recv bt", "bytes available");
                            byte[] readBuffer = new byte[1024];
                            mmInputStream.read(packetBytes);

                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                   byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    //The variable data now contains our full command

                                    //Her blir outputtet fra Pi-en skrevet
                                    handler.post(new Runnable() {
                                        public void run() {
                                            //Log.e("Aquarium", "Changing text to: " + data);
                                            // myLabel.setText(data);
                                            JSONObject jsondata;
                                            try {
                                                jsondata = new JSONObject(data);
                                                //myLabel.setText(jsondata.toString());
                                                change_screen(jsondata);
                                            }
                                            catch (JSONException e) {
                                                Log.e("Aquarium", "JSONExeption: tried to parse" + data);
                                            }
                                        }
                                    });
                                    break;

                                } else {
                                    Log.e("Aquarium", "pushing readBufferPosition forward");
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        handler.post(new Runnable() {
                            public void run() {
                                final String text = "Failed to connect";
                                //myLabel.setText(text);
                            }
                        });
                        e.printStackTrace();
                    }
                }
            }
        }

        //Cycle

        connectToBt();
        (new Thread(new workerThread())).start();
    }

    private void change_screen(JSONObject data) {
        show_locked_car();
        boolean open = false;
        try {
            Boolean driverdoor = Boolean.valueOf(data.getString("driver"));
            if(driverdoor.equals(true)){
                driver.setVisibility(View.VISIBLE);
                open = true;
            }
            Boolean passengerdoor = Boolean.valueOf(data.getString("passenger"));
            if(passengerdoor.equals(true)){
                passenger.setVisibility(View.VISIBLE);
                open = true;
            }
            Boolean backrightdoor = Boolean.valueOf(data.getString("backright"));
            if(backrightdoor.equals(true)){
                back_right.setVisibility(View.VISIBLE);
                open = true;
            }
            Boolean backleftdoor = Boolean.valueOf(data.getString("backleft"));
            if(backleftdoor.equals(true)){
                back_left.setVisibility(View.VISIBLE);
                open = true;
            }
            if(open) {
                statustekst.setText("CAR OPEN");
            }
        } catch (JSONException e) {
            Log.e("Aquarium", "JSONExeption: tried to decode");
        }
    }

    private void show_locked_car() {
        laast.setVisibility(View.VISIBLE);
        driver.setVisibility(View.INVISIBLE);
        passenger.setVisibility(View.INVISIBLE);
        back_right.setVisibility(View.INVISIBLE);
        back_left.setVisibility(View.INVISIBLE);
    }


    //TODO: Lage en funksjon som leser ping-meldinger fra Pi og svarer på
    //      disse for å bekrefte status som tilkoblet. Brukeren trenger
    //      ikke se disse, men de bør loggføres.



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //kommentert
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
