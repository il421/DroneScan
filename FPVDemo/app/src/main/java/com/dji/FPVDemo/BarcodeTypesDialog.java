package com.dji.FPVDemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import java.util.ArrayList;

public class BarcodeTypesDialog extends DialogFragment {


    private BarcodeTypesDlgListener barcodeTypesDlgListener;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        setCheckedItems();
        // Set the dialog title
        builder.setTitle("Select barcode type to scan")
                .setItems(R.array.barcode_types_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int option) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        Log.v(this.getClass().toString(), "selected barcode type: " + option);
                        barcodeTypesDlgListener = (BarcodeTypesDlgListener) getActivity();
                        barcodeTypesDlgListener.onFinishBarcodeTypeDlg(option);
                    }
                });

        return builder.create();
    }

    public interface BarcodeTypesDlgListener {
        void onFinishBarcodeTypeDlg(int position);
    }
}
