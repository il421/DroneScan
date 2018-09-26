package com.dji.FPVDemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ManualFlightActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mBtnOpen, btnSetPath, btnSetBarcodeType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        initUI();
    }

    private void initUI() {
        mBtnOpen = (Button) findViewById(R.id.btn_open);
        mBtnOpen.setOnClickListener(this);
        mBtnOpen.setEnabled(false);
        btnSetPath = (Button) findViewById(R.id.btn_set_path);
        btnSetPath.setOnClickListener(this);
        btnSetBarcodeType = (Button) findViewById(R.id.btn_set_barcode);
        btnSetBarcodeType.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_open: {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_set_barcode: {
                Intent intent = new Intent(this, BarcodeTypeSelection.class);
                overridePendingTransition(R.anim.slide_to_right, R.anim.slide_to_left);
                startActivity(intent);
                break;
            }
            case R.id.btn_set_path: {
                Intent intent = new Intent(this, PathDefinerActivity.class);
                overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }
}
