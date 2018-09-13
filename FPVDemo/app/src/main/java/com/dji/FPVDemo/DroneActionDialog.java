package com.dji.FPVDemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class DroneActionDialog extends DialogFragment {

    private EditText distanceEditTxt;
    private Spinner commandSpnr, directionSpnr;
    private DroneActionDialogListener dialogListener;
    private View view;
    private LinearLayout cmdDetails;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        int resourceId = getLayout(getTag());
//        view = inflater.inflate(R.layout.add_action_dialog, null);
        view = inflater.inflate(resourceId, null);

        builder.setView(view)
                .setPositiveButton("Add Action", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        distanceEditTxt = (EditText) getDialog().findViewById(R.id.distance_input);
                        directionSpnr = (Spinner) getDialog().findViewById(R.id.spnr_directions);

                        // FIRE ZE MISSILES!

                        dialogListener = (DroneActionDialogListener) getActivity();
                        dialogListener.onFinishActionDialog(distanceEditTxt.getText().toString(), directionSpnr.getSelectedItem().toString());

                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
//                        dialogListener.onFinishActionDialog("", "");
                    }
                });
//        setCommandSpinner();
        return builder.create();

        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose a drone action")
                .setItems(R.array.commands_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int option) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        Log.v(this.getClass().toString(), "selected: " + option);

                    }
                });

        return builder.create(); */
    }

    public int getLayout(String tag) {
        int layout = 0;
        switch (tag) {
            case "Yaw":
                layout = R.layout.yaw_detail;
                break;
            case "Move":
                layout = R.layout.move_detail;
                break;
            case "Scan":
                layout = R.layout.scan_detail;
                break;
            default:
                break;
        }
        return layout;
    }

    public void setCommandSpinner() {
        Log.v(this.getClass().getName(), "setCommandSpinner func");
        commandSpnr = (Spinner) view.findViewById(R.id.spnr_drone_commands);
        cmdDetails = (LinearLayout) view.findViewById(R.id.action_detail_layout);
        cmdDetails.removeAllViews();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity().getBaseContext(),
                R.array.commands_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        commandSpnr.setAdapter(adapter);

        commandSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Log.v("CMD SPNR", " pos " + position);
                /*
                cmdDetails.removeAllViews();
                View child;

                switch (position) {
                    case 1:
                        child = getLayoutInflater().inflate(R.layout.yaw_detail, null);
                        cmdDetails.setFocusable(true);
                        cmdDetails.addView(child);

                        break;
                    case 2:
                        child = getLayoutInflater().inflate(R.layout.move_detail, null);
                        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        cmdDetails.setFocusable(true);
                        cmdDetails.addView(child);
                        break;
                    case 3:
                        child = getLayoutInflater().inflate(R.layout.scan_detail, null);
                        cmdDetails.setFocusable(true);
                        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        cmdDetails.addView(child);
                        break;
                    default:
                        break;
                } */
//                getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here

            }

        });
    }

    public interface DroneActionDialogListener {
        void onFinishActionDialog(String distance, String direction);
    }
}
