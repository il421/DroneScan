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

    private ArrayList<Integer> selectedTypes = new ArrayList<Integer>();

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
        selectedTypes = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle("Select barcode types to scan")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(R.array.barcode_types_array, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    selectedTypes.add(which);
                                } else if (selectedTypes.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    selectedTypes.remove(Integer.valueOf(which));
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        for (Integer num: selectedTypes) {
                            Log.v("type ", "" + num);
                        }
                        JSONObjectHandler barcodeTypesJson = new JSONObjectHandler("barcodeTypes.json");
                        barcodeTypesJson.writeBarcodeTypesJSON(selectedTypes);
                        barcodeTypesJson.createAndSaveFile(getContext());
                        barcodeTypesJson.readJsonData(getContext());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }
}
