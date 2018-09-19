package com.dji.FPVDemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.Button;
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

public class MainActivity extends Activity implements SurfaceTextureListener,OnClickListener{

    private static final String TAG = MainActivity.class.getName();
    protected VideoFeeder.VideoDataCallback mReceivedVideoDataCallBack = null;
    protected DJICodecManager mCodecManager = null;
    protected TextureView mVideoSurface = null;
    protected Frame frame;
    private ExecutorService barcodeThread;
    private Thread zoomThread;
    int SHUTTER_CLICK;
    private Button resultBtn = findViewById(R.id.scan_res);

    FlightControllerState flightControler;
    Camera camera = FPVDemoApplication.getCameraInstance();
    BarcodeDetector barcodeDetector;
    ArrayList<String> listOfBarcodes = new ArrayList<>();
    MediaActionSound sound = new MediaActionSound();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // The callback for receiving the raw H264 video data for camera live view
        mReceivedVideoDataCallBack = new VideoFeeder.VideoDataCallback() {

            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                if (mCodecManager != null) {
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
            }
        };

        // CAMERA SETTING
        camera.setMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO, null);
        camera.setExposureMode(SettingsDefinitions.ExposureMode.MANUAL, null);
        camera.setAperture(SettingsDefinitions.Aperture.F_9, null);
        camera.setISO(SettingsDefinitions.ISO.ISO_12800, null);
        camera.setFocusAssistantSettings(new FocusAssistantSettings(true, true), null);
        camera.setFocusMode(SettingsDefinitions.FocusMode.AUTO, null);

        zoomThread = new Thread(new Runnable() {
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

        // RUN BAR-CODE DETECTOR AND DEFINE FORMATS (int)
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(0).build();
        barcodeThread = Executors.newSingleThreadExecutor();

        // RESULT BTN ENABLE/DISABLE
        if(flightControler.isFlying()) {
            resultBtn.setEnabled(false);
        } else {
            resultBtn.setEnabled(true);
        }


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

    // CREATE INTENT AND SHOW RESULTS

    public void getResults(View v) {
        Intent intentResults = new Intent(this, ScanningResults.class);
        overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right);

        intentResults.putStringArrayListExtra("barcode", listOfBarcodes);
        startActivity(intentResults);
        finish();
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
//                            sound.play(SHUTTER_CLICK);
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
        if (flightControler.isFlying()) {
            // GET FRAMES AND RUN BARCODE DETECTION
            Bitmap bitman = mVideoSurface.getBitmap();
            frame = new Frame.Builder().setBitmap(bitman).build();
            barcodeThread.execute(new BarcodeDetectionTimber());
        }
    }

    @Override
    public void onClick(View v) {
    }
}
