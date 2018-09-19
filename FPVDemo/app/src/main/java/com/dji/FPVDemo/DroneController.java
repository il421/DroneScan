package com.dji.FPVDemo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;

public class DroneController extends AppCompatActivity {

    private Aircraft aircraft;
    private FlightController flightController;
//    private FlightAssistant flightAssistant;
    private TextView logsTxtView;
    private CommonCallbacks.CompletionCallback actionCompletion;
    private Timer sendVirtualStickDataTimer;
    private SendVirtualStickDataTask sendVirtualStickDataTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controller);
        getAircraftData();
        startBasicMission();
    }

    private void getAircraftData() {
        aircraft = new Aircraft(null);
        flightController = aircraft.getFlightController();
//        flightAssistant = flightController.getFlightAssistant();
    }

    private void startBasicMission() {
        logsTxtView = (TextView) findViewById(R.id.logs_textview);
        FlightControlData flightControlData = new FlightControlData(0, 0, 1, 0);
        if(flightController != null) {
            logsTxtView.setText("flight controller not null");
            flightController.startTakeoff(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    showToast("in on result");
                    if(djiError == null) {
                        showToast("takeoff successful");
                        // YAW CODE HERE
                        initializeFlightCtrlr();
                    } else {
                        showToast("takeoff failed " + djiError.getDescription());
                    }
                }
            });
        } else {
            logsTxtView.setText("flight controller null");
        }

    }

    private void initializeFlightCtrlr(){
//        Aircraft aircraft = DJISimulatorApplication.getAircraftInstance();

//        if (aircraft == null || !aircraft.isConnected()) {
//            showToast("Disconnected");
//            flightController = null;
//            return;
//        } else {
            flightController = aircraft.getFlightController();
            flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
            flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
            flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
            flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);
//        }
        if (sendVirtualStickDataTimer == null) {
            sendVirtualStickDataTask = new SendVirtualStickDataTask();
            sendVirtualStickDataTimer = new Timer();
            sendVirtualStickDataTimer.schedule(sendVirtualStickDataTask, 0, 200);
        }
    }

    class SendVirtualStickDataTask extends TimerTask {
        @Override
        public void run() {
            if (flightController != null) {
                flightController.sendVirtualStickFlightControlData(
                        new FlightControlData(
                                0, 0, 2, 0
                        ), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                            }
                        }
                );
            }
        }
    }

    private void showToast(final String toastMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();

            }
        });
    }
}
