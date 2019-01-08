package com.example.ruthvikreddy.ble.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ruthvikreddy.ble.Model.Sharedpreferences;
import com.example.ruthvikreddy.ble.R;
import com.ifttt.Applet;
import com.ifttt.ErrorResponse;
import com.ifttt.IftttApiClient;
import com.ifttt.api.AppletsApi;
import com.ifttt.api.PendingResult;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Button iftt_button;

    private Thread thread;
    private boolean plotData = true;
    private GraphView graph;
    private final Handler mgraphHandler = new Handler();
    private Runnable mTimer;
    private LineGraphSeries<DataPoint> mSeries;
    private LineGraphSeries<DataPoint> series2;
    private double graph2LastXValue = 5d;
    private Sharedpreferences sharedpreferences;

    //ble connect----
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler;
    // private String UUID =  "00001207-0000-1000-8000-00805f9b34fb";
    private String mac = "D5:13:3B:E6:78:30";


    private Button stop;

    //private RecyclerView recyclerView;
    //private DeviceAdapter deviceAdapter;
    //private ArrayList<String> deviceModelList = new ArrayList<>();

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private static final int REQUEST_CODE = 200;
    private boolean mScanning;

    private static final int RQS_ENABLE_BLUETOOTH = 1;
    private ScanSettings settings;
    private ArrayList<ScanFilter> filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("BLE bluetooth");

        sharedpreferences = new Sharedpreferences(MainActivity.this);
        Log.d("Shared",sharedpreferences.readUnitsValue()+"");

//        iftt_button = (Button)findViewById(R.id.ifttt);
//        iftt_button.setOnClickListener(this);
        //Line chart
       // mChart = (LineChart)findViewById(R.id.chart1);



      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //navigationView.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);

        //graph view Initialization

        //----------------------------ble connect --------------------------------------------
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this,
                    "BLUETOOTH_LE not supported in this device!",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        getBluetoothAdapterAndLeScanner();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,
                    "ble is not supported in your device",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        mHandler = new Handler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            }
        }
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        filters = new ArrayList<>();
        //ParcelUuid uu = new ParcelUuid(UUID.fromString("00001207-0000-1000-8000-00805f9b34fb"));
        ScanFilter filter = new ScanFilter.Builder().setDeviceName("bluino").build();
        filters.add(filter);
      //  scanLeDevice(true);
        //---end of ble connect


        graph = (GraphView) findViewById(R.id.graph);
        mSeries = new LineGraphSeries<>();
         series2 = new LineGraphSeries<>();
         series2.setColor(Color.RED);
         graph.addSeries(series2);
        graph.addSeries(mSeries);
        graph.getViewport().setXAxisBoundsManual(true);
       graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(40);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        GridLabelRenderer r = graph.getGridLabelRenderer();
        r.setPadding(32);
        graph.getViewport().setYAxisBoundsManual(true);
        int value = sharedpreferences.readUnitsValue();
        if(value == 1){
            setBounds(0.0,10.0);
        }else if(value ==2){
            setBounds(0.06,0.1);
        }else if(value == 3){
            setBounds(6.8,30.0);
        }


//        graph.getViewport().setScalableY(true);
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
       // graph.getViewport().setScalableY(true);

    }


    private void setBounds(double i, double i1) {
        int value = sharedpreferences.readUnitsValue();
        if(value == 1){
            graph.getViewport().setMinY(i);
            graph.getViewport().setMaxY(i1);
        }else if(value ==2){
            graph.getViewport().setMinX(i);
            graph.getViewport().setMaxY(i1);
        }else if(value == 3){
            graph.getViewport().setMinX(i);
            graph.getViewport().setMaxY(i1);
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RQS_ENABLE_BLUETOOTH && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        getBluetoothAdapterAndLeScanner();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported in your device", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }else{
            // Toast.makeText(this,"Bluetooth is supported in your device",Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE){

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission granted

            }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)){
                    //Show an explanation to the user *asynchronously*
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
                }else {
                    Toast.makeText(this, "Location permission missing available devices will not appear", Toast.LENGTH_LONG).show();
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
              Toast.makeText(MainActivity.this, result.getDevice()+"", Toast.LENGTH_SHORT).show();
              mBluetoothLeScanner.stopScan(mScanCallback);
                Intent intent = new Intent(MainActivity.this,Scan_activity.class);
                //sharedpreferences.writeMacAddress(""+result.getDevice());
                intent.putExtra("deviceName",result.getDevice().getAddress());
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Shared",sharedpreferences.readUnitsValue()+"");
        int value = sharedpreferences.readUnitsValue();
        if(value == 1){
            setBounds(0.0,10.0);
        }else if(value ==2){
            setBounds(0.06,0.1);
        }else if(value == 3){
            setBounds(6.8,30.0);
        }

        mTimer = new Runnable() {
            @Override
            public void run() {
                graph2LastXValue += 1d;
                int yvalue =2;
                int x =0;
               // Log.d("x and y",""+x+""+yvalue+" "+graph2LastXValue+"    "+getRandom());
                mSeries.appendData(new DataPoint(graph2LastXValue, getRandom()), true, 40);
                series2.appendData(new DataPoint(graph2LastXValue,3),true,40);
                mgraphHandler.postDelayed(this, 100);
            }
        };
        mgraphHandler.postDelayed(mTimer, 1000);
        //--------------------Ble connect-------------------
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, RQS_ENABLE_BLUETOOTH);
            }
        }else{
            // scanLeDevice(true);
        }
        if(mScanning){
            Log.d("scanning","true");

        }else{
            Log.d("scanning","false");
        }
        //-------------end of connect------------------

    }
    @Override
    public void onPause() {
        mgraphHandler.removeCallbacks(mTimer);
        super.onPause();
       /* ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(),0);*/
    }
    double mLastRandom = 2;
    Random mRand = new Random();
    private double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.search:
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
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_display) {
            startActivity(new Intent(MainActivity.this,Display.class));
            // Handle the camera action
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this,General_settings.class));
        } else if (id == R.id.nav_boostControl) {
            startActivity(new Intent(MainActivity.this,BoostControl.class));
        }  else if (id == R.id.nav_waterMethanol) {
            startActivity(new Intent(MainActivity.this,WaterMethanol.class));

        } else if (id == R.id.nav_transaction_control) {
            startActivity(new Intent(MainActivity.this,Transaction_control.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View v) {

    }
}
