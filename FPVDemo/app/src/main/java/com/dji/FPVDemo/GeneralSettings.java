package com.dji.FPVDemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.dji.scan.qr.camera.CameraSettings;

import dji.sdk.base.BaseProduct;

public class GeneralSettings extends AppCompatActivity implements View.OnClickListener{

    private ImageView imgPathway, imgBarcodeSettings, imgCameraSettings;
    private BaseProduct product = FPVDemoApplication.getProductInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initUI();
    }

    private void initUI() {
        imgPathway = findViewById(R.id.img_path);
        imgPathway.setOnClickListener(this);
        imgBarcodeSettings = findViewById(R.id.img_bset);
        imgBarcodeSettings.setOnClickListener(this);
        imgCameraSettings = findViewById(R.id.img_cset);
        imgCameraSettings.setOnClickListener(this);

        if (product == null || !product.isConnected()) {
            imgCameraSettings.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_cset: {
                Intent intentCameraMode  = new Intent(this, MainActivity.class);
                intentCameraMode.putExtra("cameraMode", 0);
                overridePendingTransition(R.anim.slide_to_right, R.anim.slide_to_left);
                startActivity(intentCameraMode);
                break;
            }
            case R.id.img_bset: {
                Intent intent = new Intent(this, BarcodeTypeSelection.class);
                overridePendingTransition(R.anim.slide_to_right, R.anim.slide_to_left);
                startActivity(intent);
                break;
            }
            case R.id.img_path: {

                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
