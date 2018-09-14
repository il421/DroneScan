package com.dji.FPVDemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.media.MediaActionSound;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dji.common.camera.FocusAssistantSettings;
import dji.common.camera.ResolutionAndFrameRate;
import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.product.Model;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.products.Aircraft;

import static dji.common.camera.SettingsDefinitions.CameraMode.RECORD_VIDEO;
import static dji.common.camera.SettingsDefinitions.CameraMode.SHOOT_PHOTO;
import static dji.common.camera.SettingsDefinitions.ExposureMode.APERTURE_PRIORITY;
import static dji.common.camera.SettingsDefinitions.ExposureMode.MANUAL;
import static dji.common.camera.SettingsDefinitions.ExposureMode.PROGRAM;
import static dji.common.camera.SettingsDefinitions.ISO.ISO_12800;
import static dji.common.camera.SettingsDefinitions.ISO.ISO_1600;
import static dji.common.camera.SettingsDefinitions.ISO.ISO_3200;
import static dji.common.camera.SettingsDefinitions.ISO.ISO_6400;
import static dji.common.camera.SettingsDefinitions.ISO.ISO_800;

public class MainActivity extends Activity implements SurfaceTextureListener,OnClickListener{

    private static final String TAG = MainActivity.class.getName();
    protected VideoFeeder.VideoDataCallback mReceivedVideoDataCallBack = null;
    protected DJICodecManager mCodecManager = null;
    protected TextureView mVideoSurface = null;
    protected Frame frame;
    private ExecutorService pool;
    int SHUTTER_CLICK;

    Camera camera = FPVDemoApplication.getCameraInstance();
    BarcodeDetector barcodeDetector;
    ArrayList<String> listOfBarcodes = new ArrayList<>();
    MediaActionSound sound = new MediaActionSound();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RUN BAR-CODE DETECTOR AND DEFINE FORMATS (int)
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(0).build();

        // CAMERA SETTING
        camera.setMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO, null);
        camera.setExposureMode(MANUAL, null);
        camera.setAperture(SettingsDefinitions.Aperture.F_9, null);
        camera.setISO(SettingsDefinitions.ISO.ISO_12800, null);
        camera.setFocusAssistantSettings(new FocusAssistantSettings(true, true), null);
        camera.setFocusMode(SettingsDefinitions.FocusMode.AUTO, null);
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

        zoomThread.start();


        // CHECK CAMERA
        camera.getAperture(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.Aperture>() {
            @Override
            public void onSuccess(SettingsDefinitions.Aperture aperture) {
                showToast(aperture + " ");
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });
        camera.getISO(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ISO>() {
            @Override
            public void onSuccess(SettingsDefinitions.ISO iso) {
                showToast(iso + " ");
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });
        camera.getMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.CameraMode>() {
            @Override
            public void onSuccess(SettingsDefinitions.CameraMode cameraMode) {
                showToast(cameraMode + " ");
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });


        initUI();

        // The callback for receiving the raw H264 video data for camera live view
        mReceivedVideoDataCallBack = new VideoFeeder.VideoDataCallback() {

            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                if (mCodecManager != null) {
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
            }
        };

        pool = Executors.newSingleThreadExecutor();
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
        // GET FRAMES AND RUN BARCODE DETECTION
        Bitmap bitman = mVideoSurface.getBitmap();
        frame = new Frame.Builder().setBitmap(bitman).build();
        pool.execute(new BarcodeDetectionTimber());
    }

    // SHOW TOAST
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

            }
        });
    }

    // CREATE JSON AND SHOW IT
    public void getJSON(View v) {
        String json = new Gson().toJson(listOfBarcodes);

        showToast(json);
    }

     // SET ZOOM VIA FOCUS
    public void setZoom(View v) {
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

        zoomThread.start();
    }

    @Override
    public void onClick(View v) {
    }

    // EXECUTION SERVICE
    private class BarcodeDetectionTimber implements Runnable {

        @Override
        public void run() {
            if (barcodeDetector != null) {
                SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);

                if (barcodes.size() > 0) {
                    System.out.println("Got a barcode!!!");
                    for (int i = 0; i < barcodes.size(); i++) {
                        if (listOfBarcodes.indexOf(barcodes.valueAt(i).displayValue) == -1) {
                            listOfBarcodes.add(barcodes.valueAt(i).displayValue);
                            sound.play(SHUTTER_CLICK);
                            barcodes.clear();
                        }
                    }
                }
            }
        }
    }

//    // SWITCH CAMERA MODE
//    private void switchCameraMode(SettingsDefinitions.CameraMode cameraMode){
//        final Camera camera = FPVDemoApplication.getCameraInstance();
//        if (camera != null) {
//            camera.setMode(cameraMode, new CommonCallbacks.CompletionCallback() {
//                @Override
//                public void onResult(DJIError djiError) {
//                    Log.e(TAG, djiError + " ");
//                }
//            });
//        }
//    }

//    public void onRadioButtonClicked(View view) {
//        // Is the button now checked?
//        boolean checked = ((RadioButton) view).isChecked();
//
//        // Check which radio button was clicked
//        switch (view.getId()) {
//            case R.id.radioButton_8:{
//                if (checked)
//                    camera.setISO(ISO_800, null);
//                break;
//            }
//            case R.id.radioButton_16:{
//                if (checked)
//                    camera.setISO(ISO_1600, null);
//                break;
//            }
//            case R.id.radioButton_32:{
//                if (checked)
//                    camera.setISO(ISO_3200, null);
//                break;
//            }
//            case R.id.radioButton_64:{
//                if (checked)
//                    camera.setISO(ISO_6400, null);
//                break;
//            }
//            case R.id.radioButton_128:{
//                if (checked)
//                    camera.setISO(ISO_12800, null);
//                break;
//            }
//        }
//    }
}
