package com.dji.DroneScan;

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

    private float upDownSpeed = 0.5f;
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

        // Turn on Vision Assisted if it was off
        flightAssistant.getVisionAssistedPositioningEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                flightAssistant.setVisionAssistedPositioningEnabled(true, null);
            }

            @Override
            public void onFailure(DJIError djiError) {
            }
        });

        String data;
        int command = 0;

        startStack.addAll(Arrays.asList(moves));

        while (!startStack.empty()) {
            data = startStack.pop();

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
            } else if (getPattern("[c][o][r][n][e][r]", data)) {
                commands.push(data.substring(6));
                command = 12;
            }

            if (command <= 8) {
                pushSpeedInFlightMoves(command, speed);
                pushSecondsInFlightMoves(data, speed, command);
            }
            pushFlightCodeInCommands(command);
        }
    }

    public void run() {
        Log.d("TestHelper", "Commands: " + commands);
        runCommands();
    }

    private void runCommands() {

//        Log.d("TestHelper", "Running command: " + commands.peek() + " commands array: " + commands);
        if (commands.empty()) {
            return;
        } else if (commands.peek().equals("flight")) {
            flight();
        } else if (commands.peek().equals("takeoff")) {
            takeOff();
        } else if (commands.peek().equals("align")) {
            align();
        } else if (commands.peek().equals("scan")) {
            scan();
        } else if (commands.peek().equals("corner")) {
            findCornerAndGetCloser();
        }
    }

    private void pushSpeedInFlightMoves(int direction, float speed) {
        float yawSpeed = 45f;
        float upDownSpeed = 0.5f;

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

    private void pushSecondsInFlightMoves(String data, float speed, int command) {
        float meters = 0;
        float millisec;

        if (command <= 8) {
            Pattern p = Pattern.compile("[0-9]*\\.?[0-9]+");
            Matcher m = p.matcher(data);
            if (m.find()) {
                meters = Float.parseFloat(m.group());
            } else {
                p = Pattern.compile("\\d*");
                m = p.matcher(data);
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
        } else if (command == 11) {
            commands.push("scan");
        } else if (command == 12) {
            commands.push("corner");
        }
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
        new CountDownTimer(5000, 3000) {
            @Override
            public void onTick(long l) {
                flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 1, 0, 0), null);
            }

            @Override
            public void onFinish() {
                runCommands();
            }
        }.start();
    }

    private void align() {
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
                        flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, 3, 0), null);
                    } else {
                        flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0, -3, 0), null);
                    }
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

    private void scan() {

    }

    private void findCornerAndGetCloser() {
        commands.pop();

        final int error = 50;
        final float speed;
        final String side = commands.pop();

        if (side.equals("L")) {
            speed = -1f;
        } else {
            speed = 1f;
        }

        flightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
            @Override
            public void onUpdate(@NonNull VisionDetectionState visionDetectionState) {
                ObstacleDetectionSector[] od = visionDetectionState.getDetectionSectors();

                final float sen1 = od[0].getObstacleDistanceInMeters();
                final float sen2 = od[3].getObstacleDistanceInMeters();
                int difference;

                if (side.equals("L")) {
                    difference = (int) (sen1 / sen2 * 100 - 100);
                } else {
                    difference = (int) (sen2 / sen1 * 100 - 100);
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

                            if (sen1 > 0.7f || sen2 > 0.7f || sen3 > 0.7f) {
                                flightController.sendVirtualStickFlightControlData(new FlightControlData(0, 0.3f, 0, 0), null);
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
//                    new Handler(Looper.getMainLooper()).post(new NavigationExecutor());
                }
            }
        });
    }

    private void approachRack() {

    }

    // 0.3left10height5width0.5stepheight

    private boolean getPattern(String pattern, String where) {
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(where);
        return matcher.find();
    }


}
