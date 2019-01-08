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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ruthvikreddy.ble.Model.Sharedpreferences;
import com.example.ruthvikreddy.ble.R;

import java.util.ArrayList;

public class Transaction_control extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout slip_relativeLayout;
    private RelativeLayout speed_relativeLayout;
    private RelativeLayout time_relativeLayout;
    private RelativeLayout distance_relativeLayout;
    private RelativeLayout button_relative;
    private Button save_button;
     private Boolean save_boolean;
     int save_int = 0;
     private String final_value;
     private Scan_activity scan_activity;
     private MainActivity mainActivity;

    private Sharedpreferences sharedpreferences;
    private EditText speed_ratio1,speed_ratio2,speed_ratio3,speed_ratio4,speed_ratio5,boot,boot2,boot3,boot4,boot5;

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
        setContentView(R.layout.activity_transaction_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Transaction Control");
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


        speed_ratio1 = (EditText)findViewById(R.id.speed_ratio);
        speed_ratio2 = (EditText)findViewById(R.id.speed_ratio2);
        speed_ratio3 = (EditText)findViewById(R.id.speed_ratio3);
        speed_ratio4 = (EditText)findViewById(R.id.speed_ratio4);
        speed_ratio5 = (EditText)findViewById(R.id.speed_ratio5);

        boot = (EditText)findViewById(R.id.boost);
        boot2 = (EditText)findViewById(R.id.boost2);
        boot3 = (EditText)findViewById(R.id.boost3);
        boot4 = (EditText)findViewById(R.id.boost4);
        boot5 = (EditText)findViewById(R.id.boost5);



        save_button = (Button)findViewById(R.id.save);
        save_button.setOnClickListener(this);
        slip_relativeLayout = (RelativeLayout)findViewById(R.id.slip_relative_layout);
        speed_relativeLayout = (RelativeLayout)findViewById(R.id.speed_relative_layout);
        time_relativeLayout =(RelativeLayout)findViewById(R.id.time_relative_layout);
        distance_relativeLayout = (RelativeLayout)findViewById(R.id.distance_relative_layout);
        button_relative = (RelativeLayout)findViewById(R.id.button_relative_layout);
        speed_relativeLayout.setVisibility(View.GONE);
        slip_relativeLayout.setVisibility(View.GONE);
        time_relativeLayout.setVisibility(View.GONE);
        distance_relativeLayout.setVisibility(View.GONE);
        button_relative.setVisibility(View.GONE);
    }

    public void onRadioButtonClick(View view) {
        boolean checked = ((RadioButton)view).isChecked();

        switch (view.getId()){
            case R.id.radio_speed:
                if(checked){
                    Log.d("speed","checked");
                    speed_relativeLayout.setVisibility(View.VISIBLE);
                    button_relative.setVisibility(View.VISIBLE);
                    slip_relativeLayout.setVisibility(View.GONE);
                    time_relativeLayout.setVisibility(View.GONE);
                    distance_relativeLayout.setVisibility(View.GONE);



                    save_int = 1;



                }
                break;
            case R.id.radio_time:
                if(checked){
                    Log.d("time","checked");
                    time_relativeLayout.setVisibility(View.VISIBLE);
                    button_relative.setVisibility(View.VISIBLE);
                    slip_relativeLayout.setVisibility(View.GONE);
                    speed_relativeLayout.setVisibility(View.GONE);
                    distance_relativeLayout.setVisibility(View.GONE);

                    save_int = 2;


                }
                break;
            case R.id.radio_distance:
                if(checked){
                    Log.d("distance","checked");
                    distance_relativeLayout.setVisibility(View.VISIBLE);
                    button_relative.setVisibility(View.VISIBLE);
                    speed_relativeLayout.setVisibility(View.GONE);
                    time_relativeLayout.setVisibility(View.GONE);
                    slip_relativeLayout.setVisibility(View.GONE);

                    save_int = 3;

                }
                break;
            case R.id.radio_slip:
                if(checked){
                    Log.d("slip","checked");
                    slip_relativeLayout.setVisibility(View.VISIBLE);
                    button_relative.setVisibility(View.VISIBLE);
                    speed_relativeLayout.setVisibility(View.GONE);
                    time_relativeLayout.setVisibility(View.GONE);
                    distance_relativeLayout.setVisibility(View.GONE);

                    save_int = 4;

                }

                break;
                case R.id.none:
                if(checked){
                    Log.d("none","checked");
                    slip_relativeLayout.setVisibility(View.GONE);
                    speed_relativeLayout.setVisibility(View.GONE);
                    time_relativeLayout.setVisibility(View.GONE);
                    button_relative.setVisibility(View.GONE);
                    distance_relativeLayout.setVisibility(View.GONE);
                }

                break;

        }
    }


    @Override()
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.save:
                if(save_int == 1){
                    String speed_ratio = speed_ratio1.getText().toString();
                    String speed_ratio_2 = speed_ratio2.getText().toString();
                    String speed_ratio_3 = speed_ratio3.getText().toString();
                    String speed_ratio_4 = speed_ratio4.getText().toString();
                    String speed_ratio_5 = speed_ratio5.getText().toString();
                    String boot_1 = boot.getText().toString();
                    String boot_2 = boot2.getText().toString();
                    String boot_3 = boot3.getText().toString();
                    String boot_4 = boot4.getText().toString();
                    String boot_5 = boot5.getText().toString();
                    if(speed_ratio.isEmpty() ||speed_ratio_2.isEmpty() ||speed_ratio_3.isEmpty() ||speed_ratio_4.isEmpty() ||speed_ratio_5.isEmpty() ||
                            boot_1.isEmpty() ||boot_2.isEmpty() ||boot_3.isEmpty() ||boot_4.isEmpty() ||boot_5.isEmpty() ){
                        // Toast.makeText(Transaction_control.this,"Fill all the V-Speed fields",Toast.LENGTH_SHORT).show();
                        //  save_boolean = false;
                        Toast.makeText(Transaction_control.this,"Fill the fields of V-speed"+save_int,Toast.LENGTH_SHORT).show();
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
                       // save_int = 1;
                        final_value = speed_ratio+"_"+speed_ratio_2+"_"+speed_ratio_3+"_"+"_"+speed_ratio_4+"_"+speed_ratio_5+"_"+boot_1+"_"+boot_2
                                +"_"+boot_2+"_"+boot_3+"_"+boot_4+"_"+boot_5;

                        Toast.makeText(Transaction_control.this,final_value+""+save_int,Toast.LENGTH_SHORT).show();
                    }
//                    sharedpreferences.writeTransactionControlActivityValue(final_value);
//                    Toast.makeText(Transaction_control.this,sharedpreferences.readTransactionControlActivityValue(),Toast.LENGTH_SHORT).show();
                }else if(save_int == 2){
                    String trigger_speed = ((EditText)findViewById(R.id.trigger_speed_3)).getText().toString();
                    String reset_speed = ((EditText)findViewById(R.id.reset_speed_3)).getText().toString();
                    String seconds_ratio = ((EditText)findViewById(R.id.seconds_ratio)).getText().toString();
                    String seconds_ratio2 = ((EditText)findViewById(R.id.seconds_ratio2)).getText().toString();
                    String seconds_ratio3 = ((EditText)findViewById(R.id.seconds_ratio3)).getText().toString();
                    String seconds_ratio4 = ((EditText)findViewById(R.id.seconds_ratio4)).getText().toString();
                    String seconds_ratio5 = ((EditText)findViewById(R.id.seconds_ratio5)).getText().toString();
                    String Boost = ((EditText)findViewById(R.id.boost_2)).getText().toString();
                    String Boost2 = ((EditText)findViewById(R.id.boost2_2)).getText().toString();
                    String Boost3 = ((EditText)findViewById(R.id.boost3_2)).getText().toString();
                    String Boost4 = ((EditText)findViewById(R.id.boost4_2)).getText().toString();
                    String Boost5 = ((EditText)findViewById(R.id.boost5_2)).getText().toString();

                    if(trigger_speed.isEmpty()|| reset_speed.isEmpty()||seconds_ratio.isEmpty()||seconds_ratio2.isEmpty()||seconds_ratio3.isEmpty()||seconds_ratio4.isEmpty()||seconds_ratio5.isEmpty()||
                            Boost.isEmpty() ||Boost2.isEmpty() ||Boost3.isEmpty() ||Boost4.isEmpty() ||Boost5.isEmpty()  ){
                        Toast.makeText(Transaction_control.this,"Fill the  of time"+save_int,Toast.LENGTH_SHORT).show();

                    }else{
                        if(mBluetoothAdapter!=null){
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
                        final_value = trigger_speed+"_"+reset_speed+"_"+seconds_ratio+"_"+seconds_ratio2+"_"+seconds_ratio3+"_"+seconds_ratio4+"_"+seconds_ratio5
                                +"_"+boot+"_"+boot2+"_"+boot3+"_"+boot4+"_"+boot5;
                        Toast.makeText(Transaction_control.this,final_value+""+save_int,Toast.LENGTH_SHORT).show();
                    }

                }else if(save_int == 3){
                    String trigger_speed = ((EditText)findViewById(R.id.trigger_speed_2)).getText().toString();
                    String reset_speed = ((EditText)findViewById(R.id.reset_speed_2)).getText().toString();
                    String dis_ratio = ((EditText)findViewById(R.id.distance_ratio)).getText().toString();
                    String dis_ratio2 = ((EditText)findViewById(R.id.distance_ratio2)).getText().toString();
                    String dis_ratio3 = ((EditText)findViewById(R.id.distance_ratio3)).getText().toString();
                    String dis_ratio4 = ((EditText)findViewById(R.id.distance_ratio4)).getText().toString();
                    String dis_ratio5 = ((EditText)findViewById(R.id.distance_ratio5)).getText().toString();
                    String Boost = ((EditText)findViewById(R.id.boost_3)).getText().toString();
                    String Boost2 = ((EditText)findViewById(R.id.boost2_3)).getText().toString();
                    String Boost3 = ((EditText)findViewById(R.id.boost3_3)).getText().toString();
                    String Boost4 = ((EditText)findViewById(R.id.boost4_3)).getText().toString();
                    String Boost5 = ((EditText)findViewById(R.id.boost5_3)).getText().toString();
                    if(trigger_speed.isEmpty()|| reset_speed.isEmpty()||dis_ratio.isEmpty()||dis_ratio2.isEmpty()||dis_ratio3.isEmpty()||dis_ratio4.isEmpty()||dis_ratio5.isEmpty()||
                            Boost.isEmpty() ||Boost2.isEmpty() ||Boost3.isEmpty() ||Boost4.isEmpty() ||Boost5.isEmpty()  ){
                        Toast.makeText(Transaction_control.this,"Fill the  of time"+save_int,Toast.LENGTH_SHORT).show();
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

                        final_value = trigger_speed+"_"+reset_speed+"_"+dis_ratio+"_"+dis_ratio2+"_"+dis_ratio3+"_"+dis_ratio4+"_"+dis_ratio5
                                +"_"+boot+"_"+boot2+"_"+boot3+"_"+boot4+"_"+boot5;
                        Toast.makeText(Transaction_control.this,final_value+""+save_int,Toast.LENGTH_SHORT).show();
                    }

                }else if(save_int ==4){
                    String trigger_speed = ((EditText)findViewById(R.id.trigger_speed)).getText().toString();
                    String reset_speed = ((EditText)findViewById(R.id.reset_speed)).getText().toString();
                    String slip_ratio = ((EditText)findViewById(R.id.slip_ratio)).getText().toString();
                    String slip_ratio2 = ((EditText)findViewById(R.id.slip_ratio2)).getText().toString();
                    String slip_ratio3 = ((EditText)findViewById(R.id.slip_ratio3)).getText().toString();
                    String slip_ratio4 = ((EditText)findViewById(R.id.slip_ratio4)).getText().toString();
                    String slip_ratio5 = ((EditText)findViewById(R.id.slip_ratio5)).getText().toString();
                    String Boost = ((EditText)findViewById(R.id.minus_boost)).getText().toString();
                    String Boost2 = ((EditText)findViewById(R.id.minus_boost2)).getText().toString();
                    String Boost3 = ((EditText)findViewById(R.id.minus_boost3)).getText().toString();
                    String Boost4 = ((EditText)findViewById(R.id.minus_boost4)).getText().toString();
                    String Boost5 = ((EditText)findViewById(R.id.dutycycle5)).getText().toString();

                    if(trigger_speed.isEmpty()|| reset_speed.isEmpty()||slip_ratio.isEmpty()||slip_ratio2.isEmpty()||slip_ratio3.isEmpty()||slip_ratio4.isEmpty()||slip_ratio5.isEmpty()||
                            Boost.isEmpty() ||Boost2.isEmpty() ||Boost3.isEmpty() ||Boost4.isEmpty() ||Boost5.isEmpty()  ){
                        Toast.makeText(Transaction_control.this,"Fill the  of time"+save_int,Toast.LENGTH_SHORT).show();
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

                        final_value = trigger_speed+"_"+reset_speed+"_"+slip_ratio+"_"+slip_ratio2+"_"+slip_ratio3+"_"+slip_ratio4+"_"+slip_ratio5
                                +"_"+Boost+"_"+Boost2+"_"+Boost3+"_"+Boost4+"_"+Boost5;
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
                Toast.makeText(Transaction_control.this, final_value+"", Toast.LENGTH_SHORT).show();
                mBluetoothLeScanner.stopScan(mScanCallback);
                Intent intent = new Intent(Transaction_control.this,Scan_activity.class);
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
