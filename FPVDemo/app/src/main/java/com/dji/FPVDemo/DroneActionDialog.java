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

    private View view;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        int resourceId = getLayout(getTag());
        view = inflater.inflate(resourceId, null);

        builder.setView(view)
                .setTitle("Please set values for " + getTag() + " action")
                .setPositiveButton("Add Action", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

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

//    public interface DroneActionDialogListener {
//        void onFinishActionDialog(String distance, String direction);
//    }
}
