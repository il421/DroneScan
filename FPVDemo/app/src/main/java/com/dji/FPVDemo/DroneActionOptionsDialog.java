package com.dji.FPVDemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class DroneActionOptionsDialog extends DialogFragment {

    private DroneActionDetailsDlgListener detailsDlglistener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose a drone action " + getTag())
                .setItems(R.array.commands_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int option) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        Log.v(this.getClass().toString(), "selected: " + option);
                        detailsDlglistener = (DroneActionDetailsDlgListener) getActivity();
                        detailsDlglistener.onFinishActionOptionsDlg(option);
                    }
                });
        return builder.create();
    }
}
