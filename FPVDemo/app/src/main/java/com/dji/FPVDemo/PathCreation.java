package com.dji.FPVDemo;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class PathCreation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    JSONObject json;
    ItemAdapter itemAdapter;
    View alertView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_creation);

        // TODO: Put this part off code into the MainActivity

        if (!PreferenceManager.getDefaultSharedPreferences(this).contains("FlyingJSON")) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("FlyingJSON", "{\"actions\":[],\"speed\":\"2\",\"height\":\"1.6\"}").apply();
            Log.d("TestJson", "Preference created");
        }

        // ---------------- END ------------------ //

        String jsonText = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("FlyingJSON", null);
        json = null;
        try {
            json = new JSONObject(jsonText);
        } catch (JSONException e) {
            Log.d("TestJson", "" + e);
        }

        ListView mListView = findViewById(R.id.mListView);
        final TextView operatingAttitude = findViewById(R.id.operatingAttitude);
        Button operHeightButton = findViewById(R.id.operHeightButton);

        // Change operation height
        try {
            operatingAttitude.setText(json.getString("height") + "m");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        operHeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(PathCreation.this);
                alertView = getLayoutInflater().inflate(R.layout.change_operating_attitude, null);

                Button setButton = alertView.findViewById(R.id.setButton);
                final EditText operHeight = alertView.findViewById(R.id.operHeight);

                mBuilder.setView(alertView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                setButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (operHeight.getText().length() > 0) {
                            try {
                                json.put("height", operHeight.getText());
                                PreferenceManager.getDefaultSharedPreferences(PathCreation.this).edit().putString("FlyingJSON", json.toString()).apply();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        operatingAttitude.setText(operHeight.getText() + "m");
                                    }
                                });
                                dialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(PathCreation.this, "You need to fill operation height", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditDialogCreator dialogCreator = new EditDialogCreator(PathCreation.this, json, itemAdapter, true);
                dialogCreator.createDialog();
            }
        });

        itemAdapter = new ItemAdapter(this, json);
        mListView.setAdapter(itemAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
