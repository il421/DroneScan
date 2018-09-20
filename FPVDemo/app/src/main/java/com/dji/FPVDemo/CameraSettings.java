package com.dji.FPVDemo;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import dji.common.camera.SettingsDefinitions;
import dji.common.product.Model;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;

public class CameraSettings extends Activity implements TextureView.SurfaceTextureListener,View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();
    protected VideoFeeder.VideoDataCallback mReceivedVideoDataCallBack = null;
    protected DJICodecManager mCodecManager = null;
    protected TextureView mVideoSurface = null;

    Camera camera = FPVDemoApplication.getCameraInstance();
    ArrayList<String> cameraSettings = new ArrayList<>();
    String valueOfISO;
    String valueOfAperture;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // The callback for receiving the raw H264 video data for camera live view
        mReceivedVideoDataCallBack = new VideoFeeder.VideoDataCallback() {

            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                if (mCodecManager != null) {
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
            }
        };

        initUI();

        RadioGroup groupISO = findViewById(R.id.radioButtons_ISO);
        groupISO.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedISO =  radioGroup.findViewById(i);
                valueOfISO = checkedISO.getText().toString();
            }
        });

        RadioGroup groupAperture = findViewById(R.id.radioButtons_aperture);
        groupAperture.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedAperture =  radioGroup.findViewById(i);
                valueOfAperture = checkedAperture.getText().toString();
            }
        });

    }

    // SHOW TOAST
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(CameraSettings.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onRadioButtonClickedISO(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.ISO_800:{
                if (checked)
                    camera.setISO(SettingsDefinitions.ISO.ISO_800, null);
                break;
            }
            case R.id.ISO_1600:{
                if (checked)
                    camera.setISO(SettingsDefinitions.ISO.ISO_1600, null);
                break;
            }
            case R.id.ISO_3200:{
                if (checked)
                    camera.setISO(SettingsDefinitions.ISO.ISO_3200, null);
                break;
            }
            case R.id.ISO_6400:{
                if (checked)
                    camera.setISO(SettingsDefinitions.ISO.ISO_6400, null);
                break;
            }
            case R.id.ISO_12800:{
                if (checked)
                    camera.setISO(SettingsDefinitions.ISO.ISO_12800, null);
                break;
            }
        }
    }

    public void onRadioButtonClickedAperture(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.F_6_DOT_3:{
                if (checked)
                    camera.setAperture(SettingsDefinitions.Aperture.F_6_DOT_3, null);
                break;
            }
            case R.id.F_7_DOT_1:{
                if (checked)
                    camera.setAperture(SettingsDefinitions.Aperture.F_7_DOT_1, null);
                break;
            }
            case R.id.F_8:{
                if (checked)
                    camera.setAperture(SettingsDefinitions.Aperture.F_8, null);
                break;
            }
            case R.id.F_9:{
                if (checked)
                    camera.setAperture(SettingsDefinitions.Aperture.F_9, null);
                break;
            }
            case R.id.F_10:{
                if (checked)
                    camera.setAperture(SettingsDefinitions.Aperture.F_10, null);
                break;
            }
        }
    }

    public void cameraSaveBtn(View view) {



    }

    protected void onProductChange() {
        initPreviewer();
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        initPreviewer();
        onProductChange();

        if(mVideoSurface == null) {
            Log.e(TAG, "mVideoSurface is null");
        }
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        uninitPreviewer();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    public void onReturn(View view){
        Log.e(TAG, "onReturn");
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        uninitPreviewer();
        super.onDestroy();
    }

    private void initUI() {
        // init mVideoSurface
        mVideoSurface = findViewById(R.id.video_previewer_surface_settings);

        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);
        }
    }

    private void initPreviewer() {

        BaseProduct product = FPVDemoApplication.getProductInstance();

        if (product == null || !product.isConnected()) {
            showToast(getString(R.string.disconnected));
        } else {
            if (null != mVideoSurface) {
                mVideoSurface.setSurfaceTextureListener(this);
            }
            if (!product.getModel().equals(Model.UNKNOWN_AIRCRAFT)) {
                VideoFeeder.getInstance().getPrimaryVideoFeed().setCallback(mReceivedVideoDataCallBack);
            }
        }
    }

    private void uninitPreviewer() {
        Camera camera = FPVDemoApplication.getCameraInstance();
        if (camera != null){
            // Reset the callback
            VideoFeeder.getInstance().getPrimaryVideoFeed().setCallback(null);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureAvailable");
        if (mCodecManager == null) {
            mCodecManager = new DJICodecManager(this, surface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.e(TAG,"onSurfaceTextureDestroyed");
        if (mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onClick(View v) {
    }
}
