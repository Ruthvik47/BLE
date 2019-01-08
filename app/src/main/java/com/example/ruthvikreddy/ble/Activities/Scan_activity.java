package com.example.ruthvikreddy.ble.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruthvikreddy.ble.Model.BluetoothLeService;
import com.example.ruthvikreddy.ble.Model.SampleGattAttributes;
import com.example.ruthvikreddy.ble.Model.Sharedpreferences;
//import com.example.ruthvikreddy.bleconnect.Model.BluetoothLeService;
//import com.example.ruthvikreddy.bleconnect.Model.SampleGattAttributes;
//import com.example.ruthvikreddy.bleconnect.Model.sharedpreferences;
import com.example.ruthvikreddy.ble.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scan_activity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = Scan_activity.class.getSimpleName();
    private BluetoothDevice device;
    private BluetoothLeService mBluetoothLeService;
    private String device_name,device_data;
    private Sharedpreferences sharedpreferences;
    private boolean mConnected = true;
    private TextView status;
    private Button send;
    private ProgressBar progressBar;

    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        sharedpreferences = new Sharedpreferences(this);

        status = (TextView)findViewById(R.id.status);
        send = (Button) findViewById(R.id.send);

        send.setOnClickListener(this);

        device_name = getIntent().getStringExtra("deviceName");// sharedpreferences.readMacAddress()

        device_data = getIntent().getStringExtra("data");

        //sendDataToBLE(device_data);
        if(device_name!=null){
           // Toast.makeText(Scan_activity.this,device_name,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(Scan_activity.this,"can't connect to this device "+device_name,Toast.LENGTH_SHORT).show();
        }
        device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device_name);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(device.getAddress());

        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            device_name=null;


        }
    };
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Log.d("connected","connecteddddddddddddddddd");
                getSupportActionBar().setTitle("Send Data");
                progressBar.setVisibility(View.GONE);
                send.setVisibility(View.VISIBLE);
                status.setVisibility(View.VISIBLE);
                status.setText("Status : Connected");
               // updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Log.d("disconnected","disconnecteddddddddddddddddd");
                Toast.makeText(Scan_activity.this,"Unable to connect.Try again",Toast.LENGTH_LONG).show();
                finish();
              //  updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.d("connected","connecteddddddddddddddddd");
                // Show all the supported services and characteristics on the user interface.
               displayGattServices(mBluetoothLeService.getSupportedGattServices());
            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)){
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

            }
        }
    };
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(device.getAddress());
            Log.e(TAG, "Connect request result=" + result);
        }
       // sendDataToBLE(device_data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
       // unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        device_name=null;
        //close();

   }

    private void close() {
        if(mBluetoothLeService ==null)
            return;
        mBluetoothLeService.close();
        mBluetoothLeService=null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.send:
                //String data  = send_data.getText().toString();
                Log.d("ddddddddddddddddddddd",device_data);
                sendDataToBLE(device_data);
                break;

        }

    }
    void sendDataToBLE(String str) {
        Log.d(TAG, "Sending result=" + str);
        final byte[] tx = str.getBytes();



        if (mConnected) {
            getSupportActionBar().setTitle("Sent!");
            send.setText("Sent!");
            send.setEnabled(false);
            characteristicTX.setValue(tx);
            mBluetoothLeService.writeCharacteristic(characteristicTX);
            mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
            Toast.makeText(Scan_activity.this,"Sent..!",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    private void displayData(String stringExtra) {
        if (stringExtra != null) {
            //input.setText(stringExtra);
        }
    }
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();


        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));

            // If the service exists for HM 10 Serial, say so.
            if (SampleGattAttributes.lookup(uuid, unknownServiceString) == "HM 10 Serial") {
               // isSerial.setText("Yes");
            } else {
                //isSerial.setText("No");
            }
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            // get characteristic when UUID matches RX/TX UUID
            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
        }

    }

}
