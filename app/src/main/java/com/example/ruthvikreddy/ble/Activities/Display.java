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
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.ruthvikreddy.ble.Model.Sharedpreferences;
import com.example.ruthvikreddy.ble.R;

import java.util.ArrayList;

public class Display extends AppCompatActivity implements View.OnClickListener {
    private Sharedpreferences sharedpreferences;
    private RadioButton psi,bar,kpa,graph_d,peek_d,duty_c,waste_gate,speed,fluid_l;
    private Button save;
    private int units_value,toggle_value;
    private String final_value;
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
        setContentView(R.layout.activity_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mini display settings");
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
        graph_d = (RadioButton)findViewById(R.id.graph_display);
        peek_d  = (RadioButton)findViewById(R.id.peek_displpay);
        duty_c = (RadioButton)findViewById(R.id.duty_cycle);
        waste_gate = (RadioButton)findViewById(R.id.waste_gate);
        speed  = (RadioButton)findViewById(R.id.speed);
        fluid_l = (RadioButton)findViewById(R.id.fluid_level);

        save = (Button)findViewById(R.id.display_save);
        save.setOnClickListener(this);



    }
    public void onRadioButtonClick(View view) {
        boolean checked = ((RadioButton)view).isChecked();
        units_value = 0;

        switch (view.getId()){

            case R.id.radio_psi:
                if(checked){
                    units_value =1;
                    psi.setChecked(true);
                    Log.d("psi","checked");
                }
                break;
            case R.id.radio_bar:
                if(checked){
                    units_value =2;
                    bar.setChecked(true);
                    Log.d("bar","checked");
                }
                break;
            case R.id.radio_kpa:
                if(checked){
                    units_value =3;
                    kpa.setChecked(true);
                    Log.d("kpa","checked");
                }
                break;


        }
    }

    public void onRadiotoggleButtonClick(View view) {
        boolean checked = ((RadioButton)view).isChecked();
        toggle_value = 0;
        switch (view.getId()) {
            case R.id.graph_display:
                if (checked) {
                    toggle_value = 1;
                    graph_d.setChecked(true);
                    Log.d("graph_display", "checked");
                }
                break;
            case R.id.peek_displpay:
                if (checked) {
                    toggle_value = 2;
                    peek_d.setChecked(true);
                    Log.d("peek_display", "checked");
                }
                break;
            case R.id.duty_cycle:
                if (checked) {
                    toggle_value = 3;
                    duty_c.setChecked(true);
                    Log.d("duty_cycle", "checked");
                }
                break;
            case R.id.waste_gate:
                if (checked) {
                    toggle_value = 4;
                    waste_gate.setChecked(true);
                    Log.d("kpa", "checked");
                }
                break;
            case R.id.speed:
                if (checked) {
                    toggle_value = 5;
                    speed.setChecked(true);
                    Log.d("waste_gate", "checked");
                }
                break;
            case R.id.fluid_level:
                if (checked) {
                    toggle_value = 6;
                    fluid_l.setChecked(true);
                    Log.d("fluid_level", "checked");
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
         switch (id){
             case R.id.display_save:
                 Toast.makeText(Display.this,""+toggle_value+""+units_value,Toast.LENGTH_SHORT).show();
                 if(units_value == 0 || toggle_value == 0){
                    Toast.makeText(Display.this,"Select any options to save the value",Toast.LENGTH_SHORT).show();
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
                     sharedpreferences.writeUnitsValue(units_value);
                     sharedpreferences.writeToggleValue(toggle_value);
                     final_value = sharedpreferences.readUnitsValue()+"_"+sharedpreferences.readToggleValue();
                     Toast.makeText(Display.this,final_value,Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Display.this, final_value+"", Toast.LENGTH_SHORT).show();
                mBluetoothLeScanner.stopScan(mScanCallback);
                Intent intent = new Intent(Display.this,Scan_activity.class);
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
