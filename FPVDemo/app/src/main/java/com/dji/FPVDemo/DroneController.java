package com.dji.FPVDemo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;

public class DroneController extends AppCompatActivity implements CommonCallbacks.CompletionCallback{

    private Aircraft aircraft;
    private FlightController flightController;
//    private FlightAssistant flightAssistant;
    private TextView logsTxtView;
    private CommonCallbacks.CompletionCallback actionCompletion;

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
        logsTxtView.setText("TRALALALA");
//        FlightControlData flightControlData = new FlightControlData(1, 0, 0, 0);
//        flightController.startTakeoff(actionCompletion);
        flightController.startTakeoff(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                logsTxtView.setText("inside startTakeoff");
            }
        });
//        flightController.startTakeoff(null);
//        flightController.

    }

    @Override
    public void onResult(DJIError djiError) {
        logsTxtView.setText("Outside onResult " + actionCompletion.toString());
    }
}
