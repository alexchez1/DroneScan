package com.dji.DroneScan;

import android.os.CountDownTimer;

import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

class MNavigationHelper {
    private Aircraft aircraft = new Aircraft(null);
    private FlightController flightController = aircraft.getFlightController();
    private FlightAssistant flightAssistant = flightController.getFlightAssistant();
    private Stack<String> startStack = new Stack<>();
    private Stack<Float> flightMoves = new Stack<>();

    MNavigationHelper() {}

    MNavigationHelper(String[] moves, float speed) {

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

        float meters = 0;
        float millisec;
        float yawSpeed = 45f;
        float upDownSpeed = 1f;
        String data;
        int direction = 0;
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m;

        this.startStack.addAll(Arrays.asList(moves));

        while (!startStack.empty()) {
            data = startStack.pop();

            if (getPattern("[f]", data)) {
                direction = 1;
            } else if (getPattern("[b]", data)) {
                direction = 2;
            } else if (getPattern("[r]", data)) {
                direction = 3;
            } else if (getPattern("[l]", data)) {
                direction = 4;
            } else if (getPattern("[y][z]", data)) {
                direction = 5;
            } else if (getPattern("[y][c]", data)) {
                direction = 6;
            } else if (getPattern("[u]", data)) {
                direction = 7;
            } else if (getPattern("[d]", data)) {
                direction = 8;
            }

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

            m = p.matcher(data);
            while (m.find()) {
                meters = Float.parseFloat(m.group());
            }
            millisec = (meters / speed) * 1000 + 100;

            if (direction != 5 && direction != 6) {
                this.flightMoves.push(millisec);
            } else {
                this.flightMoves.push((meters / yawSpeed) * 1000 + 100);
            }
        }

        startFly();
    }


    public void startFly() {

        if (!flightMoves.empty()) {
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
                    startFly();
                }
            }.start();
        } else {
            return;
        }
    }

    public boolean getPattern(String pattern, String where) {
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(where);
        if (matcher.find()) {
            return true;
        }
        return false;
    }
}
