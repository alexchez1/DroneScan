package com.dji.DroneScan;

import android.os.CountDownTimer;
import android.support.annotation.NonNull;

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

class NavigationExecutor {
    private Aircraft aircraft = new Aircraft(null);
    private FlightController flightController = aircraft.getFlightController();
    private FlightAssistant flightAssistant = flightController.getFlightAssistant();
    private Stack<String> startStack = new Stack<>();
    private Stack<Float> flightMoves = new Stack<>();
    private Stack<String> commands = new Stack<>();

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

        this.startStack.addAll(Arrays.asList(moves));

        // takeoff, alignment, find corner, scan near rack, go back and find corner, land

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
            }

            createFlightArray(command, speed);
            pushFlightCodeInArray(data, speed, command);
        }

        runCommands();
    }


    public void runCommands() {

//        Log.d("TestHelper", "FlightMoves: " + flightMoves + ". Commands: " + commands);
        if (commands.empty()) {
            return;
        } else if (commands.peek().equals("flight")) {
            flight();
        } else if (commands.peek().equals("takeoff")) {
            takeOff();
        } else if (commands.peek().equals("align")) {
            align();
        }
    }

    private void createFlightArray(int direction, float speed) {
        float yawSpeed = 45f;
        float upDownSpeed = 1f;

        switch (direction) {
            case 1:
                this.flightMoves.push(0f);
                this.flightMoves.push(0f);
                this.flightMoves.push(speed);
                this.flightMoves.push(0f);
                break;
            case 2:
                this.flightMoves.push(0f);
                this.flightMoves.push(0f);
                this.flightMoves.push(-speed);
                this.flightMoves.push(0f);
                break;
            case 3:
                this.flightMoves.push(0f);
                this.flightMoves.push(0f);
                this.flightMoves.push(0f);
                this.flightMoves.push(speed);
                break;
            case 4:
                this.flightMoves.push(0f);
                this.flightMoves.push(0f);
                this.flightMoves.push(0f);
                this.flightMoves.push(-speed);
                break;
            case 5:
                this.flightMoves.push(0f);
                this.flightMoves.push(yawSpeed);
                this.flightMoves.push(0f);
                this.flightMoves.push(0f);
                break;
            case 6:
                this.flightMoves.push(0f);
                this.flightMoves.push(-yawSpeed);
                this.flightMoves.push(0f);
                this.flightMoves.push(0f);
                break;
            case 7:
                this.flightMoves.push(upDownSpeed);
                this.flightMoves.push(0f);
                this.flightMoves.push(0f);
                this.flightMoves.push(0f);
                break;
            case 8:
                this.flightMoves.push(-upDownSpeed);
                this.flightMoves.push(0f);
                this.flightMoves.push(0f);
                this.flightMoves.push(0f);
                break;
        }
    }

    private void pushFlightCodeInArray(String data, float speed, int command) {
        float meters = 0;
        float millisec;
        float yawSpeed = 45f;

        if (command <= 8) {
            Pattern p = Pattern.compile("-?\\d+");
            Matcher m = p.matcher(data);
            while (m.find()) {
                meters = Float.parseFloat(m.group());
            }

            millisec = (meters / speed) * 1000 + 100;

            if (command != 5 && command != 6) {
                this.flightMoves.push(millisec);
            } else {
                this.flightMoves.push((meters / yawSpeed) * 1000 + 100);
            }

            this.commands.push("flight");
        } else if (command == 9) {
            this.commands.push("takeoff");
        } else if (command == 10) {
            this.commands.push("align");
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
        final int err = 10;

        // Get sensors to align drone
        flightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
            @Override
            public void onUpdate(@NonNull VisionDetectionState visionDetectionState) {
                ObstacleDetectionSector[] od = visionDetectionState.getDetectionSectors();

                // Get first and last sensor values
                float sen1 = od[0].getObstacleDistanceInMeters();
                float sen2 = od[3].getObstacleDistanceInMeters();
                int difference = 0;

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
                    flightAssistant.setVisionDetectionStateUpdatedCallback(new VisionDetectionState.Callback() {
                        @Override
                        public void onUpdate(@NonNull VisionDetectionState visionDetectionState) {}
                    });
                    runCommands();
                }

//                Log.d("TestHelper", "Sensor 1: " + od[0].getObstacleDistanceInMeters() + " sensor 2: " + od[1].getObstacleDistanceInMeters() + " sensor 3: " + od[2].getObstacleDistanceInMeters() + " sensor 4: " + od[3].getObstacleDistanceInMeters());
            }
        });
    }

    private void scan() {

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
