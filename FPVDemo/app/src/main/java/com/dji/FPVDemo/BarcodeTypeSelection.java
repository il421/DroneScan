package com.dji.FPVDemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class BarcodeTypeSelection extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_barcode_type);
        GridView gridview = findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                SharedPreferences.Editor editor = getSharedPreferences("BarcodePrefs", MODE_PRIVATE).edit();
                int barcodeNum = 0;
                switch (position) {
                    case 1:
                        barcodeNum = 2;
                        break;
                    case 2:
                        barcodeNum = 1;
                        break;
                    case 3:
                        barcodeNum = 256;
                        break;
                    case 4:
                        barcodeNum = 32;
                        break;
                    case 5:
                        barcodeNum = 64;
                        break;
                    case 6:
                        barcodeNum = 16;
                        break;
                    default:
                        break;
                }
                editor.putInt("barcodeType", barcodeNum);
                editor.apply();
            }
        });
    }


}
