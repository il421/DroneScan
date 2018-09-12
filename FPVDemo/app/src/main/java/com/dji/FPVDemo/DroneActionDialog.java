package com.dji.FPVDemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class DroneActionDialog extends DialogFragment {

    private EditText distanceEditTxt;
    private Spinner commandSpnr, directionSpnr;
    private DroneActionDialogListener dialogListener;
    private View view;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.add_action_dialog, null);

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
        setCommandSpinner();
        return builder.create();
    }

    public void setCommandSpinner() {
        Log.v(this.getClass().getName(), "setCommandSpinner func");
        commandSpnr = (Spinner) view.findViewById(R.id.spnr_drone_commands);
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
                switch (position) {
                    case 0:
                        Toast.makeText(getActivity().getBaseContext(), "TAKEOFF clicked", Toast.LENGTH_SHORT);
                        break;
                    case 1:
                        Toast.makeText(getActivity().getBaseContext(), "YAW clicked", Toast.LENGTH_SHORT);
                        break;
                    case 2:
                        Toast.makeText(getActivity().getBaseContext(), "MOVE clicked", Toast.LENGTH_SHORT);
                        break;
                    case 3:
                        Toast.makeText(getActivity().getBaseContext(), "SCAN clicked", Toast.LENGTH_SHORT);
                        break;
                    case 4:
                        Toast.makeText(getActivity().getBaseContext(), "LAND clicked", Toast.LENGTH_SHORT);
                        break;
                    default:
                        break;

                }
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
