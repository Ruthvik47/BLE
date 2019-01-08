package com.example.ruthvikreddy.ble.Model;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ruthvikreddy.ble.R;

public class Sharedpreferences {
    private Context context;
    private SharedPreferences sharedPreferences;

    public Sharedpreferences(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.pref_file),Context.MODE_PRIVATE);

    }

    //----------------Mini display shared preference-------------------
    public void writeUnitsValue(int status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getResources().getString(R.string.Display_Units_int_value),status);
        editor.commit();
    }
    public void writeToggleValue(int status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(context.getResources().getString(R.string.Display_toggle_int_value),status);
        editor.commit();
    }


    public int readUnitsValue(){
        int status = 1;
        status = sharedPreferences.getInt(context.getResources().getString(R.string.Display_Units_int_value),1);
        return status;
    }
    public int readToggleValue(){
        int status = 1;
        status = sharedPreferences.getInt(context.getResources().getString(R.string.Display_toggle_int_value),1);
        return status;
    }

    //------------end of mini display shared preference------------------
    //-------------General settings ----------------------
    public void writeDisplayActivityValue(String status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.general_settings),status);
        editor.commit();
    }
    public String readDisplayActivityValue(){
        String status = "1_1_1";
        status = sharedPreferences.getString(context.getResources().getString(R.string.general_settings),status);
        return status;
    }
    //--------------general settings -------------------
    //-----------Boost control-----------------------
    public void writeBoostActivityValue(String status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.boost_control),status);
        editor.commit();
    }
    public String readBoostActivityValue(){
        String status = "1_1_1_1_1";
        status = sharedPreferences.getString(context.getResources().getString(R.string.boost_control),status);
        return status;
    }
    //----------------boost control-----------------
    //--------------water methanol--------------
    public void writeWaterMethanolActivityValue(String status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.water_methanol_activity),status);
        editor.commit();
    }
    public String readWaterMethanolActivityValue(){
        String status = "1_1_1_1_1";
        status = sharedPreferences.getString(context.getResources().getString(R.string.water_methanol_activity),status);
        return status;
    }
    //---------------water methanol------------------------
    //----------------transaction control-----------------
    public void writeTransactionControlActivityValue(String status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.transaction_control),status);
        editor.commit();
    }
    public String readTransactionControlActivityValue(){
        String status = "1_1_1_1_1";
        status = sharedPreferences.getString(context.getResources().getString(R.string.transaction_control),status);
        return status;
    }



    public void writeMacAddress(String status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.mac_address),status);
        editor.commit();
    }
    public String readMacAddress(){
        String name = "D5:13:3B:E6:78:30";
        name = sharedPreferences.getString(context.getResources().getString(R.string.device_mac_address),name);
        return name;
    }

}
