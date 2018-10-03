package com.dji.FPVDemo;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.ObstacleDetectionSector;
import dji.common.flightcontroller.VisionDetectionState;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;

class NavigationExecutor implements Runnable {
    static private Aircraft aircraft = new Aircraft(null);
    static private FlightController flightController = aircraft.getFlightController();
    static private FlightAssistant flightAssistant = flightController.getFlightAssistant();
    static private Stack<String> startStack = new Stack<>();
    static private Stack<Float> flightMoves = new Stack<>();
    static private Stack<String> commands = new Stack<>();

    private float upDownSpeed = 0.2f;
    private float yawSpeed = 45f;

    NavigationExecutor() {
    }

    NavigationExecutor(String[] moves, float speed) {

        // Set Virtual Sticks in case they wasn't before
        flightController.setVirtualStickModeEnabled(true, null);
        flightController.setVirtualStickAdvancedModeEnabled(true);

        //Set modes for flight controller
        flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
        flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
        flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
        flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);

        // Turn on Vision Assisted if it was OFF
        flightAssistant.getVisionAssistedPositioningEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (!aBoolean) {
                    flightAssistant.setVisionAssistedPositioningEnabled(true, null);
                }
            }

            @Override
            public void onFailure(DJIError djiError) {
            }
        });

        // Turn off Collision avoidance if it was ON
        flightAssistant.getCollisionAvoidanceEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean) {
                    flightAssistant.setCollisionAvoidanceEnabled(false, null);
                }
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });


        String data;
        int command = 0;

        startStack.addAll(Arrays.asList(moves));

        while (!startStack.empty()) {
            try {
                data = startStack.pop();

//                Log.d("TestHelper", "Data: " + data);

                if (getPattern("[f][o][r][w][a][r][d]", data)) {
                    command = 1;
                } else if (getPattern("[b][a][c][k]", data)) {
                    command = 2;
                } else if (getPattern("[r][i][g][h][t]", data)) {
                    command = 3;
                } else if (getPattern("[l][e][f][t]", data)) {
                    command = 4;
                } else if (getPattern("[y][a][w][R]", data)) {
                    command = 5;
                } else if (getPattern("[y][a][w][L]", data)) {
                    command = 6;
                } else if (getPattern("[u][p]", data)) {
                    command = 7;
                } else if (getPattern("[d][o][w][n]", data)) {
                    command = 8;
                } else if (getPattern("[t][a][k][e][o][f][f]", data)) {
                    command = 9;
                } else if (getPattern("[a][l][i][g][n]", data)) {
                    command = 10;
                } else if (getPattern("[s][c][a][n]", data)) {
                    command = 11;
                } else if (getPattern("[c][o][r][n][e][r][C][l][o][s][e][r]", data)) {
                    commands.push(data.substring(12));
                    command = 12;
                } else if (getPattern("[h][e][i][g][h][t]", data)) {
                    commands.push(data.substring(6));
                    command = 13;
                } else if (getPattern("[l][a][n][d]", data)) {
                    command = 14;
                } else if (getPattern("[c][o][r][n][e][r][B][a][c][k]", data)) {
                    commands.push(data.substring(10));
                    command = 15;
                }

                if (command != 11) {
                    if (command <= 8) {
                        pushSpeedInFlightMoves(command, speed);
                        pushSecondsInFlightMoves(data, speed, command);
                    }
                    pushFlightCodeInCommands(command);
                } else if (command == 11) {
                    fetchScanIntoCommands(data);
                }
            } catch (Exception e) {
                Log.d("TestHelper", "Exception: " + e);
            }
        }
        Log.d("TestHelper", "Flight moves: " + flightMoves);
    }

    public void run() {
        try {
            runCommands();
        } catch (Exception e) {
            Log.d("TestHelper", "Run command exception: " + e);
        }

    }

    private void runCommands() {

//        Log.d("TestHelper", "Current running command: " + commands.peek());

        if (commands.empty()) {
            return;
        } else if (commands.peek().equals("flight")) {
            flight();
        } else if (commands.peek().equals("takeoff")) {
            takeOff();
        } else if (commands.peek().equals("align")) {
            align();
        } else if (commands.peek().equals("cornerCloser")) {
            findCornerAndGetCloser();
        } else if (commands.peek().equals("cornerBack")) {
            findCornerAndGetBack();
        } else if (commands.peek().equals("reachHeight")) {
            reachHeight();
        } else if (commands.peek().equals("land")) {
            land();
        }
    }

    private void pushSpeedInFlightMoves(int direction, float speed) {
        switch (direction) {
            case 1:
                flightMoves.push(0f);
                flightMoves.push(0f);
                flightMoves.push(speed);
                flightMoves.push(0f);
                break;
            case 2:
                flightMoves.push(0f);
                flightMoves.push(0f);
                flightMoves.push(-speed);
                flightMoves.push(0f);
                break;
            case 3:
                flightMoves.push(0f);
                flightMoves.push(0f);
                flightMoves.push(0f);
                flightMoves.push(speed);
                break;
            case 4:
                flightMoves.push(0f);
                flightMoves.push(0f);
                flightMoves.push(0f);
                flightMoves.push(-speed);
                break;
            case 5:
                flightMoves.push(0f);
                flightMoves.push(yawSpeed);
                flightMoves.push(0f);
                flightMoves.push(0f);
                break;
            case 6:
                flightMoves.push(0f);
                flightMoves.push(-yawSpeed);
                flightMoves.push(0f);
                flightMoves.push(0f);
                break;
            case 7:
                flightMoves.push(upDownSpeed);
                flightMoves.push(0f);
                flightMoves.push(0f);
                flightMoves.push(0f);
                break;
            case 8:
                flightMoves.push(-upDownSpeed);
                flightMoves.push(0f);
                flightMoves.push(0f);
                flightMoves.push(0f);
                break;
        }
    }

    private void pushSecondsInFlightMoves(String initialMoveName, float speed, int command) {
        float meters = 0;
        float millisec;

        if (command <= 8) {
            Pattern p = Pattern.compile("[0-9]*\\.?[0-9]+");
            Matcher m = p.matcher(initialMoveName);
            if (m.find()) {
                meters = Float.parseFloat(m.group());
            } else {
                p = Pattern.compile("\\d*");
                m = p.matcher(initialMoveName);
                if (m.find()) {
                    meters = Float.parseFloat(m.group());
                }
            }

            millisec = (meters / speed) * 1000 + 100;

            if (command == 5 || command == 6) {
                flightMoves.push((meters / yawSpeed) * 1000 + 100);
            } else if (command == 7 || command == 8) {
                flightMoves.push((meters / upDownSpeed) * 1000 + 100);
            } else {
                flightMoves.push(millisec);
            }
        }
    }

    private void pushFlightCodeInCommands(int command) {
        if (command <= 8) {
            commands.push("flight");
        } else if (command == 9) {
            commands.push("takeoff");
        } else if (command == 10) {
            commands.push("align");
        } else if (command == 12) {
            commands.push("cornerCloser");
        } else if (command == 13) {
            commands.push("reachHeight");
        } else if (command == 14) {
            commands.push("land");
        } else if (command == 15) {
            commands.push("cornerBack");
        }
    }

    private void fetchScanIntoCommands(String initialMoveName) {

        float width = 0, initialHeight = 0, maxHeight = 0;
        float step = 0.2f;
        float speed = 0.3f;
        int counter;
        String side = initialMoveName.substring(4, 5);
        boolean isMoveRight;

        // Find width, initialHeight and maxHeight
        Pattern p = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher m = p.matcher(initialMoveName);

        if (m.find()) {
            width = Float.parseFloat(m.group(0));
        }

        if (m.find()) {
            initialHeight = Float.parseFloat(m.group(0));
        }

        if (m.find()) {
            maxHeight = Float.parseFloat(m.group(0));
        }

        counter = (int) Math.ceil((maxHeight - initialHeight) / step) + 1;
        isMoveRight = (side.equals("L") && counter % 2 != 0) || (side.equals("R") && counter % 2 == 0)? true : false;

        for (int i = 0; i < counter; i++) {
            // Check if move right - push right movement into stack
            if (isMoveRight) {
                pushSpeedInFlightMoves(3, speed);
                pushSecondsInFlightMoves(width + "", speed, 3);
            } else {
                pushSpeedInFlightMoves(4, speed);
                pushSecondsInFlightMoves(width + "", speed, 4);
            }
            pushFlightCodeInCommands(3);

            if (i < counter - 1) {
                // Make 1 step up
                pushSpeedInFlightMoves(7, speed);
                pushSecondsInFlightMoves(step + "", speed, 7);
                pushFlightCodeInCommands(3);
            }

            // Change direction
            isMoveRight = !isMoveRight;
        }

//        Log.d("TestHelper", "Side L: " + side.equals("L") + " Counter even: " + (counter % 2 == 0));
//        Log.d("TestHelper", "Side: " + side + " counter: " + counter + " First move right: " + isMoveRight);

    }

    private void flight() {
        // Pop value to delete it from stack
        commands.pop();

        int millisec = flightMoves.pop().intValue();
        final float v1, v2, v3, v4;
        v1 = flightMoves.pop();
        v2 = flightMoves.pop();
        v3 = flightMoves.pop();
        v4 = flightMoves.pop();

//        Log.d("TestHelper", "Before timer. millisec: " + millisec + " velocity " + v1 + " " + v2 + " " + v3 + " " + v4);

        new CountDownTimer(millisec, 100) {
            @Override
            public void onTick(long l) {
                flightController.sendVirtualStickFlightControlData(new FlightControlData(v1, v2, v3, v4), null);
            }

            @Override
            public void onFinish() {
                flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, 0), null);
                runCommands();
            }
        }.start();

    }

    private void takeOff() {
        // Pop value to delete it from stack
        commands.pop();

        // Take off action
        flightController.startTakeoff(null);


        // Small step forward after takeoff in order to align drone
        new CountDownTimer(5000, 5000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                runCommands();
            }
        }.start();
    }

    private void land() {
        // Pop one element from stack
        commands.pop();

        // Land and on result give control back to Executor
        flightController.startLanding(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                new Handler(Looper.getMainLooper()).post(new NavigationExecutor());
            }
        });
    }

    private void align() {
        Log.d("TestHelper", "Align works");
        // Pop value to delete it from stack
        commands.pop();

        // Set error level for alignment
        final int err = 100;

        // Get sensors to align drone
        flightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
            @Override
            public void onUpdate(@NonNull VisionDetectionState visionDetectionState) {
                ObstacleDetectionSector[] od = visionDetectionState.getDetectionSectors();

                // Get first and last sensor values
                float sen1 = od[0].getObstacleDistanceInMeters();
                float sen2 = od[3].getObstacleDistanceInMeters();
                int difference;

                // Calculate difference between values
                if (sen1 > sen2) {
                    difference = (int) Math.abs((sen2 / sen1) * 100 - 100);
                } else {
                    difference = (int) Math.abs((sen1 / sen2) * 100 - 100);
                }

                // Yaw right or left if difference is too big or give control back to main function
                if (difference > err) {
                    if (sen1 > sen2) {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 5, 0), null);
                    } else {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, -5, 0), null);
                    }
                } else {
                    flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, 0), null);
                    flightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
                        @Override
                        public void onUpdate(@NonNull VisionDetectionState visionDetectionState) {
                        }
                    });
                    Log.d("TestHelper", "Align stop working");
                    new Handler(Looper.getMainLooper()).post(new NavigationExecutor());
                }
            }
        });
    }

    private void findCornerAndGetCloser() {
        commands.pop();

        final int error = 30;
        final float speed;
        final String side = commands.pop();

        if (side.equals("L")) {
            speed = -0.1f;
        } else {
            speed = 0.1f;
        }

        flightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
            @Override
            public void onUpdate(@NonNull VisionDetectionState visionDetectionState) {
                ObstacleDetectionSector[] od = visionDetectionState.getDetectionSectors();

                float sen1 = od[0].getObstacleDistanceInMeters();
                float sen2 = od[3].getObstacleDistanceInMeters();
                int difference;

                if (side.equals("L")) {
                    difference = Math.abs((int) (sen1 / sen2 * 100 - 100));
                } else {
                    difference = Math.abs((int) (sen2 / sen1 * 100 - 100));
                }

                if (difference < error) {
                    flightController.sendVirtualStickFlightControlData(new FlightControlData(speed, 0, 0, 0), null);
                } else {
                    flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, 0), null);
                    flightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
                        @Override
                        public void onUpdate(@NonNull VisionDetectionState visionDetectionState) {
                            ObstacleDetectionSector[] od = visionDetectionState.getDetectionSectors();

                            float sen1;
                            float sen2;
                            float sen3;

                            if (side.equals("L")) {
                                sen1 = od[1].getObstacleDistanceInMeters();
                                sen2 = od[2].getObstacleDistanceInMeters();
                                sen3 = od[3].getObstacleDistanceInMeters();
                            } else {
                                sen1 = od[0].getObstacleDistanceInMeters();
                                sen2 = od[1].getObstacleDistanceInMeters();
                                sen3 = od[2].getObstacleDistanceInMeters();
                            }

                            if (sen1 > 1.5f || sen2 > 1.5f || sen3 > 1.5f) {
                                flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0.15f, 0, 0), null);
                            } else {
                                flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, 0), null);
                                flightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
                                    @Override
                                    public void onUpdate(@NonNull VisionDetectionState visionDetectionState) {
                                    }
                                });
                                new Handler(Looper.getMainLooper()).post(new NavigationExecutor());
                            }
                        }
                    });
                }
            }
        });
    }

    private void findCornerAndGetBack() {
        // Pop redundant command from array
        commands.pop();

        // Set variables
        final int error = 30;
        /* Error shows what the difference between sensors
         we are looking for when searching a corner (e.g. 1 sensor can show 2 metres and
         we are looking when another one will show 2.6 to understand that we have found a corner) */
        final float speed;
        final String side = commands.pop();

        // Depends of from what side we are looking for corner using different direction
        if (side.equals("L")) {
            speed = -0.1f;
        } else {
            speed = 0.1f;
        }

        // Enable front sensors of the drone and start collect information
        flightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
            @Override
            public void onUpdate(@NonNull VisionDetectionState visionDetectionState) {
                ObstacleDetectionSector[] od = visionDetectionState.getDetectionSectors();

                // Use only 2 sensors out of 4 (the last and the first)
                float sen1 = od[0].getObstacleDistanceInMeters();
                float sen2 = od[3].getObstacleDistanceInMeters();
                int difference;

                if (side.equals("L")) {
                    difference = Math.abs((int) (sen1 / sen2 * 100 - 100));
                } else {
                    difference = Math.abs((int) (sen2 / sen1 * 100 - 100));
                }

                if (difference < error) {
                    flightController.sendVirtualStickFlightControlData(new FlightControlData(speed, 0, 0, 0), null);
                } else {
                    flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, 0), null);
                    flightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
                        @Override
                        public void onUpdate(@NonNull VisionDetectionState visionDetectionState) {
                            ObstacleDetectionSector[] od = visionDetectionState.getDetectionSectors();

                            float sen1;
                            float sen2;
                            float sen3;

                            if (side.equals("L")) {
                                sen1 = od[1].getObstacleDistanceInMeters();
                                sen2 = od[2].getObstacleDistanceInMeters();
                                sen3 = od[3].getObstacleDistanceInMeters();
                            } else {
                                sen1 = od[0].getObstacleDistanceInMeters();
                                sen2 = od[1].getObstacleDistanceInMeters();
                                sen3 = od[2].getObstacleDistanceInMeters();
                            }

                            if (sen1 < 2 || sen2 < 2 || sen3 < 2) {
                                flightController.sendVirtualStickFlightControlData(new FlightControlData(0, -0.15f, 0, 0), null);
                            } else {
                                flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, 0), null);
                                flightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
                                    @Override
                                    public void onUpdate(@NonNull VisionDetectionState visionDetectionState) {
                                    }
                                });
                                new Handler(Looper.getMainLooper()).post(new NavigationExecutor());
                            }
                        }
                    });
                }
            }
        });
    }

    private void reachHeight() {
        // Pop redundant command from array
        commands.pop();

        // Get desirable height from array
        final float desireHeight = Float.parseFloat(commands.pop());

        // Get sensor, which show current heigh
        flightController.setStateCallback(new FlightControllerState.Callback() {
            @Override
            public void onUpdate(@NonNull FlightControllerState flightControllerState) {
                // Get current height from the sensor
                float realHeight = flightControllerState.getUltrasonicHeightInMeters();

                // If current height is less or more, than desirable go up/down
                if (realHeight < desireHeight) {
                    flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, upDownSpeed), null);
                } else if (realHeight > desireHeight) {
                    flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 0, -upDownSpeed), null);
                } else {
                    flightController.setStateCallback(new FlightControllerState.Callback() {
                        @Override
                        public void onUpdate(@NonNull FlightControllerState flightControllerState) {
                        }
                    });
                    // After height is the same as desirable - give control to main function
                    new Handler(Looper.getMainLooper()).post(new NavigationExecutor());
                }
            }
        });
    }

    private boolean getPattern(String pattern, String where) {
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(where);
        if (matcher.find()) {
            return true;
        }
        return false;
    }


}
