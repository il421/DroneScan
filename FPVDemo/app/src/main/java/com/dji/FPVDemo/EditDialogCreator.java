package com.dji.FPVDemo;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

public class EditDialogCreator implements AdapterView.OnItemSelectedListener {
    private Context context;
    private View alertView;
    private JSONObject json, tempjson;
    private LayoutInflater mInflater;
    private ItemAdapter itemAdapter;
    private ToggleButton toggleScan, toggleFly;
    private LinearLayout moveSelector, metersSelector, degreesSelector, sideSelector, marginAndIniSelector, widthAndMaxSelector;
    private EditText degreesNumber, metersNumber, marginNumber, iniHeightNumber, widthNumber, maxHeightNumber;
    private Spinner moveSpinner, sideSpinner;
    private ArrayAdapter<CharSequence> moveSpinnerAdapter, sideSpinnerAdapter;
    private AlertDialog dialog;
    private Button buttonAdd;
    private boolean blankDialog;
    private int pos;

    EditDialogCreator(Context c, JSONObject j, ItemAdapter ia, boolean bd) {
        context = c;
        json = j;
        itemAdapter = ia;
        blankDialog = bd;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    EditDialogCreator(Context c, JSONObject j, ItemAdapter ia, boolean bd, int position) {
        context = c;
        json = j;
        itemAdapter = ia;
        blankDialog = bd;
        pos = position;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void setLayoutViews() {
        // Get all elements from a dialog
        toggleScan = alertView.findViewById(R.id.toggleScan);
        toggleFly = alertView.findViewById(R.id.toggleFly);
        moveSelector = alertView.findViewById(R.id.moveSelector);
        metersSelector = alertView.findViewById(R.id.metersSelector);
        degreesSelector = alertView.findViewById(R.id.degreesSelector);
        sideSelector = alertView.findViewById(R.id.sideSelector);
        marginAndIniSelector = alertView.findViewById(R.id.marginAndIniSelector);
        widthAndMaxSelector = alertView.findViewById(R.id.widthAndMaxSelector);
        buttonAdd = alertView.findViewById(R.id.buttonAdd);

        // Get all inputs
        degreesNumber = alertView.findViewById(R.id.degreesNumber);
        metersNumber = alertView.findViewById(R.id.metersNumber);
        marginNumber = alertView.findViewById(R.id.marginNumber);
        iniHeightNumber = alertView.findViewById(R.id.iniHeightNumber);
        widthNumber = alertView.findViewById(R.id.widthNumber);
        maxHeightNumber = alertView.findViewById(R.id.maxHeightNumber);

        // Fill moving spinner
        moveSpinner = alertView.findViewById(R.id.moveSpinner);
        moveSpinnerAdapter = ArrayAdapter.createFromResource(context, R.array.moves_array, android.R.layout.simple_spinner_item);
        moveSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moveSpinner.setAdapter(moveSpinnerAdapter);

        // Fill side spinner
        sideSpinner = alertView.findViewById(R.id.sideSpinner);
        sideSpinnerAdapter = ArrayAdapter.createFromResource(context, R.array.side_array, android.R.layout.simple_spinner_item);
        sideSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sideSpinner.setAdapter(sideSpinnerAdapter);
    }

    private void setUIEvents() {
        // Toggle button "Scan" event
        toggleScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    toggleFly.setChecked(false);

                    // Delete all flying selectors
                    moveSelector.setVisibility(View.GONE);
                    metersSelector.setVisibility(View.GONE);
                    degreesSelector.setVisibility(View.GONE);

                    // Set scan selectors visible
                    sideSelector.setVisibility(View.VISIBLE);
                    marginAndIniSelector.setVisibility(View.VISIBLE);
                    widthAndMaxSelector.setVisibility(View.VISIBLE);

                }
            }
        });

        // Toggle button "Fly" event
        toggleFly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    toggleScan.setChecked(false);

                    // Delete all scan selectors
                    sideSelector.setVisibility(View.GONE);
                    marginAndIniSelector.setVisibility(View.GONE);
                    widthAndMaxSelector.setVisibility(View.GONE);

                    // Set flying selectors visible
                    moveSelector.setVisibility(View.VISIBLE);
                    if (moveSpinner.getSelectedItem().toString().indexOf('y') == 0) {
                        metersSelector.setVisibility(View.GONE);
                        degreesSelector.setVisibility(View.VISIBLE);
                    } else {
                        metersSelector.setVisibility(View.VISIBLE);
                        degreesSelector.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void setSubmitEvent(final boolean editMode) {
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleFly.isChecked()) {

                    // Create JSON object and put in into JSON
                    tempjson = new JSONObject();
                    try {
                        tempjson.put("name", "Fly");
                        tempjson.put("direction", moveSpinner.getSelectedItem().toString());
                        if (moveSpinner.getSelectedItem().toString().indexOf('y') == 0) {
                            // Check if user forgot to fill input
                            if (degreesNumber.getText().length() == 0) {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            tempjson.put("metOrDeg", degreesNumber.getText());
                        } else {
                            // Check if user forgot to fill input
                            if (degreesNumber.getText().length() == 0 && metersNumber.getText().length() == 0) {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            tempjson.put("metOrDeg", metersNumber.getText());
                        }

                        // Add changes to JSON and save it
                        if (editMode) {
                            json.getJSONArray("actions").put(pos, tempjson);
                        } else {
                            json.getJSONArray("actions").put(tempjson);
                        }
                        saveJSONChanges();

                        // Close dialog
                        dialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (toggleScan.isChecked()) {

                    // Check if user fill everything
                    if (marginNumber.getText().length() == 0 || iniHeightNumber.getText().length() == 0
                            || widthNumber.getText().length() == 0 || maxHeightNumber.getText().length() == 0) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    tempjson = new JSONObject();
                    try {
                        tempjson.put("name", "Scan");
                        tempjson.put("side", sideSpinner.getSelectedItem().toString());
                        tempjson.put("iniHeight", iniHeightNumber.getText());
                        tempjson.put("maxHeight", maxHeightNumber.getText());
                        tempjson.put("width", widthNumber.getText());
                        tempjson.put("margin", marginNumber.getText());

                        // Add changes to JSON and save it
                        if (editMode) {
                            json.getJSONArray("actions").put(pos, tempjson);
                        } else {
                            json.getJSONArray("actions").put(tempjson);
                        }
                        saveJSONChanges();

                        // Close dialog
                        dialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "Please choose ACTION", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fillData() {
        try {
            JSONObject jsonElem = json.getJSONArray("actions").getJSONObject(pos);
            String name = jsonElem.getString("name");
            if (name.equals("Fly")) {
                toggleFly.setChecked(true);
                moveSpinner.setSelection(moveSpinnerAdapter.getPosition(jsonElem.getString("direction")));
                if (jsonElem.getString("direction").indexOf('y') == 0) {
                    degreesNumber.setText(jsonElem.getString("metOrDeg"));
                } else {
                    metersNumber.setText(jsonElem.getString("metOrDeg"));
                }
            } else {
                toggleScan.setChecked(true);
                sideSpinner.setSelection(sideSpinnerAdapter.getPosition(jsonElem.getString("side")));
                marginNumber.setText(jsonElem.getString("margin"));
                widthNumber.setText(jsonElem.getString("width"));
                iniHeightNumber.setText(jsonElem.getString("iniHeight"));
                maxHeightNumber.setText(jsonElem.getString("maxHeight"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void createDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        alertView = mInflater.inflate(R.layout.edit_list_item, null);

        setLayoutViews();

        // Set on item select listener for Spinner
        moveSpinner.setOnItemSelectedListener(EditDialogCreator.this);

        setUIEvents();

        if (!blankDialog) {
            fillData();
            setSubmitEvent(true);
        } else {
            setSubmitEvent(false);
        }

        mBuilder.setView(alertView);
        dialog = mBuilder.create();
        dialog.show();

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        LinearLayout metersSelector = alertView.findViewById(R.id.metersSelector);
        LinearLayout degreesSelector = alertView.findViewById(R.id.degreesSelector);

        if (i > 5) {
            metersSelector.setVisibility(View.GONE);
            degreesSelector.setVisibility(View.VISIBLE);
        } else {
            metersSelector.setVisibility(View.VISIBLE);
            degreesSelector.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void saveJSONChanges() {
        try {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("FlyingJSON", json.toString()).apply();
            itemAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d("TestJson", "" + e);
        }
    }
}
