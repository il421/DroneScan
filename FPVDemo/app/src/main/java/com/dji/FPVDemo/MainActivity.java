package com.dji.FPVDemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dji.common.camera.FocusAssistantSettings;
import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.product.Model;
import dji.common.remotecontroller.HardwareState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;

public class MainActivity extends Activity implements SurfaceTextureListener,OnClickListener{

    private int cameraMode;
    private static final String TAG = MainActivity.class.getName();


    protected VideoFeeder.VideoDataCallback mReceivedVideoDataCallBack = null;
    protected DJICodecManager mCodecManager = null;
    protected TextureView mVideoSurface = null;
    protected Aircraft aircraft = new Aircraft(null);
    protected FlightController flightController;
    protected Camera camera = FPVDemoApplication.getCameraInstance();

    Frame frame;
    ExecutorService barcodeThread;

    int SHUTTER_CLICK;

    protected String valueOfISO;
    protected String valueOfAperture;

    BarcodeDetector barcodeDetector;
    ArrayList<String> listOfBarcodes = new ArrayList<>();
    MediaActionSound sound = new MediaActionSound();

    Thread zoomThread = new Thread(new Runnable() {
        PointF point = new PointF(0.5f, 0.5f);

        void zoomFocus() {
            camera.setFocusTarget(point, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    zoomFocus();
                }
            });
        }
        @Override
        public void run() {
            zoomFocus();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // DEFINE THE CAMERA MODE (0 - settings, 1 - scanning)
        Intent intentCameraMode = getIntent();
        cameraMode = intentCameraMode.getIntExtra("cameraMode", 1);

        // SELECT AN ACTIVITY
        if (cameraMode == 0) {
            setContentView(R.layout.activity_camera);
        } else {
            setContentView(R.layout.activity_main);
        }

        // The callback for receiving the raw H264 video data for camera live view
        mReceivedVideoDataCallBack = new VideoFeeder.VideoDataCallback() {

            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                if (mCodecManager != null) {
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
            }
        };

        if (cameraMode == 0) {
            // WATCHING ISO CHANGES
            RadioGroup groupISO = findViewById(R.id.radioButtons_ISO);
            groupISO.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    RadioButton checkedISO =  radioGroup.findViewById(i);
                    valueOfISO = checkedISO.getText().toString();
                }
            });

            // WATCHING APERTURE CHANGES
            RadioGroup groupAperture = findViewById(R.id.radioButtons_aperture);
            groupAperture.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    RadioButton checkedAperture =  radioGroup.findViewById(i);
                    valueOfAperture = checkedAperture.getText().toString();
                }
            });
        } else {
            // GET CUSTOM'S SETTINGS FROM CameraSettings
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String myAperture = sharedPref.getString("Aperture", "Not Available");
            String myISO = sharedPref.getString("ISO", "Not Available");

            // CAMERA SETTING
            camera.setMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO, null);
            camera.setExposureMode(SettingsDefinitions.ExposureMode.MANUAL, null);
            camera.setAperture(SettingsDefinitions.Aperture.valueOf(myAperture), null);
            camera.setISO(SettingsDefinitions.ISO.valueOf(myISO), null);
            camera.setFocusAssistantSettings(new FocusAssistantSettings(true, true), null);
            camera.setFocusMode(SettingsDefinitions.FocusMode.AUTO, null);

            // CHECK CAMERA SETTINGS
//            camera.getAperture(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.Aperture>() {
//                @Override
//                public void onSuccess(SettingsDefinitions.Aperture aperture) {
//                    showToast(aperture + " ");
//                }
//
//                @Override
//                public void onFailure(DJIError djiError) {}
//            });
//            camera.getISO(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ISO>() {
//                @Override
//                public void onSuccess(SettingsDefinitions.ISO iso) {
//                    showToast(iso + " ");
//                }
//
//                @Override
//                public void onFailure(DJIError djiError) {}
//            });
//            camera.getMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.CameraMode>() {
//                @Override
//                public void onSuccess(SettingsDefinitions.CameraMode cameraMode) {
//                    showToast(cameraMode + " ");
//                }
//
//                @Override
//                public void onFailure(DJIError djiError) {}
//            });

            // RUN BAR-CODE DETECTOR AND SETTINGS BARCODE FORMAT
            barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(0).build();
            barcodeThread = Executors.newSingleThreadExecutor();

            // RESULT BTN ENABLE/DISABLE
            final Button resultBtn = findViewById(R.id.scan_res);
            flightController = aircraft.getFlightController();
            flightController.setStateCallback(new FlightControllerState.Callback() {
                @Override
                public void onUpdate(@NonNull FlightControllerState flightControllerState) {
                    if(flightControllerState.isFlying()) {
                        resultBtn.setEnabled(false);
                    } else {
                        resultBtn.setEnabled(true);
                    }
                }
            });
        }

        // ZOOM SETTINGS
        zoomThread.start();

        initUI();
    }

    // SHOW TOAST
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // SELECT ISO
    public void onRadioButtonClickedISO(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.ISO_800:{
                if (checked)
                    camera.setISO(SettingsDefinitions.ISO.ISO_800, null);
//                    showToast("ISO_800");
                break;
            }
            case R.id.ISO_1600:{
                if (checked)
                    camera.setISO(SettingsDefinitions.ISO.ISO_1600, null);
//                    showToast("ISO_1600");
                break;
            }
            case R.id.ISO_3200:{
                if (checked)
                    camera.setISO(SettingsDefinitions.ISO.ISO_3200, null);
//                    showToast("ISO_3200");
                break;
            }
            case R.id.ISO_6400:{
                if (checked)
                    camera.setISO(SettingsDefinitions.ISO.ISO_6400, null);
//                    showToast("ISO_6400");
                break;
            }
            case R.id.ISO_12800:{
                if (checked)
                    camera.setISO(SettingsDefinitions.ISO.ISO_12800, null);
//                    showToast("ISO_12800");
                break;
            }
        }
    }

    // SELECT APERTURE
    public void onRadioButtonClickedAperture(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.F_6_DOT_3:{
                if (checked)
                    camera.setAperture(SettingsDefinitions.Aperture.F_6_DOT_3, null);
                showToast("F_6_DOT_3");
                break;
            }
            case R.id.F_7_DOT_1:{
                if (checked)
                    camera.setAperture(SettingsDefinitions.Aperture.F_7_DOT_1, null);
                showToast("F_7_DOT_1");
                break;
            }
            case R.id.F_8:{
                if (checked)
                    camera.setAperture(SettingsDefinitions.Aperture.F_8, null);
                showToast("F_8");
                break;
            }
            case R.id.F_9:{
                if (checked)
                    camera.setAperture(SettingsDefinitions.Aperture.F_9, null);
                showToast("F_9");
                break;
            }
            case R.id.F_10:{
                if (checked)
                    camera.setAperture(SettingsDefinitions.Aperture.F_10, null);
                showToast("F_10");

                break;
            }
        }
    }

    // SAVE PARAMETERS
//    public void cameraSaveBtn(View view) {
//        if (valueOfAperture != null && valueOfISO != null) {
//            // Create object of SharedPreferences.
//            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//
//            // Get Editor
//            SharedPreferences.Editor editor = sharedPref.edit();
//
//            // Put values
//            editor.putString("ISO", valueOfISO);
//            editor.putString("Aperture", valueOfAperture);
//
//            // Commit editor
//            editor.apply();
//
//            if (sharedPref.contains("ISO") && sharedPref.contains("Aperture")) {
//                showToast("Saved successfully");
//            } else {
//                showToast("Something has happend!");
//            }
//        } else {
//            showToast("Please, select camera settings!");
//        }
//
//    }

    // CREATE INTENT AND SHOW RESULTS
    public void getResults(View v) {
        Intent intentResults = new Intent(this, ScanningResults.class);
        overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);

        intentResults.putStringArrayListExtra("barcode", listOfBarcodes);
        startActivity(intentResults);
        finish();
    }

    // BARCODE DETECTION
    private class BarcodeDetectionTimber implements Runnable {

        @Override
        public void run() {
            if (barcodeDetector != null) {
                SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);

                if (barcodes.size() > 0) {
                    for (int i = 0; i < barcodes.size(); i++) {
                        if (listOfBarcodes.indexOf(barcodes.valueAt(i).displayValue) == -1) {
                            listOfBarcodes.add(barcodes.valueAt(i).displayValue);
                            sound.play(SHUTTER_CLICK);
                            showToast("Barcode: " + barcodes.valueAt(i).displayValue);
                            barcodes.clear();
                        }
                    }
                }
            }
        }
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
        mVideoSurface = findViewById(R.id.video_previewer_surface);

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

        if(cameraMode != 0) {
            // GET FRAMES AND RUN BARCODE DETECTION
            Bitmap bitman = mVideoSurface.getBitmap();
            frame = new Frame.Builder().setBitmap(bitman).build();
            barcodeThread.execute(new BarcodeDetectionTimber());
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (cameraMode == 0) {
            if (valueOfAperture != null && valueOfISO != null) {
                // Create object of SharedPreferences.
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

                // Get Editor
                SharedPreferences.Editor editor = sharedPref.edit();

                // Put values
                editor.putString("ISO", valueOfISO);
                editor.putString("Aperture", valueOfAperture);

                // Commit editor
                editor.apply();
            }
        }

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        this.finish();
    }
}
