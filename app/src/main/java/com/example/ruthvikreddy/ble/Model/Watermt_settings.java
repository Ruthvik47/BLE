package com.example.ruthvikreddy.ble.Model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ruthvikreddy.ble.R;

public class Watermt_settings extends AppCompatDialogFragment implements View.OnClickListener {
    private Button empty,full;
    private Watermt_settings_listener listener;
    private Boolean tankValue = true;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.watermt_dialog,null);
        builder.setView(view).setTitle("Advanced Options");
        empty = (Button)view.findViewById(R.id.empty_tank);
        full = (Button)view.findViewById(R.id.fill_tank);
        empty.setOnClickListener(this);
        full.setOnClickListener(this);

    return builder.create();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.empty_tank:
                listener.sendINt(0);
                Toast.makeText(getActivity(),"Tank is empty",Toast.LENGTH_LONG).show();
                break;
            case R.id.fill_tank:
                listener.sendINt(1);
                Toast.makeText(getActivity(),"Tank is full",Toast.LENGTH_LONG).show();
                break;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (Watermt_settings_listener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"this should be implemented");
        }

    }

    public interface Watermt_settings_listener{
        void sendINt(int value);
    }
}
