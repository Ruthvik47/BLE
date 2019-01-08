package com.example.ruthvikreddy.ble.Model;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ruthvikreddy.ble.Activities.Scan_activity;

import static android.content.Context.BIND_AUTO_CREATE;

public class ScanClass extends Service {
    private static final String TAG = Scan_activity.class.getSimpleName();
    private BluetoothDevice device;
    private BluetoothLeService mBluetoothLeService;
    private String device_name;
    private Sharedpreferences sharedpreferences;
    private boolean mConnected = false;
    private TextView input;
    private EditText send_data;
    private Button send,connect;

    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private Context context;


    public void getMacAddress(String macAddress){
        device_name = macAddress;//getIntent().getStringExtra("deviceName");// sharedpreferences.readMacAddress()
        if(device_name!=null){
            Toast.makeText(context,device_name,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"can't connect to this device "+device_name,Toast.LENGTH_SHORT).show();
        }
        device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device_name);
        Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
               // finish();
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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
