package com.dji.FPVDemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.dji.scan.qr.camera.CameraSettings;

public class GeneralSettings extends AppCompatActivity implements View.OnClickListener{

    private ImageView imgPathway, imgBarcodeSettings, imgCameraSettings;

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
