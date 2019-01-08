package com.example.ruthvikreddy.ble.Model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ruthvikreddy.ble.R;

public class DialogClass extends AppCompatDialogFragment {
    EditText kp,kd,ki;
    private DialogClassListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout,null);
        builder.setView(view).setTitle("Advanced Options")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String kp_text = kp.getText().toString();
                String kd_text = kd.getText().toString();
                String ki_text = ki.getText().toString();

                    listener.sendData(kp_text,kd_text,ki_text);


            }
        });
        kp = (EditText)view.findViewById(R.id.kp);
        kd = (EditText)view.findViewById(R.id.kd);
        ki = (EditText)view.findViewById(R.id.ki);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener =(DialogClassListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"this must implement");
        }
    }

    public interface DialogClassListener{
        void sendData(String kp,String kd,String ki);
    }
}
