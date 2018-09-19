package com.dji.FPVDemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class ScanningResults extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        ArrayList<String> listOfBarcodes = intent.getStringArrayListExtra("barcode");


        TableLayout resultTable = findViewById(R.id.barcode_result);


        for (int i = 0; i < listOfBarcodes.size(); i++) {

            TableRow resultRow = new TableRow(this);

            TextView resultText = new TextView(this);
            resultText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
            resultText.setTextColor(Color.BLACK);

            resultText.setText("Barcode #" + (i + 1) + " " + listOfBarcodes.get(i));

            resultTable.addView(resultRow);
            resultRow.addView(resultText);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        Intent intent = new Intent(this, ConnectionActivity.class);
        startActivity(intent);
    }
}
