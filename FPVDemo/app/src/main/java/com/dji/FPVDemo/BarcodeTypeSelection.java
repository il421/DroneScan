package com.dji.FPVDemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class BarcodeTypeSelection extends AppCompatActivity {

    GridView androidGridView;

    String[] gridViewString = {
            "All", "CODE-39", "CODE-128", "QR", "EAN-13", "EAN-8"
    } ;

    int[] gridViewImageId = {
            R.drawable.all, R.drawable.code39,
            R.drawable.code128, R.drawable.qr,
            R.drawable.ean13, R.drawable.ean8
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_barcode_type);

        BarcodeTypeAdapter adapterViewAndroid = new BarcodeTypeAdapter(BarcodeTypeSelection.this, gridViewString, gridViewImageId);
        androidGridView=findViewById(R.id.gridview);
        androidGridView.setAdapter(adapterViewAndroid);

        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                SharedPreferences.Editor editor = getSharedPreferences("BarcodePrefs", MODE_PRIVATE).edit();
                int barcodeNum = 0;
                switch (position) {
                    case 0:
                        barcodeNum = 0;
                        showToast("Barcode: all types");
                        break;
                    case 1:
                        barcodeNum = 2;
                        showToast("Barcode: CODE-39");
                        break;
                    case 2:
                        barcodeNum = 1;
                        showToast("CODE-128");
                        break;
                    case 3:
                        barcodeNum = 256;
                        showToast("Barcode: QR");
                        break;
                    case 4:
                        barcodeNum = 32;
                        showToast("EAN-13");
                        break;
                    case 5:
                        barcodeNum = 64;
                        showToast("EAN-8");
                        break;
                    default:
                        break;
                }
                editor.putInt("barcodeType", barcodeNum);
                editor.apply();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void showToast(final String toastMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();

            }
        });
    }
}
