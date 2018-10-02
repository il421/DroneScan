package com.dji.FPVDemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
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

        // Padding in DP
        final float scale = getResources().getDisplayMetrics().density;
        int padding_15dp = (int) (15 * scale + 0.5f);
        int padding_69dp = (int) (69 * scale + 0.5f);
        int padding_186dp = (int) (186 * scale + 0.5f);

        for (int i = 0; i < listOfBarcodes.size(); i++) {

            TableRow resultRow = new TableRow(this);

            TextView resultTitle = new TextView(this);
            TextView resultText = new TextView(this);

            resultTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            resultTitle.setTextColor(getResources().getColor(R.color.colorBlackResults));
            resultTitle.setBackgroundResource(R.drawable.cell_shape);
            resultTitle.setPadding(padding_69dp, padding_15dp, 0, padding_15dp);
            resultTitle.setTypeface(Typeface.create("roboto_light",Typeface.NORMAL));

            resultText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            resultText.setTextColor(getResources().getColor(R.color.colorBlackResults));
            resultText.setBackgroundResource(R.drawable.cell_shape);
            resultText.setPadding(padding_186dp, padding_15dp, 0, padding_15dp);
            resultText.setTypeface(Typeface.create("roboto_light",Typeface.NORMAL));

            resultTitle.setText("Barcode " + (i + 1));
            resultText.setText(listOfBarcodes.get(i));

            resultTable.addView(resultRow);
            resultRow.addView(resultTitle);
            resultRow.addView(resultText);
        }
    }

    public void onGoHome(View v) {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        Intent intent = new Intent(this, ConnectionActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        Intent intent = new Intent(this, ConnectionActivity.class);
        startActivity(intent);
        this.finish();
    }
}
