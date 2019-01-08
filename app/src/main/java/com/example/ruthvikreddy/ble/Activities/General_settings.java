package com.example.ruthvikreddy.ble.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.ruthvikreddy.ble.Model.Sharedpreferences;
import com.example.ruthvikreddy.ble.R;

import java.util.ArrayList;

public class General_settings extends AppCompatActivity implements View.OnClickListener {
    private Sharedpreferences sharedpreferences;
    private RadioButton psi,bar,kpa,mph,kph,feet,meter;
    private EditText trigger,front_wheel,rear_wheel;
    private Button save;
    int units_value,speed_value,distance_value;
    private  String final_value;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private static final int REQUEST_CODE = 200;
    private boolean mScanning;

    private static final int RQS_ENABLE_BLUETOOTH = 1;
    private ScanSettings settings;
    private ArrayList<ScanFilter> filters;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("General Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedpreferences = new Sharedpreferences(this);

        getBluetoothAdapterAndLeScanner();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,
                    "ble is not supported in your device",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mHandler = new Handler();

        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        filters = new ArrayList<>();
        //ParcelUuid uu = new ParcelUuid(UUID.fromString("00001207-0000-1000-8000-00805f9b34fb"));
        ScanFilter filter = new ScanFilter.Builder().setDeviceName("bluino").build();
        filters.add(filter);

        psi = (RadioButton)findViewById(R.id.radio_psi);
        bar  = (RadioButton)findViewById(R.id.radio_bar);
        kpa = (RadioButton)findViewById(R.id.radio_kpa);
        mph = (RadioButton)findViewById(R.id.mph);
        kph = (RadioButton)findViewById(R.id.kph);
        feet  = (RadioButton)findViewById(R.id.feet);
        meter = (RadioButton)findViewById(R.id.meter);
        trigger = (EditText)findViewById(R.id.trigger);
        front_wheel =(EditText)findViewById(R.id.front_wheel);
        rear_wheel = (EditText)findViewById(R.id.rear_wheel);

        save = (Button)findViewById(R.id.general_save);
        save.setOnClickListener(this);
    }
    public void onPressureRadioButtonClick(View view) {
        boolean checked = ((RadioButton)view).isChecked();
         units_value = 0;
        switch (view.getId()){

            case R.id.radio_psi:
                if(checked){
                    units_value =1;
                    psi.setChecked(true);
                    sharedpreferences.writeUnitsValue(units_value);
                    Log.d("psi","checked");
                }
                break;
            case R.id.radio_bar:
                if(checked){
                    units_value =2;
                    bar.setChecked(true);
                    sharedpreferences.writeUnitsValue(units_value);
                    Log.d("bar","checked");
                }
                break;
                case R.id.radio_kpa:
                if(checked){
                    units_value =3;
                    kpa.setChecked(true);
                    sharedpreferences.writeUnitsValue(units_value);
                    Log.d("kpa","checked");
                }
                break;



        }
    }

    public void onSpeedRadioButtonClick(View view) {
        boolean checked = ((RadioButton)view).isChecked();
        speed_value = 0;

        switch (view.getId()){
            case R.id.mph:
                if(checked){
                    speed_value = 1;
                    mph.setChecked(true);
                    Log.d("psi","checked");
                }
                break;
            case R.id.kph:
                if(checked){
                   speed_value = 2;
                    kph.setChecked(true);
                    Log.d("bar","checked");
                }
                break;

        }
    }


    public void onDistanceRadioButtonClick(View view) {
        boolean checked = ((RadioButton)view).isChecked();
        distance_value = 0;
        switch (view.getId()){
            case R.id.feet:
                if(checked){
                    distance_value = 1;
                    units_value =6;
                    feet.setChecked(true);
                    Log.d("kpa","checked");
                }
                break;
            case R.id.meter:
                if(checked){
                    distance_value = 1;
                    units_value =6;
                    meter.setChecked(true);
                    Log.d("kpa","checked");
                }
                break;

        }
    }


    @Override
    public void onClick(View v) {
        String trigger_string = trigger.getText().toString();
        String  front_string = front_wheel.getText().toString();
        String rear_string = rear_wheel.getText().toString();

        int id = v.getId();
        switch (id){
            case R.id.general_save:
                if(units_value == 0 || speed_value == 0 || distance_value == 0){
                    Toast.makeText(General_settings.this,"Select any options to save the value",Toast.LENGTH_SHORT).show();
                }else{
                    if(rear_string.isEmpty() || trigger_string.isEmpty() || front_string.isEmpty()){
                        Toast.makeText(General_settings.this,"fill all the fields",Toast.LENGTH_SHORT).show();
                    }else{
                        if(mBluetoothAdapter!=null){
                            // Toast.makeText(this,"Bluetooth is supported in your device", Toast.LENGTH_SHORT).show();
                            if (!mBluetoothAdapter.isEnabled()) {
                                if (!mBluetoothAdapter.isEnabled()) {
                                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent, RQS_ENABLE_BLUETOOTH);
                                }

                            }
                        }
                        if(mBluetoothAdapter.isEnabled()){
                            scanLeDevice(true);
                            Log.d("clicked","serach");
                        }
                        final_value = units_value+"_"+speed_value+"_"+distance_value+"_"+trigger_string+"_"+front_string+"_"+rear_string;
                        sharedpreferences.writeDisplayActivityValue(final_value);
                        Toast.makeText(General_settings.this,final_value,Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }
    }
    public void scanLeDevice(final boolean enabled) {
        if(enabled){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothLeScanner.stopScan(mScanCallback);
                }
            },SCAN_PERIOD);
            mScanning = true;
            mBluetoothLeScanner.startScan(filters,settings,mScanCallback);
        }else{
            mScanning = false;
            mBluetoothLeScanner.stopScan(mScanCallback);
        }

    }
    public ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.e("callbackType", String.valueOf(callbackType));
            Log.e("result", result.toString());
            String device = result.getDevice().getName() + "=" + result.getDevice().getAddress();
            // Toast.makeText(MainActivity.this, device + "", Toast.LENGTH_SHORT).show();
            Log.d("device",device);
//            if(!deviceModelList.contains(device)){
//                deviceModelList.add(device);
            //connect(result.getDevice().getAddress());
            // }
            if(callbackType == 1){
                Toast.makeText(General_settings.this, final_value+"", Toast.LENGTH_SHORT).show();
                mBluetoothLeScanner.stopScan(mScanCallback);
                Intent intent = new Intent(General_settings.this,Scan_activity.class);
                //sharedpreferences.writeMacAddress(""+result.getDevice());
                intent.putExtra("deviceName",result.getDevice().getAddress());
                intent.putExtra("data",final_value);
                startActivity(intent);
            }

            //  deviceAdapter.notifyDataSetChanged();
        }
    };
    private void getBluetoothAdapterAndLeScanner(){
        // Get BluetoothAdapter and BluetoothLeScanner.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        //  mScanning = false;
    }
}
