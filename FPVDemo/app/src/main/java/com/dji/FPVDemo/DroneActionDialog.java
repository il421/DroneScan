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
import android.widget.RadioButton;
import android.widget.Spinner;

public class DroneActionDialog extends DialogFragment {

    private View view;
    private String command;
    private DroneActionDetailsDlgListener detailsDlgListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        command = getTag();
        int resourceId = getLayout();
        view = inflater.inflate(resourceId, null);
        detailsDlgListener = (DroneActionDetailsDlgListener) getActivity();

        builder.setView(view)
                .setTitle("Please set values for " + getTag() + " action")
                .setPositiveButton("Add Action", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // dialogListener = (DroneActionDialogListener) getActivity();
                        if(command.equals("Yaw")) {
                            RadioButton rBtn = (RadioButton) getDialog().findViewById(R.id.opt_left);
                            Direction dir = (rBtn.isChecked()) ? Direction.Left : Direction.Right;
                            detailsDlgListener.onFinishActionDetailsDlg(new DroneAction(dir));
                        } else if (command.equals("Move")) {
                            Spinner directionSpnr = (Spinner) getDialog().findViewById(R.id.spnr_directions);
                            // check direction
                            String dir = directionSpnr.getSelectedItem().toString();
                            EditText dis = (EditText) getDialog().findViewById(R.id.distance_input);
                            detailsDlgListener.onFinishActionDetailsDlg(new DroneAction(dir, Float.parseFloat(dis.getText().toString())));
                        } else {
                            RadioButton rBtn = (RadioButton) getDialog().findViewById(R.id.opt_left);
                            Direction dir = (rBtn.isChecked()) ? Direction.Left : Direction.Right;
                            EditText minH = (EditText) getDialog().findViewById(R.id.min_height_input);
                            EditText maxH = (EditText) getDialog().findViewById(R.id.max_height_input);
                            EditText minW = (EditText) getDialog().findViewById(R.id.min_width_input);
                            EditText maxW = (EditText) getDialog().findViewById(R.id.max_width_input);
                            detailsDlgListener.onFinishActionDetailsDlg(new DroneAction(Float.parseFloat(minW.getText().toString()),
                                    Float.parseFloat(maxW.getText().toString()), Float.parseFloat(minH.getText().toString()),
                                            Float.parseFloat(maxH.getText().toString()), dir.toString()));
                        }
                        // SET DIALOG LISTENER

//                        dialogListener = (DroneActionDialogListener) getActivity();
//                        dialogListener.onFinishActionDialog(distanceEditTxt.getText().toString(), directionSpnr.getSelectedItem().toString());

                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
//                        dialogListener.onFinishActionDialog("", "");
                    }
                });
        return builder.create();

    }

    public int getLayout() {
        int layout = 0;
        switch (command) {
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

//    public interface DroneActionDialogListener {
//        void onFinishActionDialog(String distance, String direction);
//    }

}
