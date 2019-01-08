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
import com.example.ruthvikreddy.ble.Model.Watermt_settings;
import com.example.ruthvikreddy.ble.R;

import java.util.ArrayList;

public class WaterMethanol extends AppCompatActivity implements View.OnClickListener,Watermt_settings.Watermt_settings_listener {
    private Button advanced_settings,save;
    private EditText activation_pressure,empty_safe;
    private EditText psiRatio,psiRatio2,psiRatio3,psiRatio4,psiRatio5,
            dutycycle,dutycycle2,dutycycle3,dutycycle4,dutycycle5;
    private int water_int = 2;

    private Sharedpreferences sharedpreferences;
    private String final_waterValue;


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
        setContentView(R.layout.activity_water_methanol);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Water Methanol Injection");
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

        activation_pressure = (EditText)findViewById(R.id.startPressure);
        empty_safe = (EditText)findViewById(R.id.emptytank);
        psiRatio = (EditText)findViewById(R.id.psi_ratio);
        psiRatio2 = (EditText)findViewById(R.id.psi_ratio2);
        psiRatio3 = (EditText)findViewById(R.id.psi_ratio3);
        psiRatio4 = (EditText)findViewById(R.id.psi_ratio4);
        psiRatio5 = (EditText)findViewById(R.id.psi_ratio5);
        dutycycle = (EditText)findViewById(R.id.dutycycle);
        dutycycle2 = (EditText)findViewById(R.id.dutycycle2);
        dutycycle3 = (EditText)findViewById(R.id.dutycycle3);
        dutycycle4 = (EditText)findViewById(R.id.dutycycle4);
        dutycycle5 = (EditText)findViewById(R.id.dutycycle5);


        advanced_settings = (Button)findViewById(R.id.advanced_settings);
        save = (Button)findViewById(R.id.save);
        advanced_settings.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.advanced_settings:
                Watermt_settings settings = new Watermt_settings();
                settings.show(getSupportFragmentManager(),"waterml_settings");
                break;
            case R.id.save:
                String pressure_string = activation_pressure.getText().toString();
                String empty_string = empty_safe.getText().toString();
                String psi_one = psiRatio.getText().toString();
                String psi_two = psiRatio2.getText().toString();
                String psi_threee = psiRatio3.getText().toString();
                String psi_four = psiRatio4.getText().toString();
                String psi_five = psiRatio5.getText().toString();
                String duty_one = dutycycle.getText().toString();
                String duty_two = dutycycle2.getText().toString();
                String duty_three = dutycycle3.getText().toString();
                String duty_four = dutycycle4.getText().toString();
                String duty_five = dutycycle5.getText().toString();
                if(pressure_string.isEmpty() || empty_string.isEmpty()|| psi_one.isEmpty() || psi_two.isEmpty() || psi_threee.isEmpty() || psi_four.isEmpty()|| psi_five.isEmpty()
                        || psi_five.isEmpty() || duty_one.isEmpty() || duty_two.isEmpty() || duty_three.isEmpty()||duty_four.isEmpty()||duty_five.isEmpty()){
                    Toast.makeText(WaterMethanol.this,"Fill all the fields",Toast.LENGTH_SHORT).show();
                }else{
                    if(water_int == 2){
                        Toast.makeText(WaterMethanol.this,"Select advanced settings",Toast.LENGTH_SHORT).show();
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

                        final_waterValue = pressure_string+"_"+empty_string+"_"+psi_one+"_"+psi_two+"_"+psi_threee+"_"+psi_four+"_"+psi_five+"_"+duty_one
                                +"_"+duty_two+"_"+duty_three+"_"+duty_four+"_"+duty_five+"_"+water_int;
                        sharedpreferences.writeWaterMethanolActivityValue(final_waterValue);
                        //Toast.makeText(WaterMethanol.this,final_waterValue,Toast.LENGTH_SHORT).show();
                    }

                }

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
                //Toast.makeText(WaterMethanol.this, final_waterValue+"", Toast.LENGTH_SHORT).show();
                mBluetoothLeScanner.stopScan(mScanCallback);
                Intent intent = new Intent(WaterMethanol.this,Scan_activity.class);
                //sharedpreferences.writeMacAddress(""+result.getDevice());
                intent.putExtra("deviceName",result.getDevice().getAddress());
                intent.putExtra("data",final_waterValue);
                startActivity(intent);
            }

            //  deviceAdapter.notifyDataSetChanged();
        }
    };


    @Override
    public void sendINt(int value) {
        water_int = value;


    }
    private void getBluetoothAdapterAndLeScanner(){
        // Get BluetoothAdapter and BluetoothLeScanner.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        //  mScanning = false;
    }
}
