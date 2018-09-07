package com.dji.FPVDemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.CompoundButton;
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
import dji.common.product.Model;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.products.Aircraft;

import static dji.common.camera.SettingsDefinitions.CameraMode.RECORD_VIDEO;
import static dji.common.camera.SettingsDefinitions.ExposureMode.APERTURE_PRIORITY;
import static dji.common.camera.SettingsDefinitions.ExposureMode.MANUAL;
import static dji.common.camera.SettingsDefinitions.ExposureMode.PROGRAM;
import static dji.common.camera.SettingsDefinitions.ISO.ISO_6400;

public class MainActivity extends Activity implements SurfaceTextureListener,OnClickListener{

    private static final String TAG = MainActivity.class.getName();
    protected VideoFeeder.VideoDataCallback mReceivedVideoDataCallBack = null;
    protected DJICodecManager mCodecManager = null;
    protected TextureView mVideoSurface = null;
    protected Frame frame;
    private ExecutorService pool;
    int SHUTTER_CLICK;

//    Camera camera = new Aircraft(null).getCamera();

    Camera camera = FPVDemoApplication.getCameraInstance();
    BarcodeDetector barcodeDetector;
    ArrayList<String> listOfBarcodes = new ArrayList<>();
    MediaActionSound sound = new MediaActionSound();

    Switch mySwitch = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Run BarcodeDetector and define formats... 1 - CODE 128 only, 0 - ALL
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(0).build();
        switchCameraMode(SettingsDefinitions.CameraMode.RECORD_VIDEO);
        camera.setExposureMode(MANUAL, null);

//        mySwitch = findViewById(R.id.mySwitch);
//        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    switchCameraMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);
//                    camera.getMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.CameraMode>() {
//                        @Override
//                        public void onSuccess(SettingsDefinitions.CameraMode cameraMode) {
//                            showToast(cameraMode + " ");
//                        }
//
//                        @Override
//                        public void onFailure(DJIError djiError) {
//
//                        }
//                    });
//
//                } else {
//                    switchCameraMode(SettingsDefinitions.CameraMode.RECORD_VIDEO);
//                    camera.getMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.CameraMode>() {
//                        @Override
//                        public void onSuccess(SettingsDefinitions.CameraMode cameraMode) {
//                            showToast(cameraMode + " ");
//                        }
//
//                        @Override
//                        public void onFailure(DJIError djiError) {
//
//                        }
//                    });
//                    camera.setDigitalZoomFactor(2f, null);
//                    // CHECK ZOOM
//                    camera.getDigitalZoomFactor(new CommonCallbacks.CompletionCallbackWith<Float>() {
//                        @Override
//                        public void onSuccess(Float aFloat) {
//                            showToast(aFloat + " ");
//                        }
//
//                        @Override
//                        public void onFailure(DJIError djiError) {
//
//                        }
//                    });
//                }
//            }
//
//        });

//        camera.setMode(SettingsDefinitions.CameraMode.RECORD_VIDEO, new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError djiError) {
//                if (djiError == null) {
//                    showToast("Switch Camera Mode Succeeded");
//                } else {
//                    showToast(djiError.getDescription());
//                }
//            }
//        });

//        // SET UP AUTO FOCUS
//        camera.setFocusMode(SettingsDefinitions.FocusMode.AUTO, null);
//
//        // SET UP APERTURE
//        camera.setAperture(SettingsDefinitions.Aperture.F_9, null);

        // SET UP ISO
        camera.setISO(ISO_6400, null);

        // SET UP FOCUS ASSISTANT
        camera.setFocusAssistantSettings(new FocusAssistantSettings(true, false), null);

        // SET UP ZOOM
        camera.setDigitalZoomFactor(2f, null);

        // CHECK APERTURE
        camera.getAperture(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.Aperture>() {
            @Override
            public void onSuccess(SettingsDefinitions.Aperture aperture) {
                showToast(aperture + " ");
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });

        // CHECK ISO
        camera.getISO(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ISO>() {
            @Override
            public void onSuccess(SettingsDefinitions.ISO iso) {
                showToast(iso + " ");
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });

        // CHECK CAMERA MODE
        camera.getMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.CameraMode>() {
            @Override
            public void onSuccess(SettingsDefinitions.CameraMode cameraMode) {
                showToast(cameraMode + " ");
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });

        // CHECK ZOOM
        camera.getDigitalZoomFactor(new CommonCallbacks.CompletionCallbackWith<Float>() {
            @Override
            public void onSuccess(Float aFloat) {
                showToast(aFloat + " ");
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });

//        // CHECK RESOLUTION
//        camera.getVideoResolutionAndFrameRate(new CommonCallbacks.CompletionCallbackWith<ResolutionAndFrameRate>() {
//            @Override
//            public void onSuccess(ResolutionAndFrameRate resolutionAndFrameRate) {
//                showToast(resolutionAndFrameRate + " ");
//            }
//
//            @Override
//            public void onFailure(DJIError djiError) {
//
//            }
//        });

//        // CHECK AUTO FOCUS
//        camera.getFocusMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.FocusMode>() {
//            @Override
//            public void onSuccess(SettingsDefinitions.FocusMode focusMode) {
//                showToast(focusMode + "");
//            }
//
//            @Override
//            public void onFailure(DJIError djiError) {}
//        });
//
//        // CHECK AUTO FOCUS
//        camera.getFocusAssistantSettings(new CommonCallbacks.CompletionCallbackWithTwoParam<Boolean, Boolean>() {
//            @Override
//            public void onSuccess(Boolean aBoolean, Boolean aBoolean2) {
//                showToast(aBoolean + " ," + aBoolean2);
//            }
//
//            @Override
//            public void onFailure(DJIError djiError) {
//
//            }
//        });

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

    @Override
    public void onClick(View v) {
    }

    // EXECUTION SERVICE
    private class BarcodeDetectionTimber implements Runnable {
        public void recogniseBarcode(Frame frame) {
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

        @Override
        public void run() {
            this.recogniseBarcode(frame);
        }
    }


    private void switchCameraMode(SettingsDefinitions.CameraMode cameraMode){
        Camera camera = FPVDemoApplication.getCameraInstance();
        if (camera != null) {
            camera.setMode(cameraMode, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                    if (error == null) {
                    } else {
                        showToast(error.getDescription());
                    }
                }
            });
        }
    }
}
