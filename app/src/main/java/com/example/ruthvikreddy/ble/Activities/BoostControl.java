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

import com.example.ruthvikreddy.ble.Model.DialogClass;
import com.example.ruthvikreddy.ble.Model.Sharedpreferences;
import com.example.ruthvikreddy.ble.R;

import java.util.ArrayList;

public class BoostControl extends AppCompatActivity implements View.OnClickListener,DialogClass.DialogClassListener{
    private Button advanced_options,save;
    private EditText main_boost,peak_reset,max_boost;
    String kp_string,kd_string,ki_string;
    private String final_boostControlValue;
    private Sharedpreferences sharedpreferences;

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
        setContentView(R.layout.activity_boost_control);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Boost Control");
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

        advanced_options = (Button)findViewById(R.id.advanced_options);
        save = (Button)findViewById(R.id.save);
        main_boost = (EditText)findViewById(R.id.target);
        peak_reset = (EditText)findViewById(R.id.peakTime);
        max_boost = (EditText)findViewById(R.id.safetyPressure);
        advanced_options.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    public void onRadioButtonClick(View view) {
        boolean checked = ((RadioButton)view).isChecked();

        switch (view.getId()){
            case R.id.radio_psi:
                if(checked){
                    Log.d("psi","checked");
                }
                break;
            case R.id.radio_bar:
                if(checked){
                    Log.d("bar","checked");
                }
                break;

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.advanced_options:
                DialogClass dialogClass = new DialogClass();
                dialogClass.show(getSupportFragmentManager(),"Dialog class");
                break;
            case R.id.save:
                String mainBoost_string = main_boost.getText().toString();
                String peekReset_string = peak_reset.getText().toString();
                String maxBoost_string = max_boost.getText().toString();
                if(mainBoost_string.isEmpty() || peekReset_string.isEmpty() || maxBoost_string.isEmpty()){
                    Toast.makeText(BoostControl.this,"fill all the fields",Toast.LENGTH_SHORT).show();
                }else{
                    if(kp_string.isEmpty() || kd_string.isEmpty() || ki_string.isEmpty()){
                        Toast.makeText(BoostControl.this,"enter advanced settings fields",Toast.LENGTH_SHORT).show();
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
                        sharedpreferences.writeBoostActivityValue(mainBoost_string+"_"+peekReset_string
                                +"_"+maxBoost_string+"_"+kp_string+"_"+kd_string+"_"+ki_string);
                       // Toast.makeText(BoostControl.this,mainBoost_string+"_"+peekReset_string
                              //  +"_"+maxBoost_string+"_"+kp_string+"_"+kd_string+"_"+ki_string,Toast.LENGTH_SHORT).show();
                        final_boostControlValue = mainBoost_string+"_"+peekReset_string
                                +"_"+maxBoost_string+"_"+kp_string+"_"+kd_string+"_"+ki_string;
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
               // Toast.makeText(BoostControl.this, final_boostControlValue+"", Toast.LENGTH_SHORT).show();
                mBluetoothLeScanner.stopScan(mScanCallback);
                Intent intent = new Intent(BoostControl.this,Scan_activity.class);
                //sharedpreferences.writeMacAddress(""+result.getDevice());
                intent.putExtra("deviceName",result.getDevice().getAddress());
                intent.putExtra("data",final_boostControlValue);
                startActivity(intent);
            }

            //  deviceAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void sendData(String kp, String kd, String ki) {
        kp_string = kp;
        kd_string = kd;
        ki_string = ki;
        Log.d("kp",kp);
        Log.d("kd",kd);
        Log.d("ki",ki);
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
