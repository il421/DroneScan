package com.dji.FPVDemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class DroneActionDialog extends DialogFragment {

    private EditText distanceEditTxt;
    private Spinner direction;
    private DroneActionDialogListener dialogListener;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_action_dialog, null);

        builder.setView(view)
                .setPositiveButton("Add Action", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        distanceEditTxt = (EditText) getDialog().findViewById(R.id.distance_input);
                        direction = (Spinner) getDialog().findViewById(R.id.spnr_directions);
                        // FIRE ZE MISSILES!
//                        Toast.makeText(getContext(), "SELECTED: " + distanceEditTxt.getText() + " " + direction.getSelectedItem().toString(),
//                                Toast.LENGTH_SHORT).show();
                        dialogListener = (DroneActionDialogListener) getActivity();
                        dialogListener.onFinishActionDialog(distanceEditTxt.getText().toString(), direction.getSelectedItem().toString());
//                        this.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialogListener.onFinishActionDialog("", "");
                    }
                });

        return builder.create();
    }

    public interface DroneActionDialogListener {
        void onFinishActionDialog(String distance, String direction);
    }
}
